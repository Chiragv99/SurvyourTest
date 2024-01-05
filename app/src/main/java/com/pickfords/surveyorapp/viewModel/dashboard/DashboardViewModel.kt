package com.pickfords.surveyorapp.viewModel.dashboard

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.util.Preconditions.checkArgument
import androidx.lifecycle.MutableLiveData
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import com.pickfords.surveyorapp.DashboardActivity
import com.pickfords.surveyorapp.R
import com.pickfords.surveyorapp.base.BaseViewModel
import com.pickfords.surveyorapp.interfaces.OnItemSelected
import com.pickfords.surveyorapp.model.dashboard.DashboardModel
import com.pickfords.surveyorapp.model.dashboard.FilterSurvey
import com.pickfords.surveyorapp.model.surveyDetails.SeqeunceDetailSignature
import com.pickfords.surveyorapp.network.BaseModel
import com.pickfords.surveyorapp.network.CallbackObserver
import com.pickfords.surveyorapp.network.Networking
import com.pickfords.surveyorapp.utils.AppConstants
import com.pickfords.surveyorapp.utils.Session
import com.pickfords.surveyorapp.utils.Utility
import com.pickfords.surveyorapp.view.BaseActivity
import com.pickfords.surveyorapp.view.dashboard.DashboardAdapter
import com.pickfords.surveyorapp.view.dashboard.FilterDashboardAdapter
import com.pickfords.surveyorapp.view.dialogs.MessageDialog
import com.pickfords.surveyorapp.view.dialogs.SyncDataDialog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class DashboardViewModel : BaseViewModel() {

    private val dashboardList: MutableList<DashboardModel> = mutableListOf()
    private val filterSurveyList: MutableList<FilterSurvey> = mutableListOf()
    var dashboardLiveList: MutableLiveData<List<DashboardModel>> = MutableLiveData()
    var isSubmitted: MutableLiveData<Boolean> = MutableLiveData()
    var filterText: MutableLiveData<String> = MutableLiveData()
     var selectedDate: MutableLiveData<String> = MutableLiveData()


    var filterSurveyLiveList: MutableLiveData<List<FilterSurvey>> = MutableLiveData()

    private var dashboardAdapter: DashboardAdapter? = null
    private var filterDashboardAdapter: FilterDashboardAdapter? = null

    fun getDashboardListAdapter(): DashboardAdapter? = dashboardAdapter
    fun getFilterDashboardAdapter(): FilterDashboardAdapter? = filterDashboardAdapter


    // For Refresh Data After Submit
    var refreshData: MutableLiveData<String> = MutableLiveData()

    var setSelectedPosition: MutableLiveData<Int> = MutableLiveData()

    var currentDate: String = ""
    var previousDate: String = ""
    lateinit var mContext : Context

    @SuppressLint("NotifyDataSetChanged")
    fun init(context: Context) {
        mContext = context
        getCurrentDate()
        setSelectedPosition.postValue(0)
        refreshData.postValue(null)
       // dashboardLiveList.postValue(null)
        selectedDate.value = ""
        dashboardAdapter =
            DashboardAdapter(context,dashboardList, this, object : OnItemSelected<DashboardModel> {
                override fun onItemSelected(t: DashboardModel?, position: Int) {
                    MessageDialog(context, context.getString(R.string.are_you_sure_submit_survey_details))
                        .setListener(object : MessageDialog.OkButtonListener {
                            @SuppressLint("SuspiciousIndentation")
                            override fun onOkPressed(dialog: MessageDialog) {
                                dialog.dismiss()

                             var  list_SequenceList : List<String> =  (context as BaseActivity).sequenceDetailsDao.getAllSequenceIdList(t?.surveyId ?: "")
                             var  list_SequenceListSize : List<SeqeunceDetailSignature> =   (context as BaseActivity).sequenceDetailSignatureDao.getSignatureSizeBySurveyId(t?.surveyId ?: "")
                                 if(list_SequenceList.size >  list_SequenceListSize.size){
                                    MessageDialog(context, "Please enter signature for all Sequences.").show()
                                }else{
                                     (context as DashboardActivity).onSubmitClick(t?.surveyId ?: "")
                                }

                            }
                        })
                        .setCancelButton(false)
                        .show()
                }

            })


        filterDashboardAdapter = FilterDashboardAdapter(filterSurveyList, this, object : OnItemSelected<FilterSurvey> {
                override fun onItemSelected(t: FilterSurvey?, position: Int) {
                    Log.e("DateSelected","DateSelected")
                    selectedDate.value = t?.surveyDate
                    getSurveyDetailList(context, false)
                    dashboardAdapter?.notifyDataSetChanged()
                    setSelectedPosition.postValue(position)

                }

            })
        dashboardLiveList.observeForever {
            if (it != null) {
                dashboardList.clear()
                dashboardList.addAll(it)
                dashboardAdapter?.notifyDataSetChanged()
            }
        }

        if ((context as BaseActivity).filterSurveyDao.getFilterSurveyList() != null && context.filterSurveyDao.getFilterSurveyList().isNotEmpty()) {
            Log.e("Data","Not Emptily")
            filterSurveyLiveList.postValue(context.filterSurveyDao.getFilterSurveyList())
        }
        else{
            Log.e("Data","Emplity")
            callFilterSurveyDetailApi(context, true)
        }


        filterSurveyLiveList.observeForever { it ->
           // TODO - Update filter offline code - Jaimit 2
           // filterSurveyList.add(0, FilterSurvey("", "All", "", false))

            if (it != null && it != null) {
                filterSurveyList.clear()
                var format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                try {
                    it?.forEachIndexed { index, filterSurvey ->
                        val date: Date? = format.parse(filterSurvey.surveyDate)
                        format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val eFormat = SimpleDateFormat("dd", Locale.getDefault())
                        val monFormat = SimpleDateFormat("MMM", Locale.getDefault())
                        val dateUpdate = date?.let { it1 -> format.format(it1) }
                        val currentDate = format.format(Date())
                        val day = date?.let { it1 -> eFormat.format(it1) }
                        if (dateUpdate == currentDate) {
                            filterSurvey.showDate = "Today"
                            filterSurvey.isSelected = true
                            selectedDate.value = filterSurvey.surveyDate
                        } else {
                            if (index == 0) {
                              //  filterSurvey.isSelected = true
                                selectedDate.value = filterSurvey.surveyDate
                            }
                            filterSurvey.showDate =
                                day + getDayOfMonthSuffix(day?.toInt()) + " " + date?.let { it1 ->
                                    monFormat.format(
                                        it1
                                    ).toString()
                                }
                        }
                    }

                } catch (e: ParseException) {
                    e.printStackTrace()
                }
                filterSurveyList.addAll(it!!)
                addAllAndNotSubmitted(it,context)
            }
           getSurveyDetailList(context, false)
        }
    }

      fun setDefaultVale(){
        getSurveyDetailList(mContext, false)
    }
    // For Get Current Date
    private fun getCurrentDate(){
        val formatter = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DateTimeFormatter.ofPattern("yyyy-MM-dd")
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        currentDate = LocalDateTime.now().format(formatter)
        val dateTime = LocalDateTime.now()
        val previousDates = dateTime.minusDays(2)
        previousDate = previousDates.format(formatter)
        Log.d("Date:", "previous date ${previousDate.format(formatter)}")
    }
    // Add All and Not Submitted on Dashboard Top
    private fun addAllAndNotSubmitted(it: List<FilterSurvey>, context: BaseActivity) {
        var filterSurvey = FilterSurvey()
        filterSurvey.surveyDate = ""
        filterSurvey.showDate = "All"
        filterSurvey.surveyDay = ""
        filterSurvey.isSelected = it == null
        if (it.isNullOrEmpty()){
            filterSurvey.isSelected = true
        }

        filterSurveyList.add(0, filterSurvey)
        filterDashboardAdapter?.notifyDataSetChanged()


        var filterSurveyNotSubmitted = FilterSurvey()
        filterSurveyNotSubmitted.surveyDate = AppConstants.notSubmitted
        filterSurveyNotSubmitted.showDate = AppConstants.notSubmitted
        filterSurveyNotSubmitted.surveyDay = ""
        filterSurveyList.add(1, filterSurveyNotSubmitted)
        filterDashboardAdapter?.notifyDataSetChanged()
        filterSurveyNotSubmitted.isSelected = false


        // For filter Is date is selected or Not
       var  filterSurveyListSelected: Boolean = filterSurveyList.any {
           it.isSelected == true
       }

        if (!filterSurveyListSelected){
            filterSurveyList[0].isSelected = true
            filterDashboardAdapter?.notifyDataSetChanged()
            selectedDate.value = filterSurveyList[0]?.surveyDate
            getSurveyDetailList(context, false)
            dashboardAdapter?.notifyDataSetChanged()
        }

    }

    @SuppressLint("RestrictedApi")
    private fun getDayOfMonthSuffix(n: Int?): String {
        checkArgument(n in 1..31, "illegal day of month: $n")
        return if (n in 11..13) {
            "th"
        } else when (n?.rem(10)) {
            1 -> "st"
            2 -> "nd"
            3 -> "rd"
            else -> "th"
        }
    }
    //  For Save Survey Details
    fun saveSurveyDetailList(surveyId: String, context: Context) {
        if (!Utility.isNetworkConnected(context)) {
            Utility.setOfflineTag(context, "Survey Details Saved Successfully")
            Utility.submitSurvey(context, surveyId)
            val model = (context as BaseActivity).dashboardDao.getDashboard(surveyId)
            model?.isCompletedSurvey = true
            context.dashboardDao.update(model)
            dashboardLiveList.postValue(context.dashboardDao.getDashboardList())
            (context as DashboardActivity).onRefreshDashboard(context)
            return
        } else {
            (context as BaseActivity).showProgressbar()
            Networking(context)
                .getServices()
                .saveSequenceDetails(surveyId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CallbackObserver<BaseModel<Boolean>>() {
                    override fun onSuccess(response: BaseModel<Boolean>) {
                        (context as BaseActivity).hideProgressbar()
                        if (response.success.toString() == "true") {
                            MessageDialog(context, response.message).setListener(object :
                                MessageDialog.OkButtonListener {
                                override fun onOkPressed(dialog: MessageDialog) {
                                    dialog.dismiss()
                                    (context as BaseActivity).dashboardDao.deleteBySurveyId(surveyId)
                                    if (dashboardList != null && dashboardList.size > 0 && dashboardList.size == 1) { // TODO - check survey list size to call filter list api call - Pranav Shah
                                        selectedDate.value = ""

                                        if ((context as BaseActivity).filterSurveyDao.getFilterSurveyList() != null && context.filterSurveyDao.getFilterSurveyList().isNotEmpty()) {
                                            filterSurveyLiveList.postValue(context.filterSurveyDao.getFilterSurveyList())

                                        } else {
                                            callFilterSurveyDetailApi(context, true)
                                        }

                                    } else {
                                        getSurveyDetailList(context, true)

                                    }
                                }

                            }).show()
                        } else if (response.success.toString() == "false" && response.message.isNotEmpty())
                            MessageDialog(context, response.message).show()
                        else {
                            FirebaseCrashlytics.getInstance().log("saveSequenceDetails -> ${Gson().toJson(response)}")
                            MessageDialog(
                                context,
                                context.getString(R.string.something_went_wrong)
                            ).show()
                        }
                    }

                    override fun onFailed(code: Int, message: String) {
                        FirebaseCrashlytics.getInstance().log("saveSequenceDetails -> $message")
                        (context as BaseActivity).hideProgressbar()
                        MessageDialog(
                            context,
                            context.getString(R.string.something_went_wrong)
                        ).show()
                    }
                })
        }
    }


    fun getSurveyDetailList(context: Context, callfilter: Boolean) {

        var submited: String = "0";
        submited = if (isSubmitted.value == true){ "1"
        }else{ "0"
        }

        filterData(context,submited)

        if (callfilter){
            if (context is DashboardActivity){
                (context as DashboardActivity).navigateToDashboard()
            }
        }

    }

    private fun filterData(context: Context, submited: String) {
        var userId = (context as BaseActivity).session.user?.userId.toString();

        if (selectedDate.value != null && !selectedDate.value.equals("")){

            if(filterText.value != null && !filterText.value.equals("")){
                if (filterText.value.equals(context.getString(R.string.enquiryNo))){
                    filterByEnquirySmallToLargest(context,submited)
                }

                if (filterText.value.equals(context.getString(R.string.enquiryNodesc))){
                    filterByEnquiryLargestToSmall(context,submited)
                }
                if (filterText.value.equals(context.getString(R.string.firstNameLabel))){
                    filterByEnquiryDataByFirstName(context,submited)
                }

                if (filterText.value.equals(context.getString(R.string.movemanagerLabel))){
                    filterByEnquiryDataByMoveManager(context,submited)
                }

                if (filterText.value.equals(context.getString(R.string.surveyDateOldestlabel))){
                    filterByEnquiryDataAllSurveyDateOldToNewest(context,submited)
                }

                if (filterText.value.equals(context.getString(R.string.surveyDateNewestlabel))){
                    filterByEnquiryDataAllSurveyDatNewestToOld(context,submited)
                }
            }
            else{
                Log.e("Dashboard",AppConstants.notSubmitted + " " +  submited)
                if (selectedDate.value.toString() == AppConstants.notSubmitted && submited ==  AppConstants.isSubmittedNot){
                    if ((context as BaseActivity).dashboardDao.getNotSubmittedData(AppConstants.isSubmittedNot,userId) != null && (context as BaseActivity).dashboardDao.getNotSubmittedData(AppConstants.isSubmittedNot,userId).isNotEmpty()) {
                        dashboardLiveList.postValue(context.dashboardDao.getNotSubmittedData(AppConstants.isSubmittedNot,userId))
                    }
                    else{
                        dashboardLiveList.postValue(null)
                    }
                }
                else{
                    if (submited == AppConstants.isSubmitted && selectedDate.value.toString().isNullOrEmpty()){
                        if ((context as BaseActivity).dashboardDao.getsubmittedData(submited) != null && (context as BaseActivity).dashboardDao.getsubmittedData(submited).isNotEmpty()) {
                            dashboardLiveList.postValue(context.dashboardDao.getsubmittedData(submited))
                        }
                        else{
                            dashboardLiveList.postValue(null)
                        }
                    }

                     if ((context as BaseActivity).dashboardDao.getsubmittedData(submited,selectedDate.value.toString(),currentDate,userId) != null && (context as BaseActivity).dashboardDao.getsubmittedData(submited,selectedDate.value.toString(),currentDate,userId).isNotEmpty()) {
                        dashboardLiveList.postValue(context.dashboardDao.getsubmittedData(submited,selectedDate.value.toString(),currentDate,userId))
                    }
                    else{
                        dashboardLiveList.postValue(null)
                    }
                }

            }
        }
        else{

              if(filterText.value != null && !filterText.value.equals("")){

                if (filterText.value.equals(context.getString(R.string.enquiryNo))){
                    filterByEnquirySmallToLargest(context,submited)
                }

                if (filterText.value.equals(context.getString(R.string.enquiryNodesc))){
                    filterByEnquiryLargestToSmall(context,submited)
                }
                if (filterText.value.equals(context.getString(R.string.firstNameLabel))){
                    filterByEnquiryDataByFirstName(context,submited)
                }

                if (filterText.value.equals(context.getString(R.string.movemanagerLabel))){
                    filterByEnquiryDataByMoveManager(context,submited)
                }

                if (filterText.value.equals(context.getString(R.string.surveyDateOldestlabel))){
                    filterByEnquiryDataAllSurveyDateOldToNewest(context,submited)
                }

                if (filterText.value.equals(context.getString(R.string.surveyDateNewestlabel))){
                    filterByEnquiryDataAllSurveyDatNewestToOld(context,submited)
                }
            }

            else{
                  if (submited == AppConstants.isSubmitted){
                      if ((context as BaseActivity).dashboardDao.getsubmittedData(submited) != null && (context as BaseActivity).dashboardDao.getsubmittedData(submited).isNotEmpty()) {
                          dashboardLiveList.postValue(context.dashboardDao.getsubmittedData(submited))
                      }
                      else{
                          dashboardLiveList.postValue(null)
                      }
                  }

               else if ((context as BaseActivity).dashboardDao.getsubmittedDataAll(submited,currentDate) != null && (context as BaseActivity).dashboardDao.getsubmittedDataAll(submited,currentDate).isNotEmpty()) {
                    dashboardLiveList.postValue(context.dashboardDao.getsubmittedDataAll(submited,currentDate))
                }
                else{
                    dashboardLiveList.postValue(null)
                }

            }
        }
    }

  fun  filterByEnquirySmallToLargest(context: Context, submited: String) {
      if (selectedDate.value.equals("")){
          if ((context as BaseActivity).dashboardDao.getsubmittedDataAllEnquirySmallToLargest(submited,currentDate) != null && (context as BaseActivity).dashboardDao.getsubmittedDataAllEnquirySmallToLargest(submited).isNotEmpty()) {
              dashboardLiveList.postValue(context.dashboardDao.getsubmittedDataAllEnquirySmallToLargest(submited))
          }
          else{
              dashboardLiveList.postValue(null)
          }
      }
      if(!selectedDate.value.equals("")){
          if ((context as BaseActivity).dashboardDao.getsubmittedDataAllEnquirySmallToLargest(submited,selectedDate.value!!) != null && (context as BaseActivity).dashboardDao.getsubmittedDataAllEnquirySmallToLargest(submited,selectedDate.value!!).isNotEmpty()) {
              dashboardLiveList.postValue(context.dashboardDao.getsubmittedDataAllEnquirySmallToLargest(submited,selectedDate.value!!))
          }
          else{
              dashboardLiveList.postValue(null)
          }
      }
    }

    fun  filterByEnquiryLargestToSmall(context: Context, submited: String) {
        if (filterText.value.equals(context.getString(R.string.enquiryNodesc)) && selectedDate.value.equals("")){
            if ((context as BaseActivity).dashboardDao.getsubmittedDataAllEnquiryLargestToSmall(submited) != null && (context as BaseActivity).dashboardDao.getsubmittedDataAllEnquirySmallToLargest(submited).isNotEmpty()) {
                dashboardLiveList.postValue(context.dashboardDao.getsubmittedDataAllEnquiryLargestToSmall(submited))
            }
            else{
                dashboardLiveList.postValue(null)
            }
        }
        if(filterText.value.equals(context.getString(R.string.enquiryNodesc)) && !selectedDate.value.equals("")){
            if ((context as BaseActivity).dashboardDao.getsubmittedDataAllEnquiryLargestToSmall(submited,selectedDate.value!!) != null && (context as BaseActivity).dashboardDao.getsubmittedDataAllEnquirySmallToLargest(submited,selectedDate.value!!).isNotEmpty()) {
                dashboardLiveList.postValue(context.dashboardDao.getsubmittedDataAllEnquiryLargestToSmall(submited,selectedDate.value!!))
            }
            else{
                dashboardLiveList.postValue(null)
            }
        }
    }

    fun  filterByEnquiryDataByFirstName(context: Context, submited: String) {
        if (selectedDate.value.equals("")){
            if ((context as BaseActivity).dashboardDao.getsubmittedDataByFirstName(submited) != null && (context as BaseActivity).dashboardDao.getsubmittedDataByFirstName(submited).isNotEmpty()) {
                dashboardLiveList.postValue(context.dashboardDao.getsubmittedDataByFirstName(submited))
            }
            else{
                dashboardLiveList.postValue(null)
            }
        }
        if(!selectedDate.value.equals("")){
            if ((context as BaseActivity).dashboardDao.getsubmittedDataByFirstName(submited,selectedDate.value!!) != null && (context as BaseActivity).dashboardDao.getsubmittedDataByFirstName(submited,selectedDate.value!!).isNotEmpty()) {
               dashboardLiveList.postValue(context.dashboardDao.getsubmittedDataByFirstName(submited,selectedDate.value!!))
            }
            else{
                dashboardLiveList.postValue(null)
            }
        }
    }


    fun  filterByEnquiryDataByMoveManager(context: Context, submited: String) {
        if (selectedDate.value.equals("")){
            if ((context as BaseActivity).dashboardDao.getsubmittedDataByMoveManager(submited) != null && (context as BaseActivity).dashboardDao.getsubmittedDataByMoveManager(submited).isNotEmpty()) {
                dashboardLiveList.postValue(context.dashboardDao.getsubmittedDataByMoveManager(submited))
            }
            else{
                dashboardLiveList.postValue(null)
            }
        }
        if(!selectedDate.value.equals("")){
            if ((context as BaseActivity).dashboardDao.getsubmittedDataByMoveManager(submited,selectedDate.value!!) != null && (context as BaseActivity).dashboardDao.getsubmittedDataByMoveManager(submited,selectedDate.value!!).isNotEmpty()) {
                dashboardLiveList.postValue(context.dashboardDao.getsubmittedDataByMoveManager(submited,selectedDate.value!!))
            }
            else{
                dashboardLiveList.postValue(null)
            }
        }
    }


    fun  filterByEnquiryDataAllSurveyDatNewestToOld(context: Context, submited: String) {
        if (selectedDate.value.equals("")){
            if ((context as BaseActivity).dashboardDao.getsubmittedDataAllSurveyDatNewestToOld(submited) != null && (context as BaseActivity).dashboardDao.getsubmittedDataAllSurveyDatNewestToOld(submited).isNotEmpty()) {
                dashboardLiveList.postValue(context.dashboardDao.getsubmittedDataAllSurveyDatNewestToOld(submited))
            }
            else{
                dashboardLiveList.postValue(null)
            }
        }
        if(!selectedDate.value.equals("")){
            if ((context as BaseActivity).dashboardDao.getsubmittedDataAllSurveyDatNewestToOld(submited,selectedDate.value!!) != null && (context as BaseActivity).dashboardDao.getsubmittedDataAllSurveyDatNewestToOld(submited,selectedDate.value!!).isNotEmpty()) {
                dashboardLiveList.postValue(context.dashboardDao.getsubmittedDataAllSurveyDatNewestToOld(submited,selectedDate.value!!))
            }
            else{
                dashboardLiveList.postValue(null)
            }
        }
    }


    fun  filterByEnquiryDataAllSurveyDateOldToNewest(context: Context, submited: String) {
        if (selectedDate.value.equals("")){
            if ((context as BaseActivity).dashboardDao.getsubmittedDataAllSurveyDateOldToNewest(submited) != null && (context as BaseActivity).dashboardDao.getsubmittedDataAllSurveyDateOldToNewest(submited).isNotEmpty()) {
                dashboardLiveList.postValue(context.dashboardDao.getsubmittedDataAllSurveyDatNewestToOld(submited))
            }
            else{
                dashboardLiveList.postValue(null)
            }
        }
        if(!selectedDate.value.equals("")){
            if ((context as BaseActivity).dashboardDao.getsubmittedDataAllSurveyDateOldToNewest(submited,selectedDate.value!!) != null && (context as BaseActivity).dashboardDao.getsubmittedDataAllSurveyDateOldToNewest(submited,selectedDate.value!!).isNotEmpty()) {
                dashboardLiveList.postValue(context.dashboardDao.getsubmittedDataAllSurveyDateOldToNewest(submited,selectedDate.value!!))
            }
            else{
                dashboardLiveList.postValue(null)
            }
        }
    }

    //  Delete survey from Dashboard
    fun deleteSurvey(context: Context, surveyId: String, model: DashboardModel) {
        if (!Utility.isNetworkConnected(context)) {
            SyncDataDialog(
                context,
                "You Can't Delete Survey Offline"
            ).setTitleData("Warning")
                .setListener(object : SyncDataDialog.OkButtonListener {
                    override fun onOkPressed(dialog: SyncDataDialog) {
                        dialog.dismiss()
                    }
                })
                .setCancelButton(false)
                .show()
        } else if (!model.isSubmitted) {
            SyncDataDialog(
                context,
                "This survey is still not submitted are you sure to delete this?"
            ).setTitleData("Warning")
                .setListener(object : SyncDataDialog.OkButtonListener {
                    override fun onOkPressed(dialog: SyncDataDialog) {
                        callDeleteApi(context, surveyId)
                        dialog.dismiss()
                    }
                })
                .setCancelButton(true)
                .show()

        } else if (model.isSubmitted == true) {
            SyncDataDialog(
                context,
                "Are you sure to delete this survey?"
            ).setTitleData("Warning")
                .setListener(object : SyncDataDialog.OkButtonListener {
                    override fun onOkPressed(dialog: SyncDataDialog) {
                        callDeleteApi(context, surveyId)
                        dialog.dismiss()
                    }
                })
                .setCancelButton(true)
                .show()
        }
    }

    // For Copy Same Survey
    fun copySurvey(context: Context, surveyId: String) {
        if (!Utility.isNetworkConnected(context)) {
            SyncDataDialog(
                context,
                "You Can't Copy Survey Offline"
            ).setTitleData("Warning")
                .setListener(object : SyncDataDialog.OkButtonListener {
                    override fun onOkPressed(dialog: SyncDataDialog) {
                        dialog.dismiss()
                    }
                })
                .setCancelButton(false)
                .show()
        } else {
            SyncDataDialog(
                context,
                "Are you sure to copy this survey?"
            ).setTitleData("Warning")
                .setListener(object : SyncDataDialog.OkButtonListener {
                    override fun onOkPressed(dialog: SyncDataDialog) {
                        callCopyApi(context, surveyId)
                        dialog.dismiss()
                    }
                })
                .setCancelButton(true)
                .show()
        }
    }

//    Return call copy survey API
    fun callCopyApi(context: Context, surveyId: String) {
        val session = Session(context)
        isLoading.postValue(true)
        Networking(context)
            .getServices()
            .copySurvey(surveyId, session.user!!.userId!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CallbackObserver<BaseModel<Boolean>>() {
                override fun onSuccess(response: BaseModel<Boolean>) {
                    isLoading.postValue(false)
                    if (response.success) {
                        getSurveyDetailList(context, false)
                    }
                }

                override fun onFailed(code: Int, message: String) {
                    isLoading.postValue(false)
                }
            })
    }
    //    Return delete  survey API
    fun callDeleteApi(context: Context, surveyId: String) {
        val session = Session(context)
        isLoading.postValue(true)
        Networking(context)
            .getServices()
            .deleteSurvey(surveyId, session.user!!.userId!!, session.user!!.userId!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CallbackObserver<BaseModel<List<DashboardModel>>>() {
                override fun onSuccess(response: BaseModel<List<DashboardModel>>) {
                    isLoading.postValue(false)
                    if (response.success) {
                        (context as BaseActivity).dashboardDao.deleteBySurveyId(surveyId)
                    }
                    getSurveyDetailList(context, false)
                }

                override fun onFailed(code: Int, message: String) {
                    isLoading.postValue(false)
                }
            })
    }
    //    Return filter Dialogue API
    fun callFilterSurveyDetailApi(context: Context, isFromSubmit: Boolean) {
        if (Utility.isNetworkConnected(context)){
            val session = Session(context)
            isLoading.postValue(true)
            Networking(context)
                .getServices()
                .getFilterSurveyDetail(session.user!!.userId!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CallbackObserver<BaseModel<List<FilterSurvey>>>() {
                    override fun onSuccess(response: BaseModel<List<FilterSurvey>>) {
                        isLoading.postValue(false)

                        filterSurveyLiveList.postValue(response.data)
                        if (isFromSubmit) {
                            getSurveyDetailList(context, false)
                        }

                        (context as BaseActivity).filterSurveyDao.insertAll(response.data)
                    }

                    override fun onFailed(code: Int, message: String) {
                        isLoading.postValue(false)
                        filterSurveyLiveList.postValue(null)
                    }
                })
        }else{
            if ((context as BaseActivity).filterSurveyDao.getFilterSurveyList() == null ||  context.filterSurveyDao.getFilterSurveyList().isEmpty()) {
                var filterSurvey = FilterSurvey()
                filterSurveyList.add(filterSurvey)
                filterSurveyLiveList.postValue(filterSurveyList)
            }

        }
    }
}
