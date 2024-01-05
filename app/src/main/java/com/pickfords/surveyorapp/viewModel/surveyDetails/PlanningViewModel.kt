package com.pickfords.surveyorapp.viewModel.surveyDetails

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.MutableLiveData
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import com.pickfords.surveyorapp.R
import com.pickfords.surveyorapp.base.BaseViewModel
import com.pickfords.surveyorapp.model.address.AddressListModel
import com.pickfords.surveyorapp.model.dashboard.DashboardModel
import com.pickfords.surveyorapp.model.planning.CodeDetails
import com.pickfords.surveyorapp.model.planning.TimeModel
import com.pickfords.surveyorapp.model.planning.getPlanningAddress
import com.pickfords.surveyorapp.model.surveyDetails.ActivityListPlanningModel
import com.pickfords.surveyorapp.model.surveyDetails.CodePlanningModel
import com.pickfords.surveyorapp.model.surveyDetails.SaveSequenceModel
import com.pickfords.surveyorapp.model.surveyDetails.SurveyPlanningListModel
import com.pickfords.surveyorapp.network.BaseModel
import com.pickfords.surveyorapp.network.CallbackObserver
import com.pickfords.surveyorapp.network.Networking
import com.pickfords.surveyorapp.utils.AppConstants
import com.pickfords.surveyorapp.utils.Session
import com.pickfords.surveyorapp.utils.Utility
import com.pickfords.surveyorapp.view.BaseActivity
import com.pickfords.surveyorapp.view.dialogs.MessageDialog
import com.pickfords.surveyorapp.view.planning.CustomerListModel
import com.pickfords.surveyorapp.view.planning.OptionPlanningAdapter
import com.pickfords.surveyorapp.view.planning.ReferenceListModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class PlanningViewModel(var context: Context) : BaseViewModel() {

    private val customerList: MutableList<String?> = mutableListOf()
    private var customerLiveList: MutableLiveData<List<CustomerListModel>> = MutableLiveData()

    private val referenceList: MutableList<String?> = mutableListOf()
    private var referenceLiveList: MutableLiveData<List<ReferenceListModel>> = MutableLiveData()

    private val planningDetailList: MutableList<SurveyPlanningListModel> = mutableListOf()
    private var planningDetailLiveList: MutableLiveData<List<SurveyPlanningListModel>> =
        MutableLiveData()

    private var optionLiveList: MutableLiveData<List<SurveyPlanningListModel.Option>> =
        MutableLiveData()
    private val optionList: MutableList<SurveyPlanningListModel.Option> = mutableListOf()

    private var customerSpinnerAdapter: ArrayAdapter<String?>? = null
    private var referenceSpinnerAdapter: ArrayAdapter<String?>? = null

    fun getCustomerSpinnerAdapter(): ArrayAdapter<String?>? = customerSpinnerAdapter
    fun getReferenceSpinnerAdapter(): ArrayAdapter<String?>? = referenceSpinnerAdapter

    private var planningAdapter: OptionPlanningAdapter? = null

    fun getPlanningAdapter(): OptionPlanningAdapter? = planningAdapter

    private var selectedCustomerLiveData: MutableLiveData<CustomerListModel> = MutableLiveData()
    fun getSelectCustomerModel(): MutableLiveData<List<CustomerListModel>> {
        return customerLiveList
    }

    private var selectedReferenceLiveData: MutableLiveData<ReferenceListModel> = MutableLiveData()
    fun getSelectReferenceModel(): MutableLiveData<List<ReferenceListModel>> {
        return referenceLiveList
    }

    var customerId: String = ""
    var referenceId: String = ""

    //var customerId = 0
    //var referenceId = 0
    //var referenceId = 0
    var selectedData: DashboardModel? = null


    private var surveyId: String? = null
    var surveySequenceId: String? = "0"
    var surveySequenceName: String? = ""
    var selectedPosition: MutableLiveData<Int> = MutableLiveData()

    val sequenceList: MutableList<String?> = mutableListOf()
    internal var sequenceLiveList: MutableLiveData<List<SaveSequenceModel>> = MutableLiveData()
    private var sequenceSpinnerAdapter: ArrayAdapter<String?>? = null
    fun getSequenceSpinnerAdapter(): ArrayAdapter<String?>? = sequenceSpinnerAdapter


    // Address List
    var addressLiveList: MutableLiveData<List<AddressListModel>> = MutableLiveData()
    private val addressList: MutableList<getPlanningAddress> = mutableListOf()
    private val codeDetails: MutableList<CodeDetails> = mutableListOf()
    private val activityList: MutableList<String?> = mutableListOf()

    var clearAll = true


    // For SequenceList Dropdown
    fun getSequenceDetailsLiveList(): MutableLiveData<List<SaveSequenceModel>> =
        sequenceLiveList

    // For AddressList Dropdown
    var activityLiveList: MutableLiveData<List<ActivityListPlanningModel>> = MutableLiveData()
    private var activitySpinnerAdapter: ArrayAdapter<String?>? = null


    // For CodeList Dropdown
    var codeLiveList: MutableLiveData<List<CodePlanningModel?>> = MutableLiveData()
    private var codeSpinnerAdapter: ArrayAdapter<String?>? = null
    private val codeList: MutableList<String?> = mutableListOf()

    // For CodeList Dropdown
    var timeLiveList: MutableLiveData<List<TimeModel>> = MutableLiveData()
    private var timeSpinnerAdapter: ArrayAdapter<String?>? = null
    private val timeList: MutableList<String?> = mutableListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun init(context: Context) {
        customerSpinnerAdapter =
            ArrayAdapter<String?>(context, android.R.layout.simple_spinner_item, customerList)
        customerSpinnerAdapter?.setDropDownViewResource(R.layout.custom_spinner_item)

        referenceSpinnerAdapter =
            ArrayAdapter<String?>(context, android.R.layout.simple_spinner_item, referenceList)
        referenceSpinnerAdapter?.setDropDownViewResource(R.layout.custom_spinner_item)

        addressLiveList.observeForever {
            if (it != null) {
                addressList.clear()
                addressList.add(getPlanningAddress("Select Address", "0"))
                for (data in it) {
                    addressList.add(
                        getPlanningAddress(
                            data.titleName.toString(),
                            data.surveyAddressId
                        )
                    )
                }
                planningAdapter?.notifyDataSetChanged()
            }
        }


        if ((context as BaseActivity).customerListDao.getCustomerList() != null &&
            context.customerListDao.getCustomerList().isNotEmpty()
        ) {
            customerLiveList.postValue((context as BaseActivity).customerListDao.getCustomerList())


            if ((context as BaseActivity).referenceListDao.getReferenceList() != null && context.referenceListDao.getReferenceList()
                    .isNotEmpty()
            ) {
                referenceLiveList.postValue((context as BaseActivity).referenceListDao.getReferenceList())
            } else {
                getReferenceList(context)
            }

        } else {
            getCustomerList(context)
        }

        optionLiveList.observeForever {
            if (it != null) {
                optionList.clear()
                optionList.addAll(it)
                planningAdapter?.notifyDataSetChanged()
            }
        }

        planningAdapter =
            OptionPlanningAdapter(
                timeLiveList!!,
                codeDetails,
                context,
                addressList,
                activityLiveList, codeLiveList,
                object : OptionPlanningAdapter.OnItemClickListener {

                    override fun onItemClick(position: Int) {
                        if (!AppConstants.revisitPlanning) {
                            planningAdapter?.addOption(position)
                            planningAdapter?.notifyDataSetChanged()
                        }
                    }

                    override fun onRemoveClick(position: Int) {
                        Log.e("TAG", "onRemoveClick: Done ")
                        if (!AppConstants.revisitPlanning) {
                            planningAdapter?.removeOption(position)
                            planningAdapter?.notifyDataSetChanged()
                        }

                    }
                },
                optionList,
                this,
                false
            )

        sequenceSpinnerAdapter =
            ArrayAdapter<String?>(context, android.R.layout.simple_spinner_item, sequenceList)
        sequenceSpinnerAdapter?.setDropDownViewResource(R.layout.custom_spinner_item)


        sequenceLiveList.observeForever {
            if (it != null) {

                sequenceList.clear()
                //sequenceList.add("Select Sequence")

                for (data in it) {
                    val name =
                        if (data.surveySequence == null) data.labelToUse else data.surveySequence
                    sequenceList.add(name)
                }

                sequenceSpinnerAdapter?.notifyDataSetChanged()

            }
        }


        //  For Get Activity List
        if ((context as BaseActivity).activityListDao.getActivityList() != null && context.activityListDao.getActivityList().size > 0) {
            activityLiveList.postValue((context as BaseActivity).activityListDao.getActivityList())
        } else {
            getActivityList(context)
        }



        activitySpinnerAdapter =
            ArrayAdapter<String?>(context, android.R.layout.simple_spinner_item, activityList)
        activitySpinnerAdapter?.setDropDownViewResource(R.layout.custom_spinner_item)

        timeSpinnerAdapter =
            ArrayAdapter<String?>(context, android.R.layout.simple_spinner_item, timeList)
        timeSpinnerAdapter?.setDropDownViewResource(R.layout.custom_spinner_item)

        activityLiveList.observeForever {
            if (it != null) {
                activityList.clear()
                activityList.add("Select Activity")
                for (data in it) {
                    val name =
                        if (data.activity == null) "" else data.activity
                    activityList.add(name)
                }
                activitySpinnerAdapter?.notifyDataSetChanged()
            }
        }
        //  For Get code List
        if ((context as BaseActivity).codeListDao.getCodeList() != null && context.codeListDao.getCodeList()
                .isNotEmpty()
        ) {
            codeLiveList.postValue((context as BaseActivity).codeListDao.getCodeList())
        } else {
            getCodeList()
        }

        //  For Get time List
        if ((context as BaseActivity).timeListDao.getTimeList() != null && context.timeListDao.getTimeList()
                .isNotEmpty()
        ) {
            timeLiveList.postValue((context as BaseActivity).timeListDao.getTimeList())
        } else {
            getTimeList()
        }



        codeLiveList.observeForever {
            if (it != null) {
                codeList.clear()
                codeList.add("Select Code")
                codeDetails.clear()
                codeDetails.add(CodeDetails(0, "Select Code"))
                for (data in it) {
                    val name =
                        if (data?.discription == null) "" else data?.discription
                    codeList.add(name)
                    codeDetails.add(CodeDetails(data?.activityId!!, name!!))
                }
                codeSpinnerAdapter?.notifyDataSetChanged()
            }
        }

        timeLiveList.observeForever {
            if (it != null) {
                timeList.clear()
                timeList.add("Select Code")

                for (data in it) {
                    val name =
                        if (data?.activityDescription == null) "" else data.activityDescription
                    timeList.add(name)
                }
                timeSpinnerAdapter?.notifyDataSetChanged()
            }
        }

        addressLiveList.observeForever {
            if (it != null) {
                addressList.clear()
                addressList.add(getPlanningAddress("Select Address", "0"))
                for (data in it) {
                    addressList.add(
                        getPlanningAddress(
                            data.titleName.toString(),
                            data.surveyAddressId
                        )
                    )
                }
                planningAdapter?.notifyDataSetChanged()
            }
        }

    }


    // Call api for code list based on activity
    private fun getCodeList() {
        Networking(context)
            .getServices()
            .getCodePlanning()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CallbackObserver<BaseModel<List<CodePlanningModel>>>() {
                override fun onSuccess(response: BaseModel<List<CodePlanningModel>>) {

                    if (response.data != null) {
                        codeLiveList.postValue(response.data)
                        (context as BaseActivity).codeListDao.insertAll(response.data)
                    }
                }

                override fun onFailed(code: Int, message: String) {
                    codeLiveList.postValue(null)
                }
            })
    }

    private fun getTimeList() {
        Networking(context)
            .getServices()
            .getTime()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CallbackObserver<BaseModel<List<TimeModel>>>() {
                override fun onSuccess(response: BaseModel<List<TimeModel>>) {
                    if (response.data != null) {
                        timeLiveList.postValue(response.data)
                        (context as BaseActivity).timeListDao.insertAll(response.data)
                    }
                }

                override fun onFailed(code: Int, message: String) {
                    timeLiveList.postValue(null)
                }
            })
    }

    val clicksListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {
        }

        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long
        ) {
            if (sequenceLiveList != null && sequenceLiveList.value != null && sequenceLiveList.value!!.size >= position) {
                Log.e("Sequence", sequenceLiveList.value!![position].surveySequenceId)
                surveySequenceId = sequenceLiveList.value!![position].surveySequenceId
                surveySequenceName = sequenceLiveList.value!![position].surveySequence
                AppConstants.revisitPlanning = sequenceLiveList.value!![position].isRevisit
                selectedPosition.postValue(position)
                getPlanningDetailList(context)
                if ((context as BaseActivity).addressListDao.getAddressList() != null && (context as BaseActivity).addressListDao.getAddressList()
                        .isNotEmpty()
                ) {
                    addressLiveList.postValue(
                        (context as BaseActivity).addressListDao.getAddressListBySurveyId(
                            selectedData!!.surveyId
                        )
                    )
                }
            } else {
                surveySequenceId = "0"
                selectedPosition.postValue(0)
            }
        }
    }


    fun setSurveyId(id: String?, position: Int, isFirstCall: Boolean, isSavedCall: Boolean) {
        surveyId = id
        // For Offline
        if ((context as BaseActivity).saveSequenceDao.getSequenceList() != null && (context as BaseActivity).saveSequenceDao.getSequenceListBySurveyId(
                surveyId.toString(),false
            ).isNotEmpty()
        ) {
            sequenceLiveList.postValue(
                (context as BaseActivity).saveSequenceDao.getSequenceListBySurveyId(
                    surveyId.toString(),false
                )
            )
        } else if (Utility.isNetworkConnected(context)) {
            sequenceLiveList.postValue(null)
           // getSequenceList(surveyId, context, isFirstCall, isSavedCall, position)
        }
    }


    private fun getSequenceList(
        surveyId: String?,
        context: Context,
        isFirstCall: Boolean,
        isSavedCall: Boolean,
        position: Int) {
        if (Utility.isNetworkConnected(context)){
            isLoading.postValue(true)
            Networking(context).getServices().getSequenceList(surveyId).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CallbackObserver<BaseModel<List<SaveSequenceModel>>>() {
                    override fun onSuccess(response: BaseModel<List<SaveSequenceModel>>) {
                        isLoading.postValue(false)
                        sequenceLiveList.postValue(response.data)
                        if (isFirstCall) {
                            if (response.data?.size!! > 0) {
                                selectedPosition.postValue(0)
                            }/* else {
                            selectedPosition.postValue(0)
                        }*/

                        }
                        (context as BaseActivity).saveSequenceDao.insertAll(response.data)
                    }

                    override fun onFailed(code: Int, message: String) {
                        isLoading.postValue(false)
                        sequenceLiveList.postValue(null)
                    }
                })
        }
    }


    private fun getCustomerList(context: Context) {
        if (Utility.isNetworkConnected(context)){
            // isLoading.postValue(true)
            Networking(context)
                .getServices()
                .getCustomerList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CallbackObserver<BaseModel<List<CustomerListModel>>>() {
                    override fun onSuccess(response: BaseModel<List<CustomerListModel>>) {
                        customerLiveList.postValue(response.data)

                        if ((context as BaseActivity).referenceListDao.getReferenceList() != null && (context as BaseActivity).referenceListDao.getReferenceList().size > 0) {
                            referenceLiveList.postValue((context as BaseActivity).referenceListDao.getReferenceList())
                        } else {
                            getReferenceList(context)
                        }
                    }

                    override fun onFailed(code: Int, message: String) {
//                    isLoading.postValue(false)
                        customerLiveList.postValue(null)

                        if ((context as BaseActivity).referenceListDao.getReferenceList() != null && (context as BaseActivity).referenceListDao.getReferenceList().size > 0) {
                            referenceLiveList.postValue((context as BaseActivity).referenceListDao.getReferenceList())
                        } else {
                            getReferenceList(context)
                        }
                    }
                })
        }

    }


    private fun getActivityList(context: Context) {
        if (Utility.isNetworkConnected(context)){
            Networking(context)
                .getServices()
                .getAcitivityListPlanning()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CallbackObserver<BaseModel<List<ActivityListPlanningModel>>>() {
                    override fun onSuccess(response: BaseModel<List<ActivityListPlanningModel>>) {

                        if (response.data != null) {
                            activityLiveList.postValue(response.data)
                            (context as BaseActivity).activityListDao.insertAll(response.data)
                        }

                    }

                    override fun onFailed(code: Int, message: String) {
//
                        activityLiveList.postValue(null)
                    }
                })
        }

    }

    private fun getReferenceList(context: Context) {
//        isLoading.postValue(true)
        Networking(context)
            .getServices()
            .getReferenceList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CallbackObserver<BaseModel<List<ReferenceListModel>>>() {
                override fun onSuccess(response: BaseModel<List<ReferenceListModel>>) {
                    referenceLiveList.postValue(response.data)
                }

                override fun onFailed(code: Int, message: String) {
                    isLoading.postValue(false)
                    referenceLiveList.postValue(null)
                }
            })
    }

    fun getPlanningDetailList(context: Context, isCallCommentAPI: Boolean = false) {
        // For Offline
        val planningList =
            (context as BaseActivity).surveyPlanningListDao.getPlanningListBySurveyIdList(
                selectedData!!.surveyId, surveySequenceId.toString()
            )
        if (!isCallCommentAPI && planningList != null) {

            planningDetailLiveList.postValue(planningList)
            if (planningList.isNotEmpty() && planningList[0].options != null && planningList[0].options!!.size > 0) {
                optionLiveList.postValue(planningList[0].options)
                planningAdapter?.setSurveyPlanningListModel(planningList[0])

            } else {

                val dayList = ArrayList<SurveyPlanningListModel.Option.Day>()
                dayList.add(
                    SurveyPlanningListModel.Option.Day(
                         0,
                        0,
                        1,
                        "1",
                        "1",
                        "1",
                        "1",
                        "1",
                        "1", " ",
                        true, 0, "", 0, "", " ", " "

                    )
                )

                val data = SurveyPlanningListModel.Option(0, dayList)

                val optionLiveListTest: ArrayList<SurveyPlanningListModel.Option> =
                    ArrayList()
                optionLiveListTest.add(data)
                optionLiveList.postValue(optionLiveListTest)
                planningAdapter?.notifyDataSetChanged()
            }

        } else if (Utility.isNetworkConnected(context) && isCallCommentAPI) {
            if (isLoading.value != true)
                isLoading.postValue(true)
            Networking(context)
                .getServices()
                .getSurveyPlanningList(surveyId.toString(), surveySequenceId.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CallbackObserver<BaseModel<List<SurveyPlanningListModel>>>() {
                    override fun onSuccess(response: BaseModel<List<SurveyPlanningListModel>>) {
                        if (isLoading.value!!)
                            isLoading.postValue(false)
                        planningDetailLiveList.postValue(response.data)

                        context.surveyPlanningListDao.insertAll(response.data)

                        getAddressList(context, surveyId.toString(), 0)

                        if (response.data != null && response.data!!.size > 0 && response.data!![0].options != null && response.data!![0].options!!.size > 0) {

                            optionLiveList.postValue(response.data!![0].options)
                            if (response != null && response.data != null && response.data!!.size > 0) {
                                val data = response.data!![0]
                                planningAdapter?.setSurveyPlanningListModel(data)

                                //  (context as BaseActivity).surveyPlanningListDao.insert(data)
                            }


                        } else if (response.data != null && response.data!!.isEmpty()) {
                            val dayList = ArrayList<SurveyPlanningListModel.Option.Day>()
                            dayList.add(
                                SurveyPlanningListModel.Option.Day(
                                    0,
                                    0,
                                    1,
                                    "1",
                                    "1",
                                    "1",
                                    "1",
                                    "1",
                                    "1", " ",
                                    true, 0, "", 0, "", " ", " "

                                )
                            )

                            val data = SurveyPlanningListModel.Option(0, dayList)

                            val optionLiveListTest: ArrayList<SurveyPlanningListModel.Option> =
                                ArrayList()
                            optionLiveListTest.add(data)
                            optionLiveList.postValue(optionLiveListTest)
                            planningAdapter?.notifyDataSetChanged()
                        }
                    }

                    override fun onFailed(code: Int, message: String) {
                        if (isLoading.value!!)
                            isLoading.postValue(false)
                        //planningDetailLiveList.postValue(null)

                        val dayList = ArrayList<SurveyPlanningListModel.Option.Day>()
                        dayList.add(
                            SurveyPlanningListModel.Option.Day(
                                0,
                                0,
                                1,
                                "1",
                                "1",
                                "1",
                                "1",
                                "1",
                                "1", " ",
                                true
                            )
                        )
                        val data = SurveyPlanningListModel.Option(0, dayList)

                        val optionLiveListTest: ArrayList<SurveyPlanningListModel.Option> =
                            ArrayList()
                        optionLiveListTest.add(data)
                        optionLiveList.postValue(optionLiveListTest)
                        planningAdapter?.notifyDataSetChanged()
                    }
                })
        }
    }

    fun getAddressList(
        context: Context,
        surveyID: String,
        flag: Int = 1
    ) {
        if ((context as BaseActivity).addressListDao.getAddressListBySurveyId(surveyID)
                .isNotEmpty()
        ) {
            addressLiveList.postValue(context.addressListDao.getAddressListBySurveyId(surveyID))
        } else if (Utility.isNetworkConnected(context)) {

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

    }


    fun saveSurveyPlanningDetails(data: SurveyPlanningListModel) {

        //todo save data in local
        val existPlanning =
            (context as BaseActivity).surveyPlanningListDao.checkPlanningID(data.sequenceId!!)

        savePlanning(data, existPlanning)
    }

    // For Save Planning
    private fun savePlanning(data: SurveyPlanningListModel, existPlanning: Boolean) {
        /*if (!Utility.isNetworkConnected(context)) {

        }*/
        Utility.setOfflineTag(context, "Planning Details Saved Successfully.")
        (context as BaseActivity).surveyPlanningListDao.delete(data.sequenceId!!)

        if (data.surveyId == "0") {
            data.isChangedField = true
            data.surveyPlanningId = data.surveyPlanningId + 1
            (context as BaseActivity).surveyPlanningListDao.insert(data)
        } else if (existPlanning) {
            data.isChangedField = true
            // (context as BaseActivity).surveyPlanningListDao.delete(data.sequenceId!!)
            (context as BaseActivity).surveyPlanningListDao.updateRow(
                data.options,
                data.sequenceId.toString(),
                data.isChangedField!!
            )
            // (context as BaseActivity).surveyPlanningListDao.update(data)
        } else {
            data.isChangedField = true
            data.surveyPlanningId =
                (context as BaseActivity).surveyPlanningListDao.getPlanningList().size + 1
            (context as BaseActivity).surveyPlanningListDao.insert(data)
        }
    }

    fun deleteSurveyPlanDetail(context: Context, surveyPlanId: String, position: Int) {
        isLoading.postValue(true)
        Networking(context)
            .getServices()
            .deleteSurveyPlanDetail(surveyPlanId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CallbackObserver<BaseModel<List<SurveyPlanningListModel>>>() {
                override fun onSuccess(response: BaseModel<List<SurveyPlanningListModel>>) {
                    isLoading.postValue(false)
                    if (response.success.toString() == "true") {
                        planningAdapter?.notifyDataSetChanged()
                        MessageDialog(context, response.message).show()
                    } else if (response.success.toString() == "false" && response.message.isNotEmpty())
                        MessageDialog(context, response.message).show()
                    else {
                        FirebaseCrashlytics.getInstance().log("deleteSurveyPlanDetail -> ${Gson().toJson(response)}")
                        MessageDialog(
                            context,
                            context.getString(R.string.something_went_wrong)
                        ).show()
                    }
                }

                override fun onFailed(code: Int, message: String) {
                    FirebaseCrashlytics.getInstance().log("deleteSurveyPlanDetail -> $message")
                    isLoading.postValue(false)
                }
            })
    }

    fun deleteSurveyPlanDetailOption(
        context: Context,
        optionId: String,
    ) {
        isLoading.postValue(true)
        Networking(context)
            .getServices()
            .deleteSurveyPlanDetailOption(
                selectedData!!.surveyId,
                customerId,
                referenceId,
                optionId
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object :
                CallbackObserver<BaseModel<List<SurveyPlanningListModel>>>() {
                override fun onSuccess(response: BaseModel<List<SurveyPlanningListModel>>) {
                    isLoading.postValue(false)
                    if (response.success.toString() == "true") {
                        planningAdapter?.notifyDataSetChanged()
                        getPlanningDetailList(context)
                        MessageDialog(context, response.message).show()
                    } else if (response.success.toString() == "false" && response.message.isNotEmpty())
                        MessageDialog(context, response.message).show()
                    else {
                        FirebaseCrashlytics.getInstance().log("deleteSurveyPlanDetailOption -> ${Gson().toJson(response)}")
                        MessageDialog(
                            context,
                            context.getString(R.string.something_went_wrong)
                        ).show()
                    }

                }

                override fun onFailed(code: Int, message: String) {
                    FirebaseCrashlytics.getInstance().log("deleteSurveyPlanDetailOption -> $message")
                    isLoading.postValue(false)
                }
            })
    }


    fun setData(selectedData: DashboardModel?) {
        this.selectedData = selectedData
    }
}