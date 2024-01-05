package com.pickfords.surveyorapp.viewModel.surveyDetails

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.pickfords.surveyorapp.R
import com.pickfords.surveyorapp.base.BaseViewModel
import com.pickfords.surveyorapp.databinding.FragmentAddressesBinding
import com.pickfords.surveyorapp.model.address.AddressListModel
import com.pickfords.surveyorapp.model.address.AddressTypeListModel
import com.pickfords.surveyorapp.model.address.CountryListModel
import com.pickfords.surveyorapp.model.address.PropertyTypeModel
import com.pickfords.surveyorapp.model.surveyDetails.DistanceUnitTypeModel
import com.pickfords.surveyorapp.model.surveyDetails.LegPermitModel
import com.pickfords.surveyorapp.network.BaseModel
import com.pickfords.surveyorapp.network.CallbackObserver
import com.pickfords.surveyorapp.network.Networking
import com.pickfords.surveyorapp.utils.AppConstants
import com.pickfords.surveyorapp.utils.Session
import com.pickfords.surveyorapp.utils.Utility
import com.pickfords.surveyorapp.view.BaseActivity
import com.pickfords.surveyorapp.view.dialogs.MessageDialog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_survey_details_sequence.*
import org.json.JSONException
import org.json.JSONObject
import kotlin.random.Random


class AddressesViewModel(var context: Context) : BaseViewModel() {
    private val countryList: MutableList<String?> = mutableListOf()
    private var countryLiveList: MutableLiveData<List<CountryListModel>> = MutableLiveData()
    private var countrySpinnerAdapter: ArrayAdapter<String?>? = null
    fun getCountrySpinnerAdapter(): ArrayAdapter<String?>? = countrySpinnerAdapter


    private var isFirstCall = true
    var selectedAddressType: MutableLiveData<Int> = MutableLiveData()
    var selectedPosition: MutableLiveData<Int> = MutableLiveData()
    private val addressList: MutableList<String?> = mutableListOf()
    private var addressLiveList: MutableLiveData<List<AddressListModel>> = MutableLiveData()
    private var addressSpinnerAdapter: ArrayAdapter<String?>? = null
    fun getAddressLiveList(): MutableLiveData<List<AddressListModel>> = addressLiveList
    fun getAddressSpinnerAdapter(): ArrayAdapter<String?>? = addressSpinnerAdapter

    fun getAddressList(): ArrayAdapter<String?>? =
        sequenceLegDestinationDistanceTypeSpinnerAdapter

    // distance unit list code
    private var sequenceLegDestinationDistanceTypeSpinnerAdapter: ArrayAdapter<String?>? = null
    fun getSequenceLegDestinationDistanceTypeSpinnerAdapter(): ArrayAdapter<String?>? =
        sequenceLegDestinationDistanceTypeSpinnerAdapter

    private val sequenceLegDestinationDistanceTypeList: MutableList<String?> = mutableListOf()
    private var sequenceLegDestinationDistanceTypeLiveList: MutableLiveData<List<DistanceUnitTypeModel>> =
        MutableLiveData()

    fun getSequenceLegDestinationDistanceTypeLiveList(): MutableLiveData<List<DistanceUnitTypeModel>> {
        return sequenceLegDestinationDistanceTypeLiveList
    }

    // Permit Code
    private var sequenceLegPermitDestinationSpinnerAdapter: ArrayAdapter<String?>? = null
    fun getSequenceLegPermitDestinationSpinnerAdapter(): ArrayAdapter<String?>? =
        sequenceLegPermitDestinationSpinnerAdapter

    private val sequenceLegDestinationPermitList: MutableList<String?> = mutableListOf()
    private var sequenceLegPermitDestinationLiveList: MutableLiveData<List<LegPermitModel>> =
        MutableLiveData()

    fun getSequenceLegPermitDestinationLiveList(): MutableLiveData<List<LegPermitModel>> {
        return sequenceLegPermitDestinationLiveList
    }


    ///////////////////////////////////////////////////////////////////////////////////
    //   For PropertyTypeType List Dropdown

    private var propertyTypeList: MutableList<String?> = mutableListOf()
    var propertyTypeLiveList: MutableLiveData<List<PropertyTypeModel>> = MutableLiveData()
    private var propertyTypeSpinnerAdapter: ArrayAdapter<String?>? = null
    fun getpropertyTypeSpinnerAdapter(): ArrayAdapter<String?>? =
        propertyTypeSpinnerAdapter

    fun getpropertyTypeLiveList(): MutableLiveData<List<PropertyTypeModel>> {
        return propertyTypeLiveList
    }


    ///////////////////////////////////////////////////////////////////////////////////
    //                For Address Type  List Dropdown

    private var addressTypeList: MutableList<String?> = mutableListOf()
    var addressTypeLiveList: MutableLiveData<List<AddressTypeListModel>> = MutableLiveData()
    private var addressTypeSpinnerAdapter: ArrayAdapter<String?>? = null
    fun getAddressTypeSpinnerAdapter(): ArrayAdapter<String?>? =
        addressTypeSpinnerAdapter

    fun getAddressTypeLiveListData(): MutableLiveData<List<AddressTypeListModel>> {
        return addressTypeLiveList
    }

    /////////////////////////////////////////////////////////////////////////////////////////
    fun getCountryLiveList(): MutableLiveData<List<CountryListModel>> {
        return countryLiveList
    }

    private var selectedAddressLiveData: MutableLiveData<AddressListModel> = MutableLiveData()
    private var selectedAddressTypeLiveData: MutableLiveData<AddressTypeListModel> =
        MutableLiveData()


    fun getSelectAddressModel(): MutableLiveData<AddressListModel> {
        return selectedAddressLiveData
    }


    fun getSelectAddressTypeModel(): MutableLiveData<AddressTypeListModel> {
        return selectedAddressTypeLiveData
    }

    private var surveyID: String = ""
    private var clearAll = true

    var isGetAddressFromType = false

    private var selectedAddress: String? = ""

    @SuppressLint("NotifyDataSetChanged")
    fun init(context: Context/*, selectedData: DashboardModel?*/) {
        val root = JSONObject()
        try {
            root.put(
                "planningList",
                (context as BaseActivity).surveyPlanningListDao.getPlanningList()
            )
            root.put("commentList", (context as BaseActivity).commentsDetailDao.getCommentsList())
        } catch (e: JSONException) {
            e.printStackTrace()
        }



        countrySpinnerAdapter =
            ArrayAdapter<String?>(context, android.R.layout.simple_spinner_item, countryList)
        countrySpinnerAdapter?.setDropDownViewResource(R.layout.custom_spinner_item)

        addressSpinnerAdapter =
            ArrayAdapter<String?>(context, android.R.layout.simple_spinner_item, addressList)
        addressSpinnerAdapter?.setDropDownViewResource(R.layout.custom_spinner_item)

        addressTypeSpinnerAdapter =
            ArrayAdapter<String?>(context, android.R.layout.simple_spinner_item, addressTypeList)
        addressTypeSpinnerAdapter?.setDropDownViewResource(R.layout.custom_spinner_item)

        propertyTypeSpinnerAdapter =
            ArrayAdapter<String?>(context, android.R.layout.simple_spinner_item, propertyTypeList)
        propertyTypeSpinnerAdapter?.setDropDownViewResource(R.layout.custom_spinner_item)


        countryLiveList.observeForever {
            if (it != null) {
                countryList.clear()
                countryList.add("Select Country")
                for (data in it) {
                    countryList.add(data.countryName)
                }
                countrySpinnerAdapter?.notifyDataSetChanged()
            }
        }

        // To Fill Array for Address Type List
        addressTypeLiveList.observeForever {
            if (it != null) {
                addressTypeList.clear()
                addressTypeList.add("Select Address Type")
                for (data in it) {
                    addressTypeList.add(data.description)
                }
                addressTypeSpinnerAdapter?.notifyDataSetChanged()
            }
        }


        // To Fill Array for Property Type List
        propertyTypeLiveList.observeForever {
            if (it != null) {
                propertyTypeList.clear()
                for (data in it) {
                    if (data.propertyCode == "0") {
                        data.propertyType = "Select Property Type"
                    }
                    propertyTypeList.add(data.propertyType)
                }
                propertyTypeSpinnerAdapter?.notifyDataSetChanged()
            }
        }


        // To Fill Array for Address  List
        addressLiveList.observeForever {
            if (it != null) {
                Log.e("addressList1: ", " " + addressList.size + " ")
                if (clearAll) {
                    addressList.clear()
                    addressList.add("Select Address")
                }
                for (data in it) {
                    addressList.add(data.titleName.toString())
                }
                Log.e("addressList2: ", " " + addressList.size + " ")
                addressSpinnerAdapter?.notifyDataSetChanged()

                try {
                    if (!isFirstCall) {
                        selectedPosition.value = 0//addressList.size - 1
                    } else {
                        if (addressList.size > 1) {
                            selectedPosition.value = 1
                        }
                    }
                } catch (e: Exception) {
                    Log.e("addressList2: ", " " + e.printStackTrace() + " ")
                }
                Log.e("addressList: ", " " + selectedPosition.value + " ")
                clearAll = true


            }
        }

        if ((context as BaseActivity).countryListDao.getCountryList() != null && (context as BaseActivity).countryListDao.getCountryList().size > 0) {
            countryLiveList.postValue((context as BaseActivity).countryListDao.getCountryList())
        } else {
            getCountryList(context)
        }


        //  For Get Property Type List
        if ((context as BaseActivity).propertyTypeDao.getPropertyType() != null && context.propertyTypeDao.getPropertyType()
                .isNotEmpty()
        ) {
            propertyTypeLiveList.postValue((context as BaseActivity).propertyTypeDao.getPropertyType())
        } else {
            getPropertyTypeList(context)
        }


        //  For Get AddressType List
        if (context.addressTypeListModelDao.getAddressTypeList().isNotEmpty()) {
            addressTypeLiveList.postValue(context.addressTypeListModelDao.getAddressTypeList())
        } else {
            getAddressTypeList(context)
        }

        // Distance code
        sequenceLegDestinationDistanceTypeLiveList.observeForever {
            if (it != null) {
                sequenceLegDestinationDistanceTypeList.clear()
                for (data in it) {
                    sequenceLegDestinationDistanceTypeList.add(data.distanceUnit)
                }
                sequenceLegDestinationDistanceTypeSpinnerAdapter?.notifyDataSetChanged()
            }
        }
        sequenceLegDestinationDistanceTypeSpinnerAdapter = ArrayAdapter<String?>(
            context,
            android.R.layout.simple_spinner_item,
            sequenceLegDestinationDistanceTypeList
        )
        sequenceLegDestinationDistanceTypeSpinnerAdapter?.setDropDownViewResource(R.layout.custom_spinner_item)

        // Permit code
        sequenceLegPermitDestinationSpinnerAdapter = ArrayAdapter<String?>(
            context,
            android.R.layout.simple_spinner_item,
            sequenceLegDestinationPermitList
        )
        sequenceLegPermitDestinationSpinnerAdapter?.setDropDownViewResource(R.layout.custom_spinner_item)
        sequenceLegPermitDestinationLiveList.observeForever {
            if (it != null) {
                sequenceLegDestinationPermitList.clear()
                for (data in it) {
                    sequenceLegDestinationPermitList.add(data.name)
                }
                sequenceLegPermitDestinationSpinnerAdapter?.notifyDataSetChanged()
            }
        }


    //    getDistanceTypeUnit(context)
        if (context.legPermitDao.getLegPermitList() != null &&
            context.legPermitDao.getLegPermitList().isNotEmpty()
        ) {

            sequenceLegPermitDestinationLiveList.postValue(context.legPermitDao.getLegPermitList())

            if (context.distanceUnitTypeDao.getDistanceUnitList() != null &&
                context.distanceUnitTypeDao.getDistanceUnitList().isNotEmpty()
            ) {
                sequenceLegDestinationDistanceTypeLiveList.postValue(context.distanceUnitTypeDao.getDistanceUnitList())
            } else {
              //  getDistanceTypeUnit(context)
            }
        } else {
            getSequenceOriginLegsPermitList(context)
        }
    }

    private fun getPropertyTypeList(context: BaseActivity) {
        isLoading.postValue(true)
        Networking(context)
            .getServices()
            .getPropertyType()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CallbackObserver<BaseModel<List<PropertyTypeModel>>>() {
                override fun onSuccess(response: BaseModel<List<PropertyTypeModel>>) {
                    isLoading.postValue(false)
                    propertyTypeLiveList.postValue(response.data)
                    (context as BaseActivity).propertyTypeDao.insertAll(response.data)
                }

                override fun onFailed(code: Int, message: String) {
                    isLoading.postValue(false)
                    propertyTypeLiveList.postValue(null)
                }
            })
    }

    // Return Country List API
    private fun getCountryList(context: Context) {
        isLoading.postValue(true)
        Networking(context)
            .getServices()
            .getCountryList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CallbackObserver<BaseModel<List<CountryListModel>>>() {
                override fun onSuccess(response: BaseModel<List<CountryListModel>>) {
                    isLoading.postValue(false)
                    countryLiveList.postValue(response.data)
                    (context as BaseActivity).countryListDao.insertAll(response.data)
                }

                override fun onFailed(code: Int, message: String) {
                    isLoading.postValue(false)
                    countryLiveList.postValue(null)
                }

            })
    }

    // Return Address Type List API
    private fun getAddressTypeList(context: Context) {
        isLoading.postValue(true)
        Networking(context)
            .getServices()
            .getAddressTypeList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CallbackObserver<BaseModel<List<AddressTypeListModel>>>() {
                override fun onSuccess(response: BaseModel<List<AddressTypeListModel>>) {
                    isLoading.postValue(false)
                    addressTypeLiveList.postValue(response.data)
                    //  (context as BaseActivity).permitTypeListModelListDao.insertAll(response.data)
                }

                override fun onFailed(code: Int, message: String) {
                    isLoading.postValue(false)
                    addressTypeLiveList.postValue(null)
                }

            })
    }

    // Return Address  List API
    fun getAddressList(
        context: Context,
        surveyID: String,
        flag: Int = 1,
        addressBinding: FragmentAddressesBinding?
    ) {
        val session = Session(context)
        isLoading.postValue(true)
        Networking(context)
            .getServices()
            .getSurveyAddressListBySurveyId(surveyID, session.user!!.userId!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CallbackObserver<BaseModel<List<AddressListModel>>>() {
                override fun onSuccess(response: BaseModel<List<AddressListModel>>) {
                    isLoading.postValue(false)
                    addressLiveList.postValue(response.data)
                    (context as BaseActivity).addressListDao.insertAll(response.data)
                }

                override fun onFailed(code: Int, message: String) {
                    isLoading.postValue(false)
                    addressLiveList.postValue(ArrayList())
                }
            })
    }

    // Return Save Survey Address
    fun saveSurveyAddress(data: AddressListModel?, addressBinding: FragmentAddressesBinding) {


        if (data?.isAddressIdInitialized() == true) {

            var oldAddressTitle : String =   (context as BaseActivity).addressListDao.getAddressTittle(data!!.surveyAddressId.toString());
            var newAddressTitle : String =   ""


            data.isChangedField = true
            (context as BaseActivity).addressListDao.update(data)
            Utility.setOfflineTag(context, "Address update successfully")

            newAddressTitle =  (context as BaseActivity).addressListDao.getAddressTittle(data!!.surveyAddressId.toString());

            updateAddressInSequnce(oldAddressTitle,newAddressTitle,data.surveyAddressId.toString())


            isFirstCall = false
            clearAll = true
            addressLiveList.postValue(
                (context as BaseActivity).addressListDao.getAddressListBySurveyId(
                    surveyID
                )
            )
            selectedAddressLiveData.postValue(null)
        } else {
            var flag = false
            if ((context as BaseActivity).addressListDao.getAddressList().isNotEmpty()) {
                for (i in 0 until (context as BaseActivity).addressListDao.getAddressListBySurveyId(
                    surveyID
                ).size) {
                    if (data?.titleName == (context as BaseActivity).addressListDao.getAddressListBySurveyId(surveyID)[i].titleName) {
                        MessageDialog(context, "Please use another AddressType.").show()
                        return
                    } else {
                        flag = true
                    }
                }
            } else {
                flag = true
            }

            if (flag) {
                data?.surveyAddressId =
                    "ADD" + (Random.nextInt().toString())
                data?.newRecord = 1
                (context as BaseActivity).addressListDao.insert(data)
                isFirstCall = false
                clearAll = true
                addressLiveList.postValue(
                    (context as BaseActivity).addressListDao.getAddressListBySurveyId(
                        surveyID
                    )
                )
                Utility.setOfflineTag(context, "Address saved successfully")
                selectedAddressLiveData.postValue(null)
            } else {
                clearAll = true
                addressLiveList.postValue(
                    (context as BaseActivity).addressListDao.getAddressListBySurveyId(
                        surveyID
                    )
                )
                selectedAddressLiveData.postValue(data)
            }
        }

        addressBinding.spnPropertyType.setSelection(0)
    }

    private fun updateAddressInSequnce(
        oldAddressTitle: String,
        newAddressTitle: String,
        id: String
    ) {
        Log.e("AddressTititle", "$oldAddressTitle $newAddressTitle $id")
        if (oldAddressTitle.isNotEmpty() && newAddressTitle.isNotEmpty()){
            (context as BaseActivity).saveSequenceDao.updateDestinationAddressTitle(newAddressTitle,id)
            (context as BaseActivity).saveSequenceDao.updateOriginAddressTitle(newAddressTitle,id)

            (context as BaseActivity).showLegsDao.updateDestinationAddressTitle(newAddressTitle,id)
            (context as BaseActivity).showLegsDao.updateOriginAddressTitle(newAddressTitle,id)
        }
    }

    // Return Delete Address API Response
    fun deleteAddressButtonClicked() {
        if (selectedAddressLiveData == null || selectedAddressLiveData.value == null) {
            MessageDialog(context, context.getString(R.string.please_select_address_to_delete))
                .setListener(object : MessageDialog.OkButtonListener {
                    override fun onOkPressed(dialog: MessageDialog) {
                        dialog.dismiss()
                    }

                })
                .setCancelButton(false)
                .show()
        } else {
            if (addressLiveList != null && addressLiveList.value?.size!! > 1) {
                var isRevisit = selectedAddressLiveData.value!!.isRevisit
                if (isRevisit!!){
                    MessageDialog(context, context.getString(R.string.revisit_error)).show()
                }else{
                    MessageDialog(context, context.getString(R.string.are_you_sure_delete_address))
                        .setListener(object : MessageDialog.OkButtonListener {
                            override fun onOkPressed(dialog: MessageDialog) {
                                dialog.dismiss()
                                var flag = true

                                try {
                                    var addressId = ""
                                    addressId = (context as BaseActivity).saveSequenceDao.getAddressIsUsedInSequence(selectedAddressLiveData.value!!.surveyAddressId)
                                    if (addressId.isNullOrEmpty()){
                                        addressId = (context as BaseActivity).showLegsDao.getAddressIsUsedInLeg(selectedAddressLiveData.value!!.surveyAddressId)
                                    }

                                    val planningList = (context as BaseActivity).surveyPlanningListDao.getPlanningListSurveyId(AppConstants.surveyID)

                                    if (planningList.isNotEmpty()) {
                                        for (i in planningList.indices) {
                                            if (planningList[i].options!!.isNotEmpty()){
                                                planningList[i].options!![0].days?.forEach {
                                                    if (it.address == selectedAddress){
                                                        addressId = it.addressId.toString();
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    if (!addressId.isNullOrEmpty()){
                                        isLoading.postValue(false)
                                        MessageDialog(context, "Address is already in Used.").show()
                                    }else{
                                        deleteAddress(flag)
                                    }

                                }catch (e: Exception){
                                }
                            }
                        })
                        .setCancelButton(true)
                        .show()
                }
            } else {
                MessageDialog(context, "The survey required at least one address.").show()
            }
        }
    }

    // Return Delete Address API Response
    fun deleteAddress(flag: Boolean) {

        isLoading.postValue(true)
        if (flag) {
            isLoading.postValue(false)
            Utility.setOfflineTag(context, "Address Deleted Successfully")
            if (selectedAddressLiveData.value!!.surveyAddressId.contains("ADD")) {
                (context as BaseActivity).addressListDao.delete(selectedAddressLiveData.value!!.surveyAddressId)
                isFirstCall = false
                addressLiveList.postValue(
                    (context as BaseActivity).addressListDao.getAddressListBySurveyId(
                        surveyID
                    )
                )
            } else {
                val data =
                    (context as BaseActivity).addressListDao.getAddressListByAddressId(
                        selectedAddressLiveData.value!!.surveyAddressId
                    )
                data.isDelete = true
                //  (context as BaseActivity).addressListDao.delete(selectedAddressLiveData.value!!.surveyAddressId)
                (context as BaseActivity).addressListDao.update(data)
                isFirstCall = false
                addressLiveList.postValue(
                    (context as BaseActivity).addressListDao.getAddressListBySurveyId(
                        surveyID
                    )
                )

            }
        }
    }

    fun setSurveyId(surveyId: String?) {
        this.surveyID = surveyId!!
        isFirstCall = true
        // For Offline
        if ((context as BaseActivity).addressListDao.getAddressList().isNotEmpty() && (context as BaseActivity).addressListDao.getAddressListBySurveyId(
                surveyID
            ) != null && (context as BaseActivity).addressListDao.getAddressListBySurveyId(
                surveyID
            ).isNotEmpty()
        ) {
            addressLiveList.postValue(
                (context as BaseActivity).addressListDao.getAddressListBySurveyId(
                    surveyID
                )
            )
            Log.d("add", Gson().toJson((context as BaseActivity).addressListDao.getAddressList()))
        } else {
            if (Utility.isNetworkConnected(context)) {
                getAddressList(context, surveyID, 1, addressBinding = null)
            }
        }

        return


    }

    val clicksListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {

        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            isGetAddressFromType = false
            if (addressLiveList != null && addressLiveList.value != null && addressLiveList.value!!.size >= position && position != 0){
                selectedAddressLiveData.value = addressLiveList.value!![position - 1]
                selectedAddress=    addressList[position]
            }

            else{
                selectedAddressLiveData.value = null
                selectedAddress = ""
            }

        }
    }

    val clicksListenerAddressType = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {

        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            selectedAddressType.postValue(position)
            if (addressTypeLiveList != null && addressTypeLiveList.value != null && addressTypeLiveList.value!!.size >= position && position != 0) {
                selectedAddressTypeLiveData.value = addressTypeLiveList.value!![position - 1]
            } else {
                selectedAddressTypeLiveData.value = null
            }
        }
    }

    private fun getSequenceOriginLegsPermitList(context: Context) {
        if (Utility.isNetworkConnected(context)) {
            Networking(context)
                .getServices()
                .getSequenceLegPermitList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CallbackObserver<BaseModel<List<LegPermitModel>>>() {
                    override fun onSuccess(response: BaseModel<List<LegPermitModel>>) {
                        //                    isLoading.postValue(false)
                        sequenceLegPermitDestinationLiveList.postValue(response.data)
                    }

                    override fun onFailed(code: Int, message: String) {
                        //                    isLoading.postValue(false)
                        sequenceLegPermitDestinationLiveList.postValue(null)
                    }

                })
        }
    }
}