package com.pickfords.surveyorapp.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Base64
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.pickfords.surveyorapp.R
import com.pickfords.surveyorapp.interfaces.SyncCallback
import com.pickfords.surveyorapp.model.address.AddressListModel
import com.pickfords.surveyorapp.model.auth.User
import com.pickfords.surveyorapp.model.dashboard.DashboardModel
import com.pickfords.surveyorapp.model.surveyDetails.*
import com.pickfords.surveyorapp.model.syncDatatoServer.*
import com.pickfords.surveyorapp.model.syncDatatoServer.SaveUserRoomSync
import com.pickfords.surveyorapp.model.syncDatatoServer.SyncDataToServer
import com.pickfords.surveyorapp.model.syncDatatoServer.SyncPlanningCommentToServer
import com.pickfords.surveyorapp.network.BaseModel
import com.pickfords.surveyorapp.network.Networking
import com.pickfords.surveyorapp.room.Database
import com.pickfords.surveyorapp.utils.Utility.Companion.getMeasurementType
import com.pickfords.surveyorapp.view.dialogs.SyncDataDialog
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.ByteArrayOutputStream
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import java.util.HashMap


class SyncManager(private val context: Context, private val db: Database, private val session: Session) {

    // For Sync data to server
    private val obj_syncAllData = SyncDataToServer()
    private val obj_syncPartnerAndCommentData = SyncPlanningCommentToServer()
    private val obj_syncInventoryData = SyncInventoryToServer()
    private val surveyInventoryList:  ArrayList<SaveInventorySync> = ArrayList()
    val syncDeleteSurveyDataMain: JsonArray = JsonArray()
    val syncDeleteSurveyData: SetDeleteSurveyDataSync = SetDeleteSurveyDataSync()

    // For delete data in survey.
    var deleteRowId : Int = 0

    companion object {
        const val TAG = "SyncManager"

        const val ENQUIRY = "Enquiry"
        const val ADDRESS_LIST = "AddressList"
        const val SEQUENCE = "Sequence"
        const val LEGS = "Legs"
        const val ROOM_NAME = "RoomName"
        const val SaveADDRESSLEG = "SaveAddressAndLegs"
        const val PLANNING = "Planning"
        const val DELETEPLANNING = "DeletePlanning"
        const val COMMENTS = "Comments"
        const val PICTURE = "Picture"
        const val PARTNER = "Partner"
        /*const val PARTNER_AND_SERVICE = "PartnerAndService"*/
        const val SavePlanningAndComment = "SavePlanningAndComment"
        const val INVENTORY = "Inventory"
        const val SURVEY_INVENTORY = "SurveyInventory"
        const val DELETESURVEYDATA = "DeleteSurveyData"
        const val SIGNATURE = "Signature"
        const val SUBMIT_SURVEY = "SubmitSurvey"
        const val CLEAR_SYNC = "ClearSync"
        const val COMPLETE = "Complete"
    }

    private val compositeDisposable = CompositeDisposable()

    private val syncDataObservable = PublishSubject.create<String>()

    private var syncCallback: SyncCallback? = null

    private var isSyncSuccessfully = true

    private var surveyId: String? = ""

    init {
        syncDataObservable
            .flatMap {
                when (it) {
                    ENQUIRY -> checkEnquiryUpload()
                    ADDRESS_LIST -> checkAddressListUpload()
                    SEQUENCE -> checkSequenceUpload()
                    LEGS -> checkLegsUpload()
                    ROOM_NAME -> checkRoomNameUpload()
                    SIGNATURE -> checkSignatureUpload()
                    PLANNING -> checkPlanningUpload()
                    DELETEPLANNING -> checkdeletePlanningData()
                    COMMENTS -> checkCommentsUpload()
                    PICTURE -> checkPictureUpload()
                    PARTNER -> checkPartnerUpload()
                    INVENTORY -> checkInventoryUpload()
                    DELETESURVEYDATA -> deleteSurveyData()
                    SaveADDRESSLEG -> saveAddressAndLeg()
                    SavePlanningAndComment -> savePlanningAndComment()
                    SURVEY_INVENTORY -> saveSurveyInventory()
                    SUBMIT_SURVEY -> checkSubmitSurveyUpload()
                    CLEAR_SYNC -> clearSyncData()
                    else -> Observable.just(COMPLETE)
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it is String) {
                    if (it == COMPLETE) {
                        if (!isSyncSuccessfully) {
                            Log.d(TAG,"onSyncFailed")
                            syncCallback?.onSyncFailed()
                            showErrorInSyncData()
                        } else {
                            Log.d(TAG,"onSyncComplete")
                            syncCallback?.onSyncComplete()
                        }
                    }
                }
            }, {
                println("Error:${it.message}")
                if (!isSyncSuccessfully) {
                    syncCallback?.onSyncFailed()
                    showErrorInSyncData()
                }
            })
            .collect()
    }

    fun checkSyncData(surveyId: String?, syncCallback: SyncCallback) {
        this.surveyId = surveyId
        this.syncCallback = syncCallback
        isSyncSuccessfully = true
        syncDataObservable.onNext(ENQUIRY)
    }

    private fun checkInventoryUpload(): Observable<Any> {
        var sequenceList: List<SequenceDetailsModel>? = null
        sequenceList = if (surveyId.isNullOrEmpty()){
            db.sequenceDetailsDao().getSyncSurveyDetailsList()
        } else {
            db.sequenceDetailsDao().getSyncSurveyDetailsListBySurveyId(surveyId!!)
        }
        surveyInventoryList.clear()
        return Observable.fromIterable(sequenceList)
            .flatMap { data ->
                val id = data.surveyInventoryId
                val surveyId = data.surveyId
                if (data.isDelete) {
                    setDeleteSyncDataObject(id,surveyId,AppConstants.deleteInventory)
                    //deleteSequenceInventory(id)
                } else {
                    saveSequenceInventory(id, data)
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorReturn { SavedSequenceDetailsModel() }
            .doOnComplete {
                obj_syncInventoryData.surveyInventoryList = surveyInventoryList
                Log.d(TAG, "checkInventoryUpload -> onComplete")
                syncDataObservable.onNext(DELETESURVEYDATA)
            }
    }

    private fun clearSyncData(): Observable<Unit> {
        return Observable.fromCallable {
               // deleteTableRecords()
                db.getSyncDao().delete()
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete {
              //  syncDeleteSurveyData.clearAllData()
                Log.d(TAG, "clearSyncData -> onComplete")
                syncDataObservable.onNext(COMPLETE)
            }
    }

//    private fun deleteTableRecords() {
//        syncDeleteSurveyData.addressList.forEach { oldId ->
//            db.addressListDao().delete(oldId)
//        }
//        syncDeleteSurveyData.sequenceList.forEach { oldId ->
//            db.saveSequenceDao().deleteBySequenceID(oldId)
//        }
//        syncDeleteSurveyData.legList.forEach { oldId ->
//            db.showLegsDao().delete(oldId)
//        }
//        syncDeleteSurveyData.sequenceInventoryList.forEach { oldId ->
//            db.sequenceDetailsDao().delete(oldId)
//        }
//        syncDeleteSurveyData.planningList.forEach { oldId ->
//            db.surveyPlanningListDao().delete(oldId)
//        }
//        syncDeleteSurveyData.pictureList.forEach { oldId ->
//            db.picturesDao().delete(oldId)
//        }
//        syncDeleteSurveyData.partnerList.forEach { _ ->
//            db.partnerTempDao().deletePartnerTable()
//        }
//        syncDeleteSurveyData.commentList.forEach { comment ->
//            db.commentsDetailsDao().delete(comment.key, comment.value) // key as surveyCommentId and value as surveyId
//        }
//        syncDeleteSurveyData.roomNameList.forEach { oldId ->
//            db.inventoryRoomDao().updateNewRoomName(false, oldId)
//        }
//        syncDeleteSurveyData.partnerContactList.forEach { oldId ->
//            db.deletePartnerContact().delete(oldId)
//        }
//        syncDeleteSurveyData.signatureList.forEach { oldId ->
//            db.sequencedetailSignature().delete(oldId)
//        }
//        syncDeleteSurveyData.submitSurveyList.forEach { oldId ->
//            db.getSubmitDao().delete(oldId)
//        }
//    }


    private fun deleteSurveyData(): Observable<BaseModel<GetDeleteSurveyDataResponse>> {
        android.util.Log.d("MyTag","syncDeleteSurveyDataMain:${Gson().toJson(syncDeleteSurveyDataMain)}")
        return  Networking(context)
            .getServices()
            .deleteSurveySyncData(session.user?.userId!!, syncDeleteSurveyDataMain)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                syncDataObservable.onNext(SaveADDRESSLEG)
            }
            .onErrorResumeNext { it: Throwable ->
                if (it is HttpException) {
                    println("deleteSurveyData Error -> ${it.response()?.errorBody()?.string()}")
                }
                isSyncSuccessfully = false
                Log.e(TAG, "Delete Survey Data Main Failed")
                Observable.just<BaseModel<GetDeleteSurveyDataResponse>>(BaseModel()).doOnNext {
                    syncDataObservable.onNext(COMPLETE)
                }
            }
    }

    private fun checkSubmitSurveyUpload(): Observable<BaseModel<Boolean>> {
        val submitDataList = db.getSubmitDao().getSubmitData()
        return Observable.fromIterable(submitDataList)
            .flatMap { data ->
               // syncDeleteSurveyData.submitSurveyList.add(data.surveyId)
                submitSurveyData(data.surveyId)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorReturn { BaseModel() }
            .doOnComplete {
                Log.d(TAG, "checkSubmitSurveyUpload -> onComplete")
                syncDataObservable.onNext(CLEAR_SYNC)
            }
    }

    private fun checkSignatureUpload(): Observable<BaseModel<Any>> {
        var getOfflineSignatureList: List<SeqeunceDetailSignature>? = null
        val signatureListsync: ArrayList<JsonObject> = ArrayList()
        getOfflineSignatureList = if (surveyId.isNullOrEmpty()){
            db.sequencedetailSignature().getSignatureByIdOffline(true)
        } else{
            db.sequencedetailSignature().getSignatureByIdOfflineBySurveyId(true, surveyId!!)
        }

        return Observable.fromIterable(getOfflineSignatureList)
            .flatMap { data ->
                var id = data.surveySequenceId
                if (!id.isNullOrEmpty()){
                    id = if (id.contains("ADD")) {
                        "0"
                    }else{
                        data.surveySequenceId
                    }
                }
                val jsonObject = JsonObject()
                jsonObject.addProperty("SurveyId", data.surveyId)
                jsonObject.addProperty("Signature", if (data.isVideoSequence) "" else data.signature)
                jsonObject.addProperty("SignatureByte", if (data.isVideoSequence) "" else data.signature)
                jsonObject.addProperty("IsVideoSequence", data.isVideoSequence)
                jsonObject.addProperty("SurveySequenceId", id)
                jsonObject.addProperty("UserId", session.user!!.userId)
                jsonObject.addProperty("SequenceNo", data.sequenceNo)
                jsonObject.addProperty("SignatureDate", data.signatureDate)

                signatureListsync.add(jsonObject)
             //   syncDeleteSurveyData.signatureList.add(data.surveyId.toString())
                Observable.just(BaseModel<Any>())
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorReturn { BaseModel() }
            .doOnComplete {
                Log.d(TAG, "checkSignatureUpload -> onComplete")
                obj_syncAllData.setsignatureList(signatureListsync)
                val json = Gson().toJson(obj_syncAllData)
                Log.e("SyncAddress", json.toString())
                Log.d(TAG, "checkSignatureUpload -> onComplete")
                syncDataObservable.onNext(PLANNING)
            }
    }

    /*private fun clearPartnerAndServiceUpload(): Observable<BaseModel<Boolean>> {
        val deletePartnerList = db.deletePartnerContact().getDeletePartnerList()
        return Observable.fromIterable(deletePartnerList)
            .flatMap { data ->
                val id = data.partnerId
                //callRemoveContactApi(id)
              //  syncDeleteSurveyData.partnerContactList.add(id.toString())
                Observable.just(BaseModel<Boolean>())
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorReturn { BaseModel() }
            .doOnComplete {
                Log.d(TAG, "clearPartnerAndServiceUpload -> onComplete")
                syncDataObservable.onNext(INVENTORY)
            }
    }*/

    private fun checkPartnerUpload(): Observable<BaseModel<Boolean>> {
        var partnerDataList: PartnerTempModel? = null
        val partnerListSync: ArrayList<JsonElement> = ArrayList()
        partnerDataList = if (surveyId.isNullOrEmpty()){
            db.partnerTempDao().getPartnerTempListOffline("1")
        } else{
            db.partnerTempDao().getPartnerTempListOfflineBySurveyId("1", surveyId!!)
        }

        val partnerObservable = if (partnerDataList != null) {
            val data = Gson().fromJson(partnerDataList.saveDetails, JsonObject::class.java)
            partnerDataList.saveDetails
         //   syncDeleteSurveyData.partnerList.add("1")
            partnerListSync.add(data)
            Observable.just(BaseModel<Boolean>())
            //callSavePartnerApi(partnerDataList.saveDetails)
        } else {
            Observable.just(BaseModel())
        }

        return partnerObservable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorReturn { BaseModel() }
            .doOnComplete {
                obj_syncPartnerAndCommentData.setAdditionalPersonalList(partnerListSync)
                Log.d(TAG, "checkPartnerUpload -> onComplete")
                syncDataObservable.onNext(INVENTORY)
            }
    }

    private fun checkPictureUpload(): Observable<Any>? {
        var picturesList: List<PicturesModel>? = null
        val pictureListSync: ArrayList<PicturesModel> = ArrayList()
        picturesList = if (surveyId.isNullOrEmpty()){
            db.picturesDao().getPicturesList()
        }else{
            db.picturesDao().getPicturesListBySurveyId(surveyId!!)
        }

        return Observable.fromIterable(picturesList)
            .flatMap { data ->
                val id = data.surveyPicId
                val surveyId = data.surveyId
                if (data.isDelete == true) {
                    setDeleteSyncDataObject(id,surveyId,AppConstants.deletePicture)
                  //  deletePicture(id)
                } else if (id.contains("ADD")) {
                    data.surveyPicId = "0"
                    val imgFile = File(data.pictureName!!)
                    val filePath: String = imgFile.path
                    val bitmap = BitmapFactory.decodeFile(filePath)
                    val stream = ByteArrayOutputStream()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream)
                    }
                    // get the base 64 string
                    val imgString = Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT)
                    data.imageData = imgString
                    pictureListSync.add(data)
                  //  syncDeleteSurveyData.pictureList.add(id)
                    Observable.just(BaseModel<List<PicturesModel>>())
                  // savePicture(data, id)
                } else {
                    Observable.just(BaseModel())
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorReturn { BaseModel<Any>()}
            .doOnComplete {
                obj_syncPartnerAndCommentData.setSurveyPictureList(pictureListSync)
                Log.d(TAG, "checkPictureUpload -> onComplete")
               syncDataObservable.onNext(PARTNER)
            }
    }

    private fun checkCommentsUpload(): Observable<BaseModel<List<CommentsDetailModel>>> {
        var commentsList: List<CommentsDetailModel>? = null
        val commentListsync: ArrayList<CommentsDetailModel> = ArrayList()
        commentsList = if (surveyId.isNullOrEmpty()){
            db.commentsDetailsDao().getCommentsList()
        }else{
            db.commentsDetailsDao().getCommentsListBySurveyId(surveyId!!)
        }

        return Observable.fromIterable(commentsList)
            .flatMap { data ->
                val id = data.surveyCommentId
                if (data.surveyCommentId.contains("ADD") || data.isChangedField == true) {
                    if (data.sequenceId?.contains("ADD") == true) {
                        data.sequenceId = "0"
                    }

                    if (data.surveyCommentId.contains("ADD")) {
                        data.surveyCommentId = "0"
                        data.sequenceNo?.toString() ?: data.sequenceNo
                        commentListsync.add(data)
                     //   syncDeleteSurveyData.commentList[id] = data.surveyId.toString()
                        Observable.just(BaseModel<List<CommentsDetailModel>>())
                      //  saveSurveyComment(data, id)
                    } else {
                        data.sequenceNo = data.sequenceNo
                        commentListsync.add(data)
                      //  syncDeleteSurveyData.commentList[id] = data.surveyId.toString()
                        Observable.just(BaseModel<List<CommentsDetailModel>>())
                     //   saveSurveyComment(data, id)
                    }
                } else {
                    Observable.just(BaseModel())
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorReturn { BaseModel() }
            .doOnComplete {
                obj_syncPartnerAndCommentData.setCommentList(commentsList)
                Log.d(TAG, "checkCommentsUpload -> onComplete")
                syncDataObservable.onNext(PICTURE)
            }
    }

    private fun checkPlanningUpload(): Observable<BaseModel<List<SurveyPlanningListModel>>>? {
        var planningList: List<SurveyPlanningListModel>? = null
        val planningListsync: ArrayList<SurveyPlanningListModel> = ArrayList()
         planningList = if (surveyId.isNullOrEmpty()){
            db.surveyPlanningListDao().getPlanningList()
        } else {
            db.surveyPlanningListDao().getPlanningListSurveyId(surveyId!!)
        }

        return Observable.fromIterable(planningList)
            .flatMap { data ->
                val id = data.surveyPlanningId
                if (data.isChangedField == true) {
                    if (data.sequenceId?.contains("ADD") == true)
                        data.sequenceId = "0"
                    planningListsync.add(data)
              //      syncDeleteSurveyData.planningList.add(id.toString())
                    Observable.just(BaseModel<List<SurveyPlanningListModel>>())
                  //  savePlanningData(data, id.toString())
                } else {
                    Observable.just(BaseModel())
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorReturn { BaseModel() }
            .doOnComplete {
                obj_syncPartnerAndCommentData.setSurveyPlanningDetailList(planningListsync)
                Log.d(TAG, "checkPlanningUpload -> onComplete")
                syncDataObservable.onNext(DELETEPLANNING)
            }


    }

    private fun checkdeletePlanningData():  Observable<Any>? {
        val deletePlanningData = db.deletePlanningData().getDeletePlanningDataList()
        return Observable.fromIterable(deletePlanningData)
            .flatMap { data ->
                val id = data.surveyPlanningId!!.toString()
                val surveyId = data.surveyId!!.toString()
                setDeleteSyncDataObject(id,surveyId,AppConstants.deletePlanning)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorReturn {  Observable.just(BaseModel<Any>()) }
            .doOnComplete {
                val json = Gson().toJson(obj_syncAllData)
                Log.e("SyncAddress", json.toString())
                Log.d(TAG, "checkAddressListUpload -> onComplete")
                syncDataObservable.onNext(COMMENTS)
            }
    }

    private fun checkRoomNameUpload(): Observable<BaseModel<Boolean>> {
        val newlyAddedRoomList = db.inventoryRoomDao().getNewlyAddedRoom(true)
        val roomListSync: ArrayList<SaveUserRoomSync> = ArrayList()
        return Observable.fromIterable(newlyAddedRoomList)
            .flatMap { data ->
                val params = HashMap<String, Any>()
                params["roomId"] = data.roomId
                params["SurveyId"] = data.surveyId!!
                params["roomName"] = data.room!!
                params["UserRoomId"] = data.userRoomId

                val userRoom: SaveUserRoomSync = SaveUserRoomSync()
                userRoom.setRoomId(data.roomId)
                userRoom.setSurveyId(data.surveyId!!)
                userRoom.setRoomName(data.room!!)
                userRoom.setUserRoomId(data.userRoomId)

                roomListSync.add(userRoom)
              //  syncDeleteSurveyData.roomNameList.add(data.id.toString())
                //roomName(params, data)
                Observable.just(BaseModel<Boolean>())
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorReturn { BaseModel() }
            .doOnComplete {
                obj_syncAllData.setUserRoom(roomListSync)
                Log.d(TAG, "checkRoomNameUpload -> onComplete")
                syncDataObservable.onNext(SIGNATURE)
            }

    }

    private fun checkAddressListUpload(): Observable<Any>? {
        var addressList: List<AddressListModel>? = null
        val addressListSync: ArrayList<AddressListModel> = ArrayList()
        addressList = if (surveyId.isNullOrEmpty()){
            db.addressListDao().getAddressList()
        }else{
            db.addressListDao().getAddressListBySurvey(surveyId!!)
        }

        return Observable.fromIterable(addressList)
            .flatMap { data ->
                val id = data.surveyAddressId
                var surveyId = data.surveyId
                if (data.isDelete == true) {
                    setDeleteSyncDataObject(id,surveyId,AppConstants.deleteAddress)
                  //  deleteAddress(id)
                } else if (data.surveyAddressId.contains("ADD") || data.isChangedField == true) {
                    if (data.surveyAddressId.contains("ADD")) {
                        data.surveyAddressId = "0"
                    }
                    addressListSync.add(data)
               //    syncDeleteSurveyData.addressList.add(id)
                    Observable.just(BaseModel<Any>())
                 //   saveAddress(data, id)
                } else {
                    Observable.just(BaseModel<Any>())
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorReturn {  Observable.just(BaseModel<Any>()) }
            .doOnComplete {
                obj_syncAllData.setAddressList(addressListSync)
                Log.d(TAG, "checkAddressListUpload -> onComplete")
                syncDataObservable.onNext(SEQUENCE)
            }

    }

    private fun setDeleteSyncDataObject(id: String, surveyId: String?, type: String) : Observable<Any> {
        try {
            deleteRowId += 1
            syncDeleteSurveyData.setRowsId(deleteRowId)
            syncDeleteSurveyData.setSurveyId(surveyId?.toInt())
            syncDeleteSurveyData.setPkId(id?.toInt())
            syncDeleteSurveyData.setTypes(type)

            val json = Gson().toJson(syncDeleteSurveyData)
            Log.e("SyncAddress", json.toString())
            syncDeleteSurveyDataMain.add(Gson().fromJson(json, JsonObject::class.java))
        }catch (e: Exception){
            Log.e("error", e.message.toString())
        }

        return  Observable.just(BaseModel<Any>())
    }

    private fun checkEnquiryUpload(): Observable<BaseModel<List<User>>> {
        var dashboardList: List<DashboardModel>? = null
        val dashboardListSync: ArrayList<DashboardModel> = ArrayList()
        dashboardList = if (surveyId.isNullOrEmpty()){
            db.dashboardDao().getDashboardList()
        }else{
            db.dashboardDao().getDashboardListBySurveyId(surveyId!!)
        }

        return Observable.fromIterable(dashboardList)
            .flatMap { data ->
                if (data.isChangedField == true) {
                    //saveEnquiryData(data)
                    dashboardListSync.add(data)
                    Observable.just(BaseModel<List<User>>())
                } else {
                    Observable.just(BaseModel())
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorReturn { BaseModel() }
            .doOnComplete {
                obj_syncAllData.setsurveyListt(dashboardListSync)
                Log.d(TAG, "checkEnquiryUpload -> onComplete")
                syncDataObservable.onNext(ADDRESS_LIST)
            }
    }

    private fun checkLegsUpload(): Observable<Any> {
        var showLegsList: List<ShowLegsModel>? = null
        val legListSync: ArrayList<ShowLegsModel> = ArrayList()
        showLegsList = if (surveyId.isNullOrEmpty()){
            db.showLegsDao().getShowLegsListAll()
        }else{
            db.showLegsDao().getLegListBySurvey(surveyId)
        }

        return Observable.fromIterable(showLegsList)
            .flatMap { data ->
                val oldId = data.surveySequenceLegId
                var surveyId = data.surveyId
                if (data.isDelete == true) {
                   // deleteLegBySurveySequence(data.surveySequenceLegId!!, oldId!!)
                    setDeleteSyncDataObject(oldId.toString(),surveyId,AppConstants.deleteSequenceLeg)
                } else if (data.surveySequenceLegId!!.contains("ADD") || data.isChangedField == true) {

                    if (data.originAddressId?.contains("ADD") == true) {
                        data.originAddressId = "0"
                    }
                    if (data.destinationAddressId?.contains("ADD") == true) {
                        data.destinationAddressId = "0"
                    }
                    if (data.surveySequenceId?.contains("ADD") == true) {
                        data.surveySequenceId = "0"
                    }

                    if (data.surveySequenceLegId!!.contains("ADD")) {
                        data.surveySequenceLegId = "0"
                        legListSync.add(data)
                      //  syncDeleteSurveyData.legList.add(oldId!!)
                      //  saveLegsData(data, oldId!!)
                        Observable.just(BaseModel<Any>())
                    } else {
                        legListSync.add(data)
                   //     syncDeleteSurveyData.legList.add(oldId!!)
                        // saveLegsData(data, oldId!!)
                        Observable.just(BaseModel<Any>())
                    }
                } else {
                    Observable.just(BaseModel<Any>())
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorReturn { BaseModel<Any>() }
            .doOnComplete {
                obj_syncAllData.setSequenceLegList(legListSync)
                Log.d(TAG, "checkLegsUpload -> onComplete" + "obj_syncAllData:${Gson().toJson(obj_syncAllData)}")
                syncDataObservable.onNext(ROOM_NAME)
            }

    }

    private fun checkSequenceUpload(): Observable<Any>? {
        var sequenceList: List<SaveSequenceModel>? = null
        val sequenceListSync: ArrayList<SaveSequenceModel> = ArrayList()
        sequenceList = if (surveyId.isNullOrEmpty()){
            db.saveSequenceDao().getSequenceList()
        } else{
            db.saveSequenceDao().getSequenceListBySurveyId(surveyId!!)
        }

        return Observable.fromIterable(sequenceList)
            .flatMap { data ->
                val oldId = data.surveySequenceId
                val surveyId = data.surveyId
                if (data.originAddressId?.contains("ADD") == true) {
                    data.originAddressId = "0"
                }
                if (data.destinationAddressId?.contains("ADD") == true) {
                    data.destinationAddressId = "0"
                }
                if (data.isDelete == true) {
                  //  deleteSurveySequence(data.surveySequenceId, oldId)
                    setDeleteSyncDataObject(oldId,surveyId,AppConstants.deleteSequence)
                } else if (data.surveySequenceId.contains("ADD") || data.isChangedField == true) {
                    if (data.surveySequenceId.contains("ADD"))
                        data.surveySequenceId = "0"
                    sequenceListSync.add(data)
                    Log.e("SequxwSRDA",data!!.originAddress.toString());
                //    syncDeleteSurveyData.sequenceList.add(oldId)
                    Observable.just(BaseModel<Any>())
                  //  saveSequenceData(data, oldId)
                } else {
                    Observable.just(BaseModel())
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorReturn { Observable.just(BaseModel<Any>())}
            .doOnComplete {
                obj_syncAllData.setSequenceList(sequenceListSync)
                Log.d(TAG, "checkSequenceUpload -> onComplete" +"obj_syncAllData:${Gson().toJson(obj_syncAllData)}")
                syncDataObservable.onNext(LEGS)
            }
    }

    private fun deleteSurveySequence(surveySequenceId: String, oldId: String): Observable<BaseModel<List<SurveyPlanningListModel>>> {
        return Networking(context)
            .getServices()
            .deleteSurveySequence(surveySequenceId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { db.saveSequenceDao().deleteBySequenceID(oldId) }
            .doOnError {
                isSyncSuccessfully = false
                Log.e(TAG, "Delete SurveySequence Failed")
            }
    }

    private fun saveSequenceData(data: SaveSequenceModel, oldId: String): Observable<BaseModel<List<SaveSequenceModel>>> {
        return Networking(context)
            .getServices()
            .saveSequenceData(data)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                Log.d(TAG,"Save SurveySequence before Delete")
                db.saveSequenceDao().deleteBySequenceID(oldId)
                Log.d(TAG,"Save SurveySequence after Delete")
            }
            .doOnError {
                isSyncSuccessfully = false
                Log.e(TAG, "Save SurveySequence Failed")
            }
    }

    private fun deleteLegBySurveySequence(surveySequenceLegId: String, oldId: String): Observable<BasicModel> {
        return Networking(context)
            .getServices()
            .deleteSequenceLegById(surveySequenceLegId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { db.showLegsDao().delete(oldId) }
            .doOnError {
                isSyncSuccessfully = false
                Log.e(TAG, "Delete Legs Data Failed")
            }
    }

    private fun saveLegsData(data: ShowLegsModel, oldId: String): Observable<BaseModel<List<ShowLegsModel>>> {
        return Networking(context)
            .getServices()
            .saveSequenceLeg(data)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                Log.d("MyTag","Save Legs Data Success before Delete")
                db.showLegsDao().delete(oldId)
                Log.d("MyTag","Save Legs Data Success after Delete")
            }
            .doOnError {
                isSyncSuccessfully = false
                Log.e("MyTag", "Save Legs Data Failed")
            }
    }

    private fun saveEnquiryData(dashboardModel: DashboardModel): Observable<BaseModel<List<User>>> {
        return Networking(context)
            .getServices()
            .saveSurveyDetail(dashboardModel)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { Log.d(TAG, "Save Enquiry Data Success") }
            .doOnError {
                isSyncSuccessfully = false
                Log.e(TAG, "Save Enquiry Data Failed")
            }
    }

    private fun saveAddress(data: AddressListModel, oldId: String): Observable<BaseModel<List<AddressListModel>>> {
        return Networking(context)
            .getServices()
            .saveSurveyAddress(data)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                db.addressListDao().delete(oldId)
            }
            .doOnError {
                isSyncSuccessfully = false
                Log.e(TAG, "Save Address Data Failed")
            }
    }

    private fun deleteAddress(surveyAddressId: String): Observable<BaseModel<List<AddressListModel>>> {
        return Networking(context)
            .getServices()
            .deleteSurveyAddress(
                surveyAddressId,
                session.user!!.userId!!
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { db.addressListDao().delete(surveyAddressId) }
            .doOnError {
                isSyncSuccessfully = false
                Log.e(TAG, "Delete Address Data Failed")
            }
    }

    private fun roomName(params: HashMap<String, Any>, data: InventoryRoomModel): Observable<BaseModel<Boolean>> {
        return Networking(context)
            .getServices()
            .saveRoomName(Networking.wrapParams(params))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { db.inventoryRoomDao().updateNewRoomName(false, data.id.toString()) }
            .doOnError {
                isSyncSuccessfully = false
                Log.e(TAG, "Save RoomName Data Failed")
            }
    }

    private fun savePlanningData(data: SurveyPlanningListModel, oldId: String): Observable<BaseModel<List<SurveyPlanningListModel>>> {
        return Networking(context)
            .getServices()
            .saveSurveyPlanningDetail(data)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { db.surveyPlanningListDao().delete(oldId) }
            .doOnError {
                isSyncSuccessfully = false
                Log.e(TAG, "Save Planning Data Failed")
            }
    }

    private fun saveSurveyComment(data: CommentsDetailModel, oldId: String): Observable<BaseModel<List<CommentsDetailModel>>> {
        return Networking(context)
            .getServices()
            .saveSurveyComment(data)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { db.commentsDetailsDao().delete(oldId, data.surveyId.toString()) }
            .doOnError {
                isSyncSuccessfully = false
                Log.e(TAG, "Save Survey Comment Data Failed")
            }
    }

    private fun deletePicture(surveyPicId: String): Observable<BaseModel<List<PicturesModel>>> {
        return Networking(context)
            .getServices()
            .deleteSurveyPicture(surveyPicId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { db.picturesDao().delete(surveyPicId) }
            .doOnError {
                isSyncSuccessfully = false
                Log.e(TAG, "Delete Picture Data Failed")
            }
    }

    private fun savePicture(data: PicturesModel, oldId: String): Observable<BaseModel<List<PicturesModel>>> {
        val imgFile = File(data.pictureName!!)
        val filePath: String = imgFile.path
        val bitmap = BitmapFactory.decodeFile(filePath)
        val stream = ByteArrayOutputStream()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream)
        }
        // get the base 64 string
        val imgString = Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT)

        val partMap: Map<String, RequestBody> = mapOf(
            "SurveyPicId" to "0".toRequestBody("text/plain".toMediaTypeOrNull()),
            "SurveyId" to data.surveyId!!.toRequestBody("text/plain".toMediaTypeOrNull()),
            "PictureName" to data.pictureName!!.toRequestBody("text/plain".toMediaTypeOrNull()),
            "CreatedBy" to data.createdBy!!.toRequestBody("text/plain".toMediaTypeOrNull()),
            "ImageData" to imgString!!.toRequestBody("text/plain".toMediaTypeOrNull())
        )

        return Networking(context)
            .getServices()
            .saveSurveyPictureBase(partMap)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { db.picturesDao().delete(oldId) }
            .doOnError {
                isSyncSuccessfully = false
                Log.e(TAG, "Save Picture Data Failed")
            }
    }

    private fun callSavePartnerApi(saveDetails: String): Observable<BaseModel<Boolean>> {
        return Networking(context).getServices()
            .savePartnerServiceList(JsonParser().parse(saveDetails) as JsonObject)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { db.partnerTempDao().deletePartnerTable() }
            .doOnError {
                isSyncSuccessfully = false
                Log.e(TAG, "Save Partner Data Failed")
            }
    }

    private fun callRemoveContactApi(id: Int): Observable<BaseModel<Boolean>> {
        return Networking(context)
            .getServices()
            .removePartnerContact(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { db.deletePartnerContact().delete(id.toString()) }
            .doOnError {
                isSyncSuccessfully = false
                Log.e(TAG, "Delete Contact Data Failed")
            }
    }

    private fun saveSignature(jsonObject: JsonObject, surveyId: Int): Observable<BaseModel<Any>> {
        return Networking(context)
            .getServices()
            .saveSignature(jsonObject)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { db.sequencedetailSignature().delete(surveyId.toString()) }
            .doOnError {
                isSyncSuccessfully = false
                Log.e(TAG, "Save Signature Data Failed")
            }
    }

    private fun submitSurveyData(surveyId: String): Observable<BaseModel<Boolean>> {
        return Networking(context)
            .getServices()
            .saveSequenceDetails(surveyId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { db.getSubmitDao().delete(surveyId) }
            .doOnError {
                isSyncSuccessfully = false
                Log.e(TAG, "Submit Survey Data Failed")
            }
    }

    private fun deleteSequenceInventory(oldId: String): Observable<BaseModel<List<SaveSequenceModel>>> {
        return Networking(context)
            .getServices()
            .deleteSequenceInventoryItem(oldId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { db.sequenceDetailsDao().delete(oldId) }
            .doOnError {
                isSyncSuccessfully = false
                Log.e(TAG, "Submit Survey Data Failed")
            }
    }

    private fun saveSequenceInventory(id: String, data: SequenceDetailsModel): Observable<SavedSequenceDetailsModel> {
        val saveInventorySync = SaveInventorySync()
       return if (data.surveyInventoryId.contains("ADD") || data.isChangedField == true) {

           if (data.surveyInventoryId.contains("ADD")) {
                data.tempId =  data.surveyInventoryId
                data.surveyInventoryId = "0"
               saveInventorySync.SurveyInventoryId = "0"
           } else {
               saveInventorySync.SurveyInventoryId = data.surveyInventoryId
           }

           if (data.sequenceId!!.contains("ADD")) {
               data.sequenceId = "0"
               saveInventorySync.SequenceId = "0"
           } else {
               saveInventorySync.SequenceId = data.sequenceId
           }

           if (data.surveySequenceLegId!!.contains("ADD")) {
               data.surveySequenceLegId = "0"
               saveInventorySync.SurveySequenceLegId = "0"
           } else {
               saveInventorySync.SurveySequenceLegId = data.surveySequenceLegId
           }

           saveInventorySync.SurveyId = data.surveyId
           saveInventorySync.FloorId = data.floorId
           saveInventorySync.Floor = data.floor
           saveInventorySync.RoomId = data.roomId
           saveInventorySync.Room = data.room
           saveInventorySync.SurveySequence = data.surveySequence
           saveInventorySync.SurveySequenceLeg = data.surveySequenceLeg
           saveInventorySync.CountId = data.count
           saveInventorySync.Volume = data.volume
           saveInventorySync.IsDismantle = data.isDismantle
           saveInventorySync.IsCard = data.isCard
           saveInventorySync.IsBubbleWrap = data.isBubbleWrap
           saveInventorySync.IsRemain = data.isRemain
           saveInventorySync.IsFullExportWrap = data.isFullExportWrap
           saveInventorySync.CreateLength = data.createLength
           saveInventorySync.CreateHeight = data.createHeight
           saveInventorySync.CreateWidth = data.createWidth
           saveInventorySync.MiscComment = data.miscComment
           saveInventorySync.DamageComment = data.damageComment
           saveInventorySync.InventoryTypeId = data.inventoryTypeId
           saveInventorySync.InventoryItemId = data.inventoryNameId
           saveInventorySync.CreatedBy = session.user?.userId
           saveInventorySync.InventoryItemName = data.inventoryItemName
           saveInventorySync.IsApproveAdmin = data.isApproveAdmin
           saveInventorySync.IsPackingType = data.packingType
           saveInventorySync.IsCrate = data.isCrate
           saveInventorySync.IsCustomize = data.isCustomize
           saveInventorySync.CreateUnit = getMeasurementType("inches", context)
           saveInventorySync.InventoryValue = data.inventoryValue
           saveInventorySync.IsNew = data.newRecord
           saveInventorySync.isUserAdded = data.isUserAdded
           saveInventorySync.CrateTypeId = data.crateTypeId!!.toInt()
           saveInventorySync.isSubItem = data.isSubItem
           saveInventorySync.subItemName = data.subItemName
           saveInventorySync.tempId = data.tempId

           /*val partMap: MutableMap<String, RequestBody> = mutableMapOf(
               "SurveyInventoryId" to data.surveyInventoryId.toRequestBody("text/plain".toMediaTypeOrNull()),
               "SurveyId" to data.surveyId.toString()
                   .toRequestBody("text/plain".toMediaTypeOrNull()),
               "FloorId" to data.floorId!!.toRequestBody("text/plain".toMediaTypeOrNull()),
               "RoomId" to data.roomId!!.toRequestBody("text/plain".toMediaTypeOrNull()),
               "Room" to data.room!!.toRequestBody("text/plain".toMediaTypeOrNull()),
               "SequenceId" to data.sequenceId!!.toRequestBody("text/plain".toMediaTypeOrNull()),
               "SurveySequenceLeg" to if (data.surveySequenceLeg.isNullOrEmpty()) "".toRequestBody(
                   "text/plain".toMediaTypeOrNull()
               ) else data.surveySequenceLeg!!.toRequestBody("text/plain".toMediaTypeOrNull()),
               "SurveySequenceLegId" to if (data.surveySequenceLegId.equals("0")) "0".toRequestBody(
                   "text/plain".toMediaTypeOrNull()
               ) else data.surveySequenceLegId!!.toRequestBody("text/plain".toMediaTypeOrNull()),
               "CountId" to data.count!!.toRequestBody("text/plain".toMediaTypeOrNull()),
               "Volume" to data.volume!!.toRequestBody("text/plain".toMediaTypeOrNull()),
               "TotalVolume" to data.totalVolume!!.toRequestBody("text/plain".toMediaTypeOrNull()),
               "IsDismantle" to data.isDismantle.toString()
                   .toRequestBody("text/plain".toMediaTypeOrNull()),
               "IsCard" to data.isCard!!.toString()
                   .toRequestBody("text/plain".toMediaTypeOrNull()),
               "IsBubbleWrap" to data.isBubbleWrap!!.toString()
                   .toRequestBody("text/plain".toMediaTypeOrNull()),
               "IsRemain" to data.isRemain!!.toString()
                   .toRequestBody("text/plain".toMediaTypeOrNull()),
               "IsFullExportWrap" to data.isFullExportWrap!!.toString()
                   .toRequestBody("text/plain".toMediaTypeOrNull()),
               "InventoryTypeId" to data.inventoryTypeId!!.toRequestBody("text/plain".toMediaTypeOrNull()),

               "Item" to data.item!!.toRequestBody("text/plain".toMediaTypeOrNull()),
               "InventoryItemId" to if (data.inventoryNameId.equals("0")) "".toRequestBody(
                   "text/plain".toMediaTypeOrNull()
               ) else data.inventoryNameId!!.toRequestBody("text/plain".toMediaTypeOrNull()),

               "InventoryValue" to (data.inventoryValue!!
                   ?.toRequestBody("text/plain".toMediaTypeOrNull())
                   ?: "".toRequestBody("text/plain".toMediaTypeOrNull())),
               "IsCrate" to data.isCrate.toString()
                   .toRequestBody("text/plain".toMediaTypeOrNull()),
               "IsPackingType" to data.packingType.toString()
                   .toRequestBody("text/plain".toMediaTypeOrNull()),
               "IsCustomize" to data.isCustomize.toString()
                   .toRequestBody("text/plain".toMediaTypeOrNull()),
               "SurveySequence" to data.surveySequence.toString()
                   .toRequestBody("text/plain".toMediaTypeOrNull()),

               )*/

          /* if (!data.inventoryItemName.isNullOrEmpty()) {
               partMap["InventoryItemName"] =
                   data.inventoryItemName!!.toRequestBody("text/plain".toMediaTypeOrNull())
           }

           partMap["CreatedBy"] =
               session.user?.userId!!.toRequestBody("text/plain".toMediaTypeOrNull())


           if (!data.createLength.isNullOrEmpty()) {
               partMap["CreateLength"] =
                   data.createLength!!.toRequestBody("text/plain".toMediaTypeOrNull())
           }

           if (!data.createLength.isNullOrEmpty()) {
               partMap["CreateLength"] =
                   data.createLength!!.toRequestBody("text/plain".toMediaTypeOrNull())
           }

           if (!data.createHeight.isNullOrEmpty()) {
               partMap["CreateHeight"] =
                   data.createHeight!!.toRequestBody("text/plain".toMediaTypeOrNull())
           }

           if (!data.createWidth.isNullOrEmpty()) {
               partMap["CreateWidth"] =
                   data.createWidth!!.toRequestBody("text/plain".toMediaTypeOrNull())
           }

           if (!data.miscComment.isNullOrEmpty()) {
               partMap["MiscComment"] =
                   data.miscComment!!.toRequestBody("text/plain".toMediaTypeOrNull())
           }

           if (!data.damageComment.isNullOrEmpty()) {
               partMap["DamageComment"] =
                   data.damageComment!!.toRequestBody("text/plain".toMediaTypeOrNull())
           }*/

           if (data.itemImage != null && data.itemImage!!.isNotEmpty() && !data.itemImage.equals("null")) {
//               val imgFile = File(data.image!!)
//               val filePath: String = imgFile.path
//               val bitmap = BitmapFactory.decodeFile(filePath)
//               val stream = ByteArrayOutputStream()
//               if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                   bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream)
//               }
//               // get the base 64 string
//               val imgString =
//                   Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT)
//             /*  partMap["ImageData"] =
//                   imgString.toRequestBody("text/plain".toMediaTypeOrNull())*/
               saveInventorySync.ImageData = data.itemImage
               saveInventorySync.Image = data.image
           }
          /* if (data.inventoryNameId == null || data.inventoryNameId!!.isEmpty()) {
               partMap["InventoryItemName"] =
                   data.inventoryItemName!!.toRequestBody("text/plain".toMediaTypeOrNull())
               partMap["IsApproveAdmin"] = data.isApproveAdmin.toString()
                   .toRequestBody("text/plain".toMediaTypeOrNull())
           }*/
           try {
               if (data.surveyInventoryId.toInt() > 0) {
                   surveyInventoryList.add(saveInventorySync)
                   Observable.just(SavedSequenceDetailsModel())
                   //updateInventoryData(partMap, id)
               } else {
                   surveyInventoryList.add(saveInventorySync)
                   //saveInventoryData(partMap, id)
                   Observable.just(SavedSequenceDetailsModel())
               }
           } catch (e: Exception) {
               surveyInventoryList.add(saveInventorySync)
               //saveInventoryData(partMap, id)
               Observable.just(SavedSequenceDetailsModel())
           }
       } else {
           Log.d(TAG,"saveSequenceInventory Empty")
           Observable.empty()
       }
    }

   /* private fun updateInventoryData(partMap: MutableMap<String, RequestBody>, id: String): Observable<SavedSequenceDetailsModel> {
        return Networking(context)
            .getServices()
            .postUpdateSequenceInventoryItemDetails(partMap)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { db.sequenceDetailsDao().updateIsChangedField(id, false) }
            .doOnError {
                isSyncSuccessfully = false
                Log.e(TAG, "Update Inventory Data Failed")
            }
    }*/


    private fun saveAddressAndLeg(): Observable<BaseModel<GetAddressAndLegSyncResponse>> {
        android.util.Log.d("MyTag","obj_syncAllData:${Gson().toJson(obj_syncAllData)}")
        return Networking(context)
            .getServices()
            .saveSequenceOffline(session.user?.userId.toString(), obj_syncAllData)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                syncDataObservable.onNext(SavePlanningAndComment)
            }
            .onErrorResumeNext { it: Throwable ->
                if (it is HttpException) {
                    println("saveAddressAndLeg Error -> ${it.response()?.errorBody()?.string()}")
                }
                isSyncSuccessfully = false
                Log.e(TAG, "Save sAddress And Leg Data Failed")
                Observable.just<BaseModel<GetAddressAndLegSyncResponse>>(BaseModel()).doOnNext {
                    syncDataObservable.onNext(COMPLETE)
                }
            }
    }

    private fun savePlanningAndComment(): Observable<BaseModel<GetPlanningAndCommentSyncResponse>> {
        android.util.Log.d("MyTag","obj_syncPartnerAndCommentData:${Gson().toJson(obj_syncPartnerAndCommentData)}")
        return Networking(context)
            .getServices()
            .savePlanningCommentOffline(session.user?.userId.toString(),obj_syncPartnerAndCommentData)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                syncDataObservable.onNext(SURVEY_INVENTORY)
            }
            .onErrorResumeNext { it: Throwable ->
                if (it is HttpException) {
                    println("savePlanningAndComment Error -> ${it.response()?.errorBody()?.string()}")
                }
                isSyncSuccessfully = false
                Log.e(TAG, "Save Planning And Comment Data Failed")
                Observable.just<BaseModel<GetPlanningAndCommentSyncResponse>>(BaseModel()).doOnNext {
                    syncDataObservable.onNext(COMPLETE)
                }
            }
    }

   /* private fun saveInventoryData(partMap: MutableMap<String, RequestBody>, id: String): Observable<SavedSequenceDetailsModel> {
        return Networking(context)
            .getServices()
            .postSaveInventoryDetails(partMap)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { response ->
              //  Log.d("SequnceListInventory",response.message.toString())
                if (response.success) {
                    if (response.Data != null) {
                        db.sequenceDetailsDao().delete(id)
                        val seq: SequenceDetailsModel? = response.Data
                        db.sequenceDetailsDao().insert(seq)
                    }
                }
            }
            .doOnError {
                isSyncSuccessfully = false
              //  Log.e(TAG, "Save Inventory Data Failed")
            }
    }*/

    private fun showErrorInSyncData() {
        val errorMessage = if (Log.errorMessage.toString().isEmpty()) {
            context.getString(R.string.dialog_sync_data_error_message)
        } else {
            context.getString(R.string.dialog_sync_data_error_message)
          //  "Something went wrong with " + Log.errorMessage.toString() + " please try again!"
        }
        SyncDataDialog(context, errorMessage).apply {
            setTitleData(context.getString(R.string.dialog_sync_data_error_title))
            setCancelButton(false)
            setListener(object: SyncDataDialog.OkButtonListener {
                override fun onOkPressed(dialog: SyncDataDialog) {
                    dialog.dismiss()
                }
            })
        }.also { it.show() }
    }

    /*private fun createInventoryJson(data: SequenceDetailsModel): SaveInventorySync {
        val saveInventory = SaveInventorySync()
        saveInventory.setInventoryItemId(0)
        if (data.inventoryTypeId != null && !data.inventoryTypeId!!.isEmpty()) {
            saveInventory.setInventoryTypeId(data.inventoryTypeId!!.toInt())
        }
        saveInventory.setInventoryType("")
        saveInventory.setName(data.inventoryItemName)

        if (data.createdBy != null && !data.createdBy!!.isEmpty()) {
            saveInventory.setCreatedBy(data.createdBy!!.toInt())
        }

        if (data.createLength != null && !data.createLength!!.isEmpty()) {
            saveInventory.setLength(data.createLength!!.toLong())
        } else {
            saveInventory.setLength(0)
        }
        if (data.createWidth != null && !data.createWidth!!.isEmpty()) {
            saveInventory.setWidth(data.createWidth!!.toLong())
        } else {
            saveInventory.setWidth(0)
        }
        if (data.createHeight != null && !data.createHeight!!.isEmpty()) {
            saveInventory.setHeight(data.createHeight!!.toLong())
        } else {
            saveInventory.setHeight(0)
        }

        if (data.volume != null && !data.volume!!.isEmpty()) {
            saveInventory.setVolume(data.volume!!.toString())
        } else {
            saveInventory.setVolume("0")
        }

        if (data.count != null && !data.count!!.isEmpty()) {
            saveInventory.setCount(data.count!!.toInt())
        } else {
            saveInventory.setCount(0)
        }

        saveInventory.setIsPackingType(0)

        if (data.inventoryNameId == null || data.inventoryNameId!!.isEmpty()) {
            saveInventory.setNeedToApproveByAdmin(data.isApproveAdmin)
        }

        if (data.totalVolume.isNullOrEmpty()) {
            saveInventory.setInventoryValue(data.totalVolume!!.toLong())
        } else {
            saveInventory.setInventoryValue(0)
        }

        saveInventory.setIsApprove(true)
        return saveInventory;
    }*/

    private fun saveSurveyInventory(): Observable<BaseModel<Any>> {
        android.util.Log.d("MyTag","obj_saveSurveyInventoryData:${Gson().toJson(obj_syncInventoryData)}")
        return Networking(context)
            .getServices()
            .saveSurveyInventoryOffline(session.user?.userId.toString(), obj_syncInventoryData)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                syncDataObservable.onNext(SUBMIT_SURVEY)
            }
            .onErrorResumeNext { it: Throwable ->
                if (it is HttpException) {
                    println("saveSurveyInventory Error -> ${it.response()?.errorBody()?.string()}")
                }
                isSyncSuccessfully = false
                Log.e(TAG, "Save Survey Inventory Data Failed")
                Observable.just<BaseModel<Any>>(BaseModel()).doOnNext {
                    syncDataObservable.onNext(COMPLETE)
                }
            }
    }

    private fun Disposable.collect() {
        compositeDisposable.add(this)
    }

    fun dispose() = compositeDisposable.dispose()

    object Log {

        var errorMessage = StringBuilder()

        fun d(tag: String, msg: String) {
            println("$tag --> $msg")
        }

        fun e(tag: String, msg: String) {
            println("$tag --> $msg")
            errorMessage.append(msg)
            errorMessage.append("\n")
        }
    }
}

