package com.pickfords.surveyorapp.viewModel.surveyDetails

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.pickfords.surveyorapp.R
import com.pickfords.surveyorapp.base.BaseViewModel
import com.pickfords.surveyorapp.model.surveyDetails.*
import com.pickfords.surveyorapp.network.BaseModel
import com.pickfords.surveyorapp.network.CallbackObserver
import com.pickfords.surveyorapp.network.Networking
import com.pickfords.surveyorapp.utils.AppConstants
import com.pickfords.surveyorapp.utils.Utility
import com.pickfords.surveyorapp.view.BaseActivity
import com.pickfords.surveyorapp.view.dialogs.MessageDialog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File

class InventoryViewModel(val context: Context) : BaseViewModel() {
    var itemName: ObservableField<String> = ObservableField()
    var itemValue: ObservableField<String> = ObservableField()
    var totalVolume: MutableLiveData<String> = MutableLiveData()
    var itemEnabled: ObservableField<Boolean> = ObservableField()
    var roomNameEnabled: ObservableField<Boolean> = ObservableField()
    var isPackingType: ObservableField<Int> = ObservableField()
    var volume: ObservableField<String> = ObservableField()
    var count: ObservableField<String> = ObservableField()
    var itemimage: MutableLiveData<String> =  MutableLiveData()
    var isEditable: ObservableField<Boolean> = ObservableField()
    var selectedCount: MutableLiveData<Int> = MutableLiveData()


    val isRefreshList = MutableLiveData<Boolean>()

    var selectedItemPosition: Int = 0
    var selectedItemPositionSequence:  MutableLiveData<Int> = MutableLiveData()
    var isRevisit:  MutableLiveData<Boolean> = MutableLiveData()

    var currentSequenceDetailModel: MutableLiveData<SequenceDetailsModel> = MutableLiveData()
    var isReset: MutableLiveData<Boolean> = MutableLiveData()
    var isDelete: MutableLiveData<Boolean> = MutableLiveData()
    var isUserAdded: MutableLiveData<Boolean> = MutableLiveData()
    var newlyAddedInventoryType: MutableLiveData<InventoryTypeSelectionModel> = MutableLiveData()

    var sequenceLiveList: MutableLiveData<List<SaveSequenceModel>> = MutableLiveData()
    private var sequenceLiveListData: MutableLiveData<List<SequenceDetailsModel>> =
        MutableLiveData()
    var selectedSequenceLiveData: MutableLiveData<SaveSequenceModel> =
        MutableLiveData()

    fun getSelectSequenceModel(): MutableLiveData<List<SaveSequenceModel>> {
        return sequenceLiveList
    }

    var sequenceLegLiveList: MutableLiveData<List<ShowLegsModel>> =
        MutableLiveData()
    var selectedSequenceLegLiveData: MutableLiveData<ShowLegsModel> =
        MutableLiveData()

    fun getSelectedLegList(): MutableLiveData<List<ShowLegsModel>> {
        return sequenceLegLiveList
    }

    var inventoryFloorLiveList: MutableLiveData<List<InventoryFloorModel>> =
        MutableLiveData()
    var selectedInventoryFloorLiveData: MutableLiveData<InventoryFloorModel> =
        MutableLiveData()

    fun getSelectFloorModel(): MutableLiveData<List<InventoryFloorModel>> {
        return inventoryFloorLiveList
    }

    fun getSelectedFloor(): String? = selectedInventoryFloorLiveData.value?.floor

    var inventoryRoomLiveList: MutableLiveData<List<InventoryRoomModel>> = MutableLiveData()

    var addRoomLiveList: MutableLiveData<String> = MutableLiveData()
    var selectedInventoryRoomLiveData: MutableLiveData<InventoryRoomModel> =
        MutableLiveData()

    var inventoryCrateTypeList: MutableLiveData<List<crateTypeModel>> =
        MutableLiveData()
    var selectedCrateTypeListLiveData: MutableLiveData<crateTypeModel> =
        MutableLiveData()

    fun getSelectRoomModel(): MutableLiveData<List<InventoryRoomModel>> {
        return inventoryRoomLiveList
    }

    private var inventoryTypeLiveList: MutableLiveData<List<SurveyInventoryList>> =
        MutableLiveData()

    fun getInventoryModel(): MutableLiveData<List<SurveyInventoryList>> {
        return inventoryTypeLiveList
    }

    var selectedPosition: MutableLiveData<Int> = MutableLiveData()

    @SuppressLint("NotifyDataSetChanged")
    fun init(context: Context, userId: Int) {

        isEditable.set(false)
        isRevisit.value = false
        isUserAdded.value = false

        selectedCount.value = 0;

        Log.e("init InventoryViewModel", "init InventoryViewModel")
        if ((context as BaseActivity).inventoryListDao.getInventoryList() != null && (context as BaseActivity).inventoryListDao.getInventoryList().isNotEmpty()) {
            inventoryTypeLiveList.postValue(context.inventoryListDao.getInventoryList())

            if ((context as BaseActivity).inventoryFloorDao.getInsuranceFloorList() != null && (context as BaseActivity).inventoryFloorDao.getInsuranceFloorList()
                    .isNotEmpty()
            ) {
                inventoryFloorLiveList.postValue(context.inventoryFloorDao.getInsuranceFloorList())

                if ((context as BaseActivity).inventoryRoomDao.getInsuranceRoomList(AppConstants.surveyID.toInt()) != null && (context as BaseActivity).inventoryRoomDao.getInsuranceRoomList(
                        AppConstants.surveyID.toInt()
                    ).isNotEmpty()
                ) {
                    inventoryRoomLiveList.postValue(context.inventoryRoomDao.getInsuranceRoomList(AppConstants.surveyID.toInt()))
                } else {
                    getInventoryRoomList(context, userId, false)
                }

                // For Get Crate Type
                if ((context as BaseActivity).crateTypeDataDao.getCrateType() != null && (context as BaseActivity).crateTypeDataDao.getCrateType().isNotEmpty()) { inventoryCrateTypeList.postValue(context.crateTypeDataDao.getCrateType())
                } else {
                }
                isLoading.postValue(false)
            } else {
                getInventoryFloorList(context, userId)
            }
        } else {
            if (Utility.isNetworkConnected(context)){
                getInventoryList(context, userId)
            }

        }
        volume.set("0.0")
        count.set("1")

    }

    // For Save Room Name
    fun saveRoomName(roomId: Int, userId: Int, roomName: String, int_Type: Int, userRoomId: Int) {


        var inventoryItem =  InventoryRoomModel()
        inventoryItem.roomId = 0
        inventoryItem.surveyId = userId
        inventoryItem.room = roomName
        inventoryItem.userRoomId = 0
        inventoryItem.newRecord = true

        if (int_Type == 1) {
            var int = (context as BaseActivity).inventoryRoomDao.updateRoomName(roomId, roomName)
        }
        else{
            (context as BaseActivity).inventoryRoomDao.insert(inventoryItem)
        }


        if (context.inventoryRoomDao.getInsuranceRoomList(AppConstants.surveyID.toInt()) != null && context.inventoryRoomDao.getInsuranceRoomList(AppConstants.surveyID.toInt()
            ).isNotEmpty()
        ) {
            inventoryRoomLiveList.postValue(context.inventoryRoomDao.getInsuranceRoomList(AppConstants.surveyID.toInt()))
        } else {
            getInventoryRoomList(context, userId, false)
        }

    }

    // For Get Selected Item Image
    fun getSelectedItemImage(name: String, sequence: String) {

        if ((context as BaseActivity).sequenceDetailsDao.getInventoryItemImage(name,sequence) != null) {
            if ((context as BaseActivity).sequenceDetailsDao.getInventoryItemImage(name,sequence).itemImage !=null){
                itemimage.postValue((context as BaseActivity).sequenceDetailsDao.getInventoryItemImage(name,sequence).itemImage)
            }
            else{
                itemimage.postValue(null)
            }
        } else{
            itemimage.postValue(null)
        }
    }

    // For Get Inventory List API
    private fun getInventoryList(context: Context, userId: Int) {
        if (Utility.isNetworkConnected(context)){
            isLoading.postValue(true)
            Networking(context)
                .getServices()
                .getInventoryTypeList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CallbackObserver<BaseModel<List<SurveyInventoryList>>>() {
                    override fun onSuccess(response: BaseModel<List<SurveyInventoryList>>) {
                        inventoryTypeLiveList.postValue(response.data) // todo Ask Jaimit - Viraj
                        // isLoading.postValue(false) // todo jaimit


                        if ((context as BaseActivity).inventoryFloorDao.getInsuranceFloorList() != null && context.inventoryFloorDao.getInsuranceFloorList()
                                .isNotEmpty()
                        ) {
                            inventoryFloorLiveList.postValue(context.inventoryFloorDao.getInsuranceFloorList())

                            if (context.inventoryRoomDao.getInsuranceRoomList(AppConstants.surveyID.toInt()) != null && context.inventoryRoomDao.getInsuranceRoomList(
                                    AppConstants.surveyID.toInt()
                                ).isNotEmpty()
                            ) {
                                inventoryRoomLiveList.postValue(context.inventoryRoomDao.getInsuranceRoomList(AppConstants.surveyID.toInt()))
                            } else {
                                getInventoryRoomList(context, userId, false)
                            }
                            isLoading.postValue(false)
                        } else {
                            getInventoryFloorList(context, userId)
                        }
                    }

                    override fun onFailed(code: Int, message: String) {
                        inventoryTypeLiveList.postValue(null)

                        if ((context as BaseActivity).inventoryFloorDao.getInsuranceFloorList() != null && context.inventoryFloorDao.getInsuranceFloorList().size > 0) {
                            inventoryFloorLiveList.postValue(context.inventoryFloorDao.getInsuranceFloorList())
                        } else {
                            getInventoryFloorList(context, userId)
                        }

                    }
                })
        }
    }

    // For Get Inventory Floor List
    private fun getInventoryFloorList(context: Context, userId: Int) {
        if (Utility.isNetworkConnected(context)){
        Networking(context)
            .getServices()
            .getInventoryFloorListForDDL()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CallbackObserver<BaseModel<List<InventoryFloorModel>>>() {
                override fun onSuccess(response: BaseModel<List<InventoryFloorModel>>) {
                    //                    isLoading.postValue(false) // todo jaimit
                    inventoryFloorLiveList.postValue(response.data)

                    if ((context as BaseActivity).inventoryRoomDao.getInsuranceRoomList(AppConstants.surveyID.toInt()) != null && (context as BaseActivity).inventoryRoomDao.getInsuranceRoomList(AppConstants.surveyID.toInt()).size > 0) {
                        inventoryRoomLiveList.postValue(context.inventoryRoomDao.getInsuranceRoomList(AppConstants.surveyID.toInt()))
                    } else {
                        getInventoryRoomList(context, userId, false)
                    }
                }

                override fun onFailed(code: Int, message: String) {
                    //                    isLoading.postValue(false) // todo jaimit
                    inventoryFloorLiveList.postValue(null)


                    if ((context as BaseActivity).inventoryRoomDao.getInsuranceRoomList(AppConstants.surveyID.toInt()) != null && (context as BaseActivity).inventoryRoomDao.getInsuranceRoomList(AppConstants.surveyID.toInt()).size > 0) {
                        inventoryRoomLiveList.postValue(context.inventoryRoomDao.getInsuranceRoomList(AppConstants.surveyID.toInt()))
                    } else {
                        getInventoryRoomList(context, userId, false)
                    }
                }
            })
        }
    }
    // For Get Inventory Room  List
    private fun getInventoryRoomList(context: Context, userId: Int, fromAddRoom: Boolean) {
        //        isLoading.postValue(true) // todo jaimit
        if (Utility.isNetworkConnected(context)) {
            Networking(context)
                .getServices()
                .getInventoryRoomListForDDL(Integer.parseInt(AppConstants.surveyID))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CallbackObserver<BaseModel<List<InventoryRoomModel>>>() {
                    override fun onSuccess(response: BaseModel<List<InventoryRoomModel>>) {
                        isLoading.postValue(false)
                        inventoryRoomLiveList.postValue(response.data)
                        (context as BaseActivity).inventoryRoomDao.insertAll(response.data) // todo jaimit
                    }

                    override fun onFailed(code: Int, message: String) {
                        isLoading.postValue(false)
                        inventoryRoomLiveList.postValue(null)
                    }
                })
        }
    }


    fun postSaveInventoryDetails(context: Context, partMap: Map<String, RequestBody>, photos: MutableList<MultipartBody.Part>, model: SequenceDetailsModel?, surveyInventoryID: String?, imgFile: File?) {
        var currentTimeStamp =  System.currentTimeMillis().toInt()
        if (imgFile !=null){
            Log.e("ImageFile",imgFile.path)
            model!!.itemImage = imgFile.path
            val filePath: String = imgFile!!.path
            val bitmap = BitmapFactory.decodeFile(filePath)
            model!!.itemImage =  getByteArrayFromImageURL(bitmap)
        }
        else{
            model!!.itemImage = null
        }

        if (!TextUtils.isEmpty(surveyInventoryID.toString()) && !surveyInventoryID.equals("0")) {
            model?.isChangedField = true
            if(!model?.surveyInventoryId!!.contains("ADD")){
                model?.newRecord = false
            }
            (context as BaseActivity).sequenceDetailsDao.update(model)
        //    (context as BaseActivity).sequenceDetailsDao.updateData(model.count!!,model.inventoryNameId!!)
            Log.e("Update Item:", model?.toString().toString() + " "+ model?.inventoryNameId.toString())
        } else {
            model!!.newRecord = true
             model?.surveyInventoryId = "ADD" + System.currentTimeMillis()
            if (model?.inventoryNameId.toString().isEmpty()){
                model?.inventoryNameId = currentTimeStamp.toString()
            }
            //   "ADD" + (Random.nextInt().toString()) // todo Update at other places with System.currentTimeMillis() - Viraj
            Log.d("Add surveyInventoryId", model?.surveyInventoryId!! + " "+model?.inventoryNameId + model?.isSubItem.toString())
            (context as BaseActivity).sequenceDetailsDao.insert(model)


            // todo need to check with Chirag - Jaimit
            Log.e("@@@", ">>>>>>>>>>>>>>>>>>>> surveyId: ${model.surveyId.toString()} sequenceId: ${model.sequenceId.toString()} surveySequenceId: ${model.surveySequenceId.toString()}" + " "+model?.isSubItem.toString())
            sequenceLiveListData.postValue(context.sequenceDetailsDao.getSurveyDetailsBySurveyId(model.surveyId.toString(), model.sequenceId.toString()))



            if (model?.inventoryNameId.toString().isNotEmpty() && model?.inventoryNameId.toString().length > 5 && model!!.isSubItem == false && context.inventoryItemListDao.getInventoryListByName(model!!.inventoryItemName!!).isEmpty()){
                model?.inventoryNameId = ""
            }

            if (model.inventoryNameId == "") {

                var intLastId : Long = 0;
                intLastId =  (context as BaseActivity).inventoryItemListDao.getInventoryListLastId()

                intLastId += 1;

                val inventorytype = InventoryTypeSelectionModel(
                    intLastId,
                    currentTimeStamp,
                    model.inventoryTypeId?.toInt(),
                    model.item,
                    0,
                    model.volume?.toDouble(),
                    0,
                    0,
                    0,
                    "",
                    model.surveyId!!.toInt(),
                    1,
                    "0",
                    true,
                    isCurrentItem = false,
                    isCustomize = true,
                    inventoryValue = model.inventoryValue?.toDouble(),
                    model.inventoryNameId
                )
                newlyAddedInventoryType.value = inventorytype
                (context as BaseActivity).inventoryItemListDao.insert(inventorytype).also {
                    Log.e("Inserted Id:", it.toString())
                }

            }
            currentSequenceDetailModel.value = model
          //  isReset.value = true
        }
        var sequenceId = ""
        var sequence = ""
        if(selectedItemPositionSequence.value!! >0){
            sequenceId  = getSelectSequenceModel().value?.get(selectedItemPositionSequence.value!! - 1)?.surveySequenceId.toString()
            sequence = getSelectSequenceModel().value?.get(selectedItemPositionSequence.value!! - 1)?.surveySequence.toString()
            updateVolumeCount(model?.surveyId.toString(), sequenceId);
        }
        else{
            sequenceId  = getSelectSequenceModel().value?.get(selectedItemPositionSequence.value!!)?.surveySequenceId.toString()
            sequence = getSelectSequenceModel().value?.get(selectedItemPositionSequence.value!!)?.surveySequence.toString()
            updateVolumeCount(model?.surveyId.toString(), sequenceId);
        }
        // clearAllFields()  // todo jaimit
        getSelectedItemImage(model.inventoryItemName.toString(),sequence)
        return
    }

    private fun getByteArrayFromImageURL(bitmap: Bitmap): String? {
        val stream = ByteArrayOutputStream()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream)
        }
        // get the base 64 string
        return Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT)
    }


    private fun getSurveyInventoryItemList(itemId: String, surveyId: String, seq: SequenceDetailsModel) {
        if (Utility.isNetworkConnected(context)){
          Networking(context)
            .getServices()
            .getSurveyInventoryBySurveyId(surveyId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CallbackObserver<BaseModel<List<GetSurveyInventoryBySurveyId>>>() {
                override fun onSuccess(response: BaseModel<List<GetSurveyInventoryBySurveyId>>) {

                    if (response.success.toString() == "true") {

                        if (response.data != null && response!!.data!!.size > 0){
                            for (i in response.data!!){

                                val inventorytype = InventoryTypeSelectionModel(
                                    seq?.surveyInventoryId!!.toLong(),
                                    null,
                                    seq?.inventoryTypeId?.toInt(),
                                    seq?.item,
                                    0,
                                    seq?.volume?.toDouble(),
                                    0,
                                    0,
                                    0,
                                    "",
                                    i.SurveyId,
                                    seq?.isPackage?.let { if (it) 1 else 0 },
                                    seq?.surveyInventoryId,
                                    true,
                                    false,
                                    true,
                                    seq?.inventoryValue?.toDouble(),

                                )



                                (context as BaseActivity).inventoryItemListDao.insert(inventorytype).also {
                                    Log.e("Inserted Id:", it.toString())
                                }
                            }
                        }

                    } else if (response.success.toString() == "false" && response.message.isNotEmpty())
                        MessageDialog(context, response.message).show()
                    else
                        MessageDialog(
                            context,
                            context.getString(R.string.something_went_wrong)
                        ).show()
                }

                override fun onFailed(code: Int, message: String) {
                    Log.e("SurveyInventoryItemList", "onFailed$message")
                }
            })
        }
    }


     fun updateVolumeCount(surveyId: String?, sequenceId: String) {
        val volume = (context as BaseActivity).sequenceDetailsDao.getSumVolume(surveyId!!,sequenceId,"0")
        if (volume != null && volume.toString() != "null") {
            totalVolume.postValue(volume.toString())
            Log.e("TotalVolume",totalVolume.value.toString())
        } else {
            totalVolume.postValue("0.0")
        }
    }

    private fun clearAllFields() {
        itemName.set("")
        isPackingType.set(0)
        itemEnabled.set(false)
        volume.set("20")
        count.set("1")
        //  isRefreshList.postValue(true) //  todo jaimit
        isRefreshList.postValue(false)  // todo Ask Jaimit - Viraj
    }

    // For Update Leg List
    fun getLegList(){
        if (sequenceLiveList.value != null && sequenceLiveList.value!!.size >= selectedItemPosition && selectedItemPosition != 0) {
            selectedSequenceLiveData.value = sequenceLiveList.value!![selectedItemPosition - 1].also {
                it.position = selectedItemPosition
            }
            sequenceLegLiveList.postValue(null)
            if ((context as BaseActivity).showLegsDao.getShowLegsList() != null && context.showLegsDao.getShowLegsBySurveySequenceId(
                    selectedSequenceLiveData.value!!.surveySequenceId
                ).isNotEmpty()
            ) {
                sequenceLegLiveList.postValue(context.showLegsDao.getShowLegsBySurveySequenceId(selectedSequenceLiveData.value!!.surveySequenceId))
            } else {
                sequenceLegLiveList.postValue(null)
              //  getInventoryLegList(context, selectedSequenceLiveData.value!!.surveySequenceId)
            }
        }
    }

    fun getSequenceList(surveyId: String?, context: Context) {
        if ((context as BaseActivity).saveSequenceDao.getSequenceList() != null && (context as BaseActivity).saveSequenceDao.getSequenceListBySurveyId(surveyId!!.toString(),false).isNotEmpty()) {
            sequenceLiveList.postValue((context as BaseActivity).saveSequenceDao.getSequenceListBySurveyId(surveyId,false))
        } else {
            sequenceLiveList.postValue(null)
        }
    }

    // Delete Sequence Inventory Item
    fun deleteSequenceInventoryItem(sequenceInventoryItemId: String,context: Context, surveyId: String, itemName: String) {
        if (sequenceInventoryItemId.contains("ADD")) {
            var isUserAdd =  (context as BaseActivity).sequenceDetailsDao.getUserAddedOrNot(sequenceInventoryItemId)
            Log.e("IsUserAdd",isUserAdded.toString())
            if(isUserAdd){
                (context as BaseActivity).inventoryItemListDao.deleteUserAddedItem(itemName)
                isUserAdded.postValue(true)
                isDelete.postValue(true)
            }
            else{
                isUserAdded.postValue(false)
            }
            (context as BaseActivity).sequenceDetailsDao.delete(sequenceInventoryItemId)
        } else {
            (context as BaseActivity).sequenceDetailsDao.updateIsDelete(
                sequenceInventoryItemId,
                true
            )
        }
        isDelete.value = true
        if(selectedItemPositionSequence.value!! >0){
            var sequenceId  = getSelectSequenceModel().value?.get(selectedItemPositionSequence.value!! - 1)?.surveySequenceId.toString()
            updateVolumeCount(surveyId, sequenceId);
        }
        else{
            var sequenceId  = getSelectSequenceModel().value?.get(selectedItemPositionSequence.value!!)?.surveySequenceId.toString()
            updateVolumeCount(surveyId, sequenceId);
        }
    }

    // Get Survey Detail Item
    fun getSurveyDetailByInventoryItem(context: Context, surveyInventoryId: String): SequenceDetailsModel? { if ((context as BaseActivity).sequenceDetailsDao.getSurveyDetailsList().isNotEmpty()) {
            var sequenceInventoryItem: SequenceDetailsModel? = null
            if (surveyInventoryId.isNotBlank()) {
                sequenceInventoryItem = context.sequenceDetailsDao.getSurveyDetailByInventoryItem(surveyInventoryId)
                Log.e("@@@", "sequenceInventoryItemList => ${sequenceInventoryItem.toString()}")
            }
            return sequenceInventoryItem
        }
        return null
    }

    fun getSurveyInventoryItem(
        context: Context,
        selectedSequence: String,
        surveyId: String,
        selectedFloor: String,
        selectedRoom: String,
        inventoryTypeId: String,
        itemName: String,
        selectedLeg: String,
        inventoryNameId: String
    ): SequenceDetailsModel? {
        if ((context as BaseActivity).sequenceDetailsDao.getSurveyDetailsList().isNotEmpty()) {

            var sequenceInventoryItem: SequenceDetailsModel? = null
            if (surveyId.isNotBlank() && selectedFloor.isNotBlank() && selectedRoom.isNotBlank() && selectedSequence.isNotBlank() && inventoryTypeId.isNotBlank() && itemName.isNotBlank()) {

                isEditable.set(false)


                Log.e(
                    "QueryParams: ",
                    "surveyId: $surveyId, selectedFloor: $selectedFloor, selectedRoom: $selectedRoom," +
                            "selectedSequence: $selectedSequence, selectedLeg: $selectedLeg, inventoryTypeId: $inventoryTypeId, itemName: $itemName, inventoryNameId: $inventoryNameId"
                )

                if (inventoryNameId.equals("")) {
                    if (selectedLeg.isNotEmpty() && !selectedLeg.lowercase().equals("null")) {
                        sequenceInventoryItem =
                            context.sequenceDetailsDao.getSurveyDetailsBySurveyIdFRSLIIItem(
                                selectedSequence,
                                surveyId,
                                selectedFloor,
                                selectedRoom,
                                inventoryTypeId,
                                itemName,
                                selectedLeg
                            )
                    } else {
                        sequenceInventoryItem =
                            context.sequenceDetailsDao.getSurveyDetailsBySurveyIdFRSIIItem(
                                selectedSequence,
                                surveyId,
                                selectedFloor,
                                selectedRoom,
                                itemName
                            )
                    }
                } else {
                    if (selectedLeg.isNotEmpty() && !selectedLeg.lowercase().equals("null")) {
                        sequenceInventoryItem =
                            context.sequenceDetailsDao.getSurveyDetailsBySurveyIdFRSLIIItem(
                                selectedSequence,
                                surveyId,
                                selectedFloor,
                                selectedRoom,
                                inventoryTypeId,
                                itemName,
                                selectedLeg,
                                inventoryNameId
                            )
                    } else {
                        sequenceInventoryItem =
                            context.sequenceDetailsDao.getSurveyDetailsBySurveyIdFRSIIItem(
                                selectedSequence,
                                surveyId,
                                selectedFloor,
                                selectedRoom,
                                itemName
                            )
                    }
                }
                Log.e("@@@", "sequenceInventoryItem: ${sequenceInventoryItem.toString()}")
            }
            return sequenceInventoryItem
        }
        return null
    }

    // Sequence Click Listener
    val sequenceClicksListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {
            selectedItemPosition = 0
        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            isReset.value = true
            if (sequenceLiveList.value != null && sequenceLiveList.value!!.size >= position && position != 0) {
                selectedSequenceLiveData.value = sequenceLiveList.value!![position - 1].also {
                    it.position = position
                    selectedItemPosition = position
                    selectedItemPositionSequence.value = position
                    AppConstants.revisitPlanning =  sequenceLiveList.value!![position - 1].isRevisit
                    isRevisit.value = sequenceLiveList.value!![position - 1].isRevisit
                    selectedPosition.postValue(position)
                }

                if ((context as BaseActivity).showLegsDao.getShowLegsList() != null && context.showLegsDao.getShowLegsBySurveySequenceId(selectedSequenceLiveData.value!!.surveySequenceId).size > 0) {
                    sequenceLegLiveList.postValue(
                        context.showLegsDao.getShowLegsBySurveySequenceId(
                            selectedSequenceLiveData.value!!.surveySequenceId
                        )
                    )
                } else {
                    sequenceLegLiveList.postValue(null)
                   // getInventoryLegList(context, selectedSequenceLiveData.value!!.surveySequenceId)
                }
            } else {
                selectedSequenceLiveData.value = SaveSequenceModel().also { it.position = 0 }
                sequenceLegLiveList.postValue(null)
                selectedItemPosition = 0
                selectedItemPositionSequence.value = 0
                selectedPosition.postValue(0)
            }
        }
    }
    // Leg  Click Listener
    val legClicksListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {

        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

            if (sequenceLegLiveList != null && sequenceLegLiveList.value != null && sequenceLegLiveList.value!!.size >= position && position != 0) {
                selectedSequenceLegLiveData.value = sequenceLegLiveList.value!![position - 1]
               Log.d("item","if LEg")
            } else {
                selectedSequenceLegLiveData.value = ShowLegsModel()
                Log.d("item","else Leg")
            }
        }
    }
    // Floor  Click Listener
    val floorClicksListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {
        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            if (inventoryFloorLiveList != null && inventoryFloorLiveList.value != null && inventoryFloorLiveList.value!!.size >= position && position != 0) {
                selectedInventoryFloorLiveData.value = inventoryFloorLiveList.value!![position - 1]
                /*selectedFloorPosition.value = position
                selectedFloorPos = position*/
            } else {
                selectedInventoryFloorLiveData.value = InventoryFloorModel()
            }
        }
    }

 // Room Click Listener
    val roomClicksListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {
        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

            if (position == 1) {
                addRoomLiveList.value = "Add Data"
            } else {
                if (inventoryRoomLiveList != null && inventoryRoomLiveList.value != null && inventoryRoomLiveList.value!!.size >= position && position != 0) {
                    selectedInventoryRoomLiveData.value = inventoryRoomLiveList.value!![position - 2]
                } else if (inventoryRoomLiveList != null && inventoryRoomLiveList.value != null && inventoryRoomLiveList.value!!.size <= position && position != 0) {
                    selectedInventoryRoomLiveData.value = inventoryRoomLiveList.value!![position - 2]
                } else {
                    selectedInventoryRoomLiveData.value = InventoryRoomModel()
                }

            }

        }
    }
}