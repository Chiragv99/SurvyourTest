package com.pickfords.surveyorapp.viewModel.surveyDetails

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.Observable
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.pickfords.surveyorapp.R
import com.pickfords.surveyorapp.base.BaseViewModel
import com.pickfords.surveyorapp.model.address.CountryListModel
import com.pickfords.surveyorapp.model.dashboard.DashboardModel
import com.pickfords.surveyorapp.model.enquiry.EnquiryTypeModel
import com.pickfords.surveyorapp.model.enquiry.RentalTypeModel
import com.pickfords.surveyorapp.model.enquiry.SurveySizeListModel
import com.pickfords.surveyorapp.model.enquiry.SurveyTypeModel
import com.pickfords.surveyorapp.model.surveyDetails.LanguageModel
import com.pickfords.surveyorapp.network.BaseModel
import com.pickfords.surveyorapp.network.CallbackObserver
import com.pickfords.surveyorapp.network.Networking
import com.pickfords.surveyorapp.utils.Session
import com.pickfords.surveyorapp.utils.Utility
import com.pickfords.surveyorapp.view.BaseActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*

class EnquiryViewModel(val context: Context) : BaseViewModel() {

    //  For Country
    val countryList: MutableList<String?> = mutableListOf()
    val enquiryList: MutableList<String?> = mutableListOf()

    private var countryLiveList: MutableLiveData<List<CountryListModel>> = MutableLiveData()
    private var countrySpinnerAdapter: ArrayAdapter<String?>? = null
    private var enquiryTypeSpinnerAdapter: ArrayAdapter<String?>? = null
    private var rentalTypeSpinnerAdapter: ArrayAdapter<String?>? = null
    private var surveySizeTypeSpinnerAdapter: ArrayAdapter<String?>? = null
    private var surveyTypeSpinnerAdapter: ArrayAdapter<String?>? = null

    val rentalList: MutableList<String?> = mutableListOf()
    private var rentalTypeLiveList: MutableLiveData<List<RentalTypeModel>> = MutableLiveData()
    fun getRentalLiveList(): MutableLiveData<List<RentalTypeModel>> {
        return rentalTypeLiveList
    }

    val surveySize: MutableList<String?> = mutableListOf()
    private var surveySizeLiveList: MutableLiveData<List<SurveySizeListModel>> = MutableLiveData()
    fun getSurveySizeLiveList(): MutableLiveData<List<SurveySizeListModel>> {
        return surveySizeLiveList
    }

    val surveyType: MutableList<String?> = mutableListOf()
    private var surveyTypeLiveList: MutableLiveData<List<SurveyTypeModel>> = MutableLiveData()
    fun getSurveyTypeList(): MutableLiveData<List<SurveyTypeModel>> {
        return surveyTypeLiveList
    }


    fun getCountrySpinnerAdapter(): ArrayAdapter<String?>? = countrySpinnerAdapter
    fun getEnquiryTypeSpinnerAdapter(): ArrayAdapter<String?>? = enquiryTypeSpinnerAdapter
    fun getRentalTypeSpinnerAdapter(): ArrayAdapter<String?>? = rentalTypeSpinnerAdapter
    fun getSurveySizeTypeSpinnerAdapter(): ArrayAdapter<String?>? = surveySizeTypeSpinnerAdapter

    fun getSurveyTypeSpinnerAdapter(): ArrayAdapter<String?>? = surveyTypeSpinnerAdapter


    private var enquiryTypeLiveList: MutableLiveData<List<EnquiryTypeModel>> = MutableLiveData()

    val language = ObservableField<String>().apply {
        addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                this@apply.get()?.let { Log.d("value", it) } //selected value
            }
        })
    }
    val date = ObservableField<String>().apply {
        addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                this@apply.get()?.let { Log.d("value", it) } //selected value
            }
        })
    }
    val languageList: MutableList<String?> = mutableListOf()
   private var languageLiveList: MutableLiveData<List<LanguageModel>> = MutableLiveData()

    private var languageSpinnerAdapter: ArrayAdapter<String?>? = null

    fun getLanguageSpinnerAdapter(): ArrayAdapter<String?>? = languageSpinnerAdapter

    fun getCountryLiveList(): MutableLiveData<List<CountryListModel>> {
        return countryLiveList
    }

    fun getEnquiryTypeLiveList(): MutableLiveData<List<EnquiryTypeModel>> {
        return enquiryTypeLiveList
    }
 fun getLanguageLiveList(): MutableLiveData<List<LanguageModel>> {
        return languageLiveList
    }


    @SuppressLint("NotifyDataSetChanged")
    fun init(context: Context) {

        countrySpinnerAdapter =
            ArrayAdapter<String?>(context, android.R.layout.simple_spinner_item, countryList)
        countrySpinnerAdapter?.setDropDownViewResource(R.layout.custom_spinner_item)



        enquiryTypeSpinnerAdapter =
            ArrayAdapter<String?>(context, android.R.layout.simple_spinner_item, enquiryList)
        enquiryTypeSpinnerAdapter?.setDropDownViewResource(R.layout.custom_spinner_item)

        surveySizeTypeSpinnerAdapter =
            ArrayAdapter<String?>(context, android.R.layout.simple_spinner_item, surveySize)
        surveySizeTypeSpinnerAdapter?.setDropDownViewResource(R.layout.custom_spinner_item)

        rentalTypeSpinnerAdapter =
            ArrayAdapter<String?>(context, android.R.layout.simple_spinner_item, rentalList)
        rentalTypeSpinnerAdapter?.setDropDownViewResource(R.layout.custom_spinner_item)

        surveyTypeSpinnerAdapter =
            ArrayAdapter<String?>(context, android.R.layout.simple_spinner_item, surveyType)
        surveySizeTypeSpinnerAdapter?.setDropDownViewResource(R.layout.custom_spinner_item)


        languageSpinnerAdapter =
            ArrayAdapter<String?>(context, android.R.layout.simple_spinner_item, languageList)
        languageSpinnerAdapter?.setDropDownViewResource(R.layout.custom_spinner_item)

        languageLiveList.observeForever {
            if (it != null) {
                languageList.clear()
                for (data in it) {
                    languageList.add(data.language)
                }
                languageSpinnerAdapter?.notifyDataSetChanged()
            }
        }

        if ((context as BaseActivity).languageDao.getLanguageList() != null && (context).languageDao.getLanguageList()
                .isNotEmpty()
        ) {
            languageLiveList.postValue((context).languageDao.getLanguageList())
        } else {
            getLanguageList(context)
        }


        if (context.countryListDao.getCountryList() != null && context.countryListDao.getCountryList()
                .isNotEmpty()
        ) {
            countryLiveList.postValue((context as BaseActivity).countryListDao.getCountryList())
        } else {
            getCountryList(context)
        }


        if (context.enquiryListDao.getEnquiryTypeList() != null && context.enquiryListDao.getEnquiryTypeList()
                .isNotEmpty()
        ) {
            enquiryTypeLiveList.postValue(context.enquiryListDao.getEnquiryTypeList())
        } else {
            getEnquiryList(context)
        }

        if (context.surveySizeTypeDao.getSurveySizeList() != null && context.surveySizeTypeDao.getSurveySizeList()
                .isNotEmpty()
        ) {
            surveySizeLiveList.postValue(context.surveySizeTypeDao.getSurveySizeList())
        } else {
            getSurveySize(context);
        }

        if (context.rentalDao.getRentalList() != null && context.rentalDao.getRentalList()
                .isNotEmpty()
        ) {
            rentalTypeLiveList.postValue(context.rentalDao.getRentalList())
        } else {
            getRentalList(context)
        }

        if (context.surveyTypeDao.getSurveyType() != null && context.surveyTypeDao.getSurveyType()
                .isNotEmpty()
        ) {
             surveyTypeLiveList.postValue(context.surveyTypeDao.getSurveyType())

        } else {
            getRentalList(context)
        }
    }

    private fun getEnquiryList(context: BaseActivity) {
        isLoading.postValue(true)
        Networking(context)
            .getServices()
            .getenquiryType()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CallbackObserver<BaseModel<List<EnquiryTypeModel>>>() {
                override fun onSuccess(response: BaseModel<List<EnquiryTypeModel>>) {
                    isLoading.postValue(false)
                    enquiryTypeLiveList.postValue(response.data)
                    context.enquiryListDao.insertAll(response.data)
                }

                override fun onFailed(code: Int, message: String) {
                    isLoading.postValue(false)
                    enquiryTypeLiveList.postValue(null)
                }

            })

    }

    private fun getSurveySize(context: BaseActivity) {
        isLoading.postValue(true)
        Networking(context)
            .getServices()
            .getSurveySize()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CallbackObserver<BaseModel<List<SurveySizeListModel>>>() {
                override fun onSuccess(response: BaseModel<List<SurveySizeListModel>>) {
                    isLoading.postValue(false)
                    surveySizeLiveList.postValue(response.data)
                    context.surveySizeTypeDao.insertAll(response.data)
                }

                override fun onFailed(code: Int, message: String) {
                    isLoading.postValue(false)
                    surveySizeLiveList.postValue(null)
                }


            })

    }

    private fun getRentalList(context: BaseActivity) {
        isLoading.postValue(true)
        Networking(context)
            .getServices()
            .getRentalList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CallbackObserver<BaseModel<List<RentalTypeModel>>>() {
                override fun onSuccess(response: BaseModel<List<RentalTypeModel>>) {
                    isLoading.postValue(false)
                    rentalTypeLiveList.postValue(response.data)
                    context.rentalDao.insertAll(response.data)
                }

                override fun onFailed(code: Int, message: String) {
                    isLoading.postValue(false)
                    rentalTypeLiveList.postValue(null)
                }

            })

    }

    fun onClickSave(dashboardModel: DashboardModel) {
        try {
            val session = Session(context)
            Log.e("data", "" + dashboardModel.toString())
            dashboardModel.moveDate = date.get()
       //     dashboardModel.languageId = language.get()!!.toInt()
            dashboardModel.createdBy = session.user!!.userId!!.toInt()
            dashboardModel.userId = session.user!!.userId!!.toInt()
            saveEnquiryData(dashboardModel, context)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Save Enquiry Data
    private fun saveEnquiryData(dashboardModel: DashboardModel, context: Context) {

        Utility.setOfflineTag(context, "Enquiry Saved Successfully")
        dashboardModel.isChangedField = true
        (context as BaseActivity).dashboardDao.update(dashboardModel)
    }

    // Fill Language List API
    private fun getLanguageList(context: Context) {
        isLoading.postValue(true)
        Networking(context)
            .getServices()
            .getLanguageList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CallbackObserver<BaseModel<List<LanguageModel>>>() {
                override fun onSuccess(response: BaseModel<List<LanguageModel>>) {
                    isLoading.postValue(false)
                    languageLiveList.postValue(response.data)
                }

                override fun onFailed(code: Int, message: String) {
                    isLoading.postValue(false)
                    languageLiveList.postValue(null)
                }

            })
    }

    // Fill Country List API
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
                }

                override fun onFailed(code: Int, message: String) {
                    isLoading.postValue(false)
                    countryLiveList.postValue(null)
                }
            })
    }

    // Click Listener Language
    val clicksListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {

        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        }
    }

    // Username Text Changed
    fun onUsernameTextChanged(text: CharSequence?) {
        val calendar = Calendar.getInstance()
        calendar.time = SimpleDateFormat("dd/MMM/yyyy").parse(text.toString())
        date.set(SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss").format(calendar.time))
    }
}