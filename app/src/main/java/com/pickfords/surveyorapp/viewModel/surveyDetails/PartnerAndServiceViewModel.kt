package com.pickfords.surveyorapp.viewModel.surveyDetails

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.pickfords.surveyorapp.base.BaseViewModel
import com.pickfords.surveyorapp.model.DeletePartnerAndServiceContactModel
import com.pickfords.surveyorapp.model.PartnerListModel
import com.pickfords.surveyorapp.model.PartnerServiceModel
import com.pickfords.surveyorapp.model.dashboard.DashboardModel
import com.pickfords.surveyorapp.model.surveyDetails.AdditionalInfoPartnerModel
import com.pickfords.surveyorapp.model.surveyDetails.PartnerTempModel
import com.pickfords.surveyorapp.network.BaseModel
import com.pickfords.surveyorapp.network.CallbackObserver
import com.pickfords.surveyorapp.network.Networking
import com.pickfords.surveyorapp.utils.AppConstants
import com.pickfords.surveyorapp.utils.Session
import com.pickfords.surveyorapp.utils.Utility
import com.pickfords.surveyorapp.view.BaseActivity
import com.pickfords.surveyorapp.view.dialogs.DataSavedDialog
import com.pickfords.surveyorapp.view.surveyDetails.AdditionalDetailAdapter
import com.pickfords.surveyorapp.view.surveyDetails.PartnerAndServiceAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONArray
import org.json.JSONObject


//todo Pranav Shah & janki

class PartnerAndServiceViewModel : BaseViewModel() {

    lateinit var context: BaseActivity

    private var additionalDetailAdapter: AdditionalDetailAdapter? = null
    fun getAdditionalDetailAdapter(): AdditionalDetailAdapter? = additionalDetailAdapter

    private var partnerAndServiceAdapter: PartnerAndServiceAdapter? = null
    fun getPartnerAndServiceAdapter(): PartnerAndServiceAdapter? = partnerAndServiceAdapter

    private var additionalInfoList: ArrayList<AdditionalInfoPartnerModel> = arrayListOf()
    private var additionalInfoLiveList: MutableLiveData<List<AdditionalInfoPartnerModel>> =
        MutableLiveData()


    // parent list value
    private val partnerList: ArrayList<PartnerListModel> = arrayListOf()
    private var partnerLiveList: MutableLiveData<List<PartnerListModel>> = MutableLiveData()


    private var selectedDashboradData: DashboardModel? = null

    fun init(context: Context, selectedData: DashboardModel?) {
        this.context = context as BaseActivity

        selectedDashboradData = selectedData
        partnerAndServiceAdapter = PartnerAndServiceAdapter(context, partnerList, this)
        partnerLiveList.observeForever {
            if (it != null) {
                partnerList.clear()
                partnerList.addAll(it)
                partnerAndServiceAdapter?.notifyDataSetChanged()
            }
        }

        additionalDetailAdapter = AdditionalDetailAdapter(additionalInfoList, selectedDashboradData,
            object : AdditionalDetailAdapter.ContactClickListener {
                override fun removeContact(position: Int, id: Int?, context: Context) {
                    dialogRemove(id, position)
                }

            })
        additionalInfoLiveList.observeForever {
            if (it != null) {
                additionalInfoList.clear()
                additionalInfoList.addAll(it)
                additionalDetailAdapter?.notifyDataSetChanged()
            }
            else{
                additionalInfoList.clear()
                additionalDetailAdapter?.notifyDataSetChanged()
            }
        }


        if (context.partnerAndServiceDao.getPartnerList()
                .isNotEmpty()
        ) {
            partnerLiveList.postValue(context.partnerAndServiceDao.getPartnerList())
            if (context.additionalInfo.getAdditionlInfo(selectedDashboradData!!.surveyId) != null && context.additionalInfo.getAdditionlInfo(
                    selectedDashboradData!!.surveyId
                )
                    .isNotEmpty()
            ) {
                additionalInfoLiveList.postValue(
                    context.additionalInfo.getAdditionlInfo(
                        selectedDashboradData!!.surveyId
                    )
                )
            }else{
                additionalInfoLiveList.postValue(null)
            }
        } else {

            getPartnerList()
        }
    }

    // Alert For Remove Partner List
    private fun dialogRemove(id: Int?, position: Int) {
        val builder = AlertDialog.Builder(context)
        //Uncomment the below code to Set the message and title from the strings.xml file
        builder.setTitle("Alert!")

        //Setting message manually and performing action on button click
        builder.setMessage("Are you sure you want to remove contact detail?")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->


                val delete = DeletePartnerAndServiceContactModel()
                delete.partnerId = id!!
                delete.IsDelete = false

                context.deletePartnerAndServiceContactDao.insert(delete)
                context.additionalInfo.delete(id.toString())

                additionalInfoList.removeAt(position)
                additionalDetailAdapter?.updateList(additionalInfoList)

            }
            .setNegativeButton(
                "No"
            ) { dialog, _ -> //  Action for 'NO' Button
                dialog.cancel()
            }
        //Creating dialog box
        val alert: AlertDialog = builder.create()
        alert.show()

    }


    fun updateAdditionalAdapter(selectedData: DashboardModel?) {
        additionalInfoList.add(AdditionalInfoPartnerModel(0))
        additionalDetailAdapter?.updateList(additionalInfoList)
    }

    // For get Partner List API
    fun getPartnerList() {
        if (Utility.isNetworkConnected(context)) {
            isLoading.postValue(true)
            Networking(context)
                .getServices()
                .getPartnerServiceList(AppConstants.surveyID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CallbackObserver<PartnerServiceModel>() {
                    override fun onSuccess(response: PartnerServiceModel) {
                        Log.d("PartnerServiceList", response.data.toString())
                        isLoading.postValue(false)
                        partnerLiveList.postValue(response.data?.partnerList)
                        additionalInfoLiveList.postValue(response.data?.additionList)

                        if (response.message == "Success") {
                            if (response.data != null && !response.data.additionList.isNullOrEmpty()) {
                                for (i in response.data.additionList) {
                                    i.surveyId = AppConstants.surveyID
                                    i.newRecord = false
                                    context.additionalInfo.insert(response.data?.additionList)
                                }
                            }
                        }

                        context.partnerAndServiceDao.insertAllPartnerList(response.data?.partnerList)

                    }

                    override fun onFailed(code: Int, message: String) {
                        isLoading.postValue(false)
                    }
                })
        }
    }

    // For Save Partner List
     fun saveListApi() {
        var selectPartnerId : Int = 0
        val obj = JSONObject()
        obj.put("SurveyId", AppConstants.surveyID)
        var partnerId = ""
        for (i in 0 until partnerList.size)
            if (partnerList[i].IsLink == true) {
                partnerId = partnerId + partnerList[i].Id + ","
            }
        if (partnerId.isNotEmpty()) {
            obj.put("PartnerId", partnerId.substring(0, partnerId.length - 1))
        } else {
            isLoading.postValue(false)
            Toast.makeText(
                context,
                "Please select at least one partner !",
                Toast.LENGTH_LONG
            ).show()
            return
        }


        val jsonArray = JSONArray()

        for (i in 0 until additionalInfoList.size) {

            val jObj = JSONObject()
            additionalInfoList[i].surveyId = selectedDashboradData!!.surveyId
            jObj.put("AdditionalPersonalId", additionalInfoList[i].Id)
            jObj.put("FirstName", additionalInfoList[i].firstName)
            jObj.put("LastName", additionalInfoList[i].lastName)
            jObj.put("Email", additionalInfoList[i].email)
            jObj.put("Phone", additionalInfoList[i].phone)
            jObj.put("Comments", additionalInfoList[i].comments)
            jObj.put("PartnerId", partnerId)
            jObj.put("IsNew", true)

            jsonArray.put(jObj)
        }
        obj.put("AdditionalPersonalList", jsonArray)
        Log.e("request", obj.toString())



        val partnerTempModel = PartnerTempModel()
        partnerTempModel.saveDetails = obj.toString()
        partnerTempModel.isSync = true
        if (selectedDashboradData != null) {
            partnerTempModel.surveyID = selectedDashboradData?.surveyId.toString()
            if (!context.partnerTempDao.getPartnerTempListBySurveyOffline(partnerTempModel.surveyID).isNullOrEmpty()
            ) {
                context.partnerTempDao.update(partnerTempModel.saveDetails,partnerTempModel.surveyID)
            } else {
                context.partnerTempDao.insert(partnerTempModel)
            }
        }
      // var id =  context.additionalInfo.getPartnerIsAvailable(additionalInfoList[i].firstName)
        context.partnerAndServiceDao.updateAllAdditionalPartnerList(additionalInfoList)
        context.partnerAndServiceDao.updateAllPartnerList(partnerList)
        context.additionalInfo.insert(additionalInfoList)
        DataSavedDialog(context, "Additional Personal detail saved successfully.").show()

    }

    // For Save Partner List
    fun savePartnerApi(selectedData: DashboardModel?) {

        val obj = JSONObject()
        obj.put("SurveyId", AppConstants.surveyID)
        var partnerId = ""
        for (i in 0 until partnerList.size)
            if (partnerList[i].IsLink == true) {
                partnerId = partnerId + partnerList[i].Id + ","
            }
        if (partnerId.isNotEmpty()) {
            obj.put("PartnerId", partnerId.substring(0, partnerId.length - 1))
        } else {
            isLoading.postValue(false)
            Toast.makeText(
                context,
                "Please select at least one partner !",
                Toast.LENGTH_LONG
            ).show()
            return
        }


        val jsonArray = JSONArray()

        for (i in 0 until additionalInfoList.size) {

            val jObj = JSONObject()
            additionalInfoList[i].surveyId = selectedDashboradData!!.surveyId
            jObj.put("AdditionalPersonalId", additionalInfoList[i].Id)
            jObj.put("FirstName", additionalInfoList[i].firstName)
            jObj.put("LastName", additionalInfoList[i].lastName)
            jObj.put("Email", additionalInfoList[i].email)
            jObj.put("Phone", additionalInfoList[i].phone)
            jObj.put("Comments", additionalInfoList[i].comments)
            jObj.put("PartnerId", partnerId)
            if (!Utility.isNetworkConnected(context)) {
                jObj.put("IsNew", true)
            } else {
                if (!Utility.isNetworkConnected(context)) {
                    jObj.put("IsNew", false)
                }
            }
            jsonArray.put(jObj)
        }
        obj.put("AdditionalPersonalList", jsonArray)
        Log.e("request", obj.toString())

        val partnerTempModel = PartnerTempModel()
        partnerTempModel.saveDetails = obj.toString()
        partnerTempModel.isSync = true
        if (selectedDashboradData != null) {
            partnerTempModel.surveyID = selectedDashboradData?.surveyId.toString()
            if (!context.partnerTempDao.getPartnerTempListBySurveyOffline(
                    partnerTempModel.surveyID
                ).isNullOrEmpty()
            ) {
                context.partnerTempDao.update(partnerTempModel.saveDetails,partnerTempModel.surveyID)
            } else {
                context.partnerTempDao.insert(partnerTempModel)
            }
        }
        context.partnerAndServiceDao.updateAllAdditionalPartnerList(
            additionalInfoList
        )
        context.partnerAndServiceDao.updateAllPartnerList(partnerList)
        context.additionalInfo.insert(additionalInfoList)
        DataSavedDialog(context, "Additional Personal detail saved successfully.").show()
    }

    fun validate(context: Context?) {
        if (additionalInfoList != null && additionalInfoList.size > 0) {
            if (hasDuplicates(additionalInfoList)) {
                Toast.makeText(
                    context,
                    "Empty or Repeated elements found for first name",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                saveListApi()
            }
        }
    }

    private fun hasDuplicates(arr: List<AdditionalInfoPartnerModel>): Boolean {
        for (i in arr.indices) {
            for (j in i + 1 until arr.size) {
                if (arr[i].firstName.toString().isEmpty() || arr[j].firstName.toString()
                        .isEmpty() || arr[i].firstName == arr[j].firstName
                ) {
                    return true
                }
            }
        }
        return false
    }

}