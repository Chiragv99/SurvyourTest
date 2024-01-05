package com.pickfords.surveyorapp.view.surveyDetails

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import com.pickfords.surveyorapp.R
import com.pickfords.surveyorapp.databinding.FragmentEnquiryBinding
import com.pickfords.surveyorapp.extentions.isEmpty
import com.pickfords.surveyorapp.interfaces.FragmentLifecycleInterface
import com.pickfords.surveyorapp.model.dashboard.DashboardModel
import com.pickfords.surveyorapp.model.enquiry.EnquiryTypeModel
import com.pickfords.surveyorapp.model.enquiry.RentalTypeModel
import com.pickfords.surveyorapp.model.enquiry.SurveySizeListModel
import com.pickfords.surveyorapp.model.enquiry.SurveyTypeModel
import com.pickfords.surveyorapp.utils.DateAndTimeUtils
import com.pickfords.surveyorapp.utils.EditTextErrorResolver
import com.pickfords.surveyorapp.utils.Session
import com.pickfords.surveyorapp.utils.Session.Companion.DATA
import com.pickfords.surveyorapp.view.BaseFragment
import com.pickfords.surveyorapp.view.dialogs.MessageDialog
import com.pickfords.surveyorapp.viewModel.surveyDetails.EnquiryViewModel
import kotlinx.android.synthetic.main.fragment_enquiry.edtInputFirstName
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date


class FragmentEnquiry : BaseFragment(), FragmentLifecycleInterface {
    lateinit var enquiryBinding: FragmentEnquiryBinding
    private val viewModel by lazy { EnquiryViewModel(requireActivity()) }
    private var selectedData: DashboardModel? = null
    private var flagCompany = false

    companion object {
        fun newInstance(selectedData: DashboardModel?): FragmentEnquiry {
            val bundle = Bundle()
            bundle.putSerializable(DATA, selectedData)
            val fragmentEnquiry = FragmentEnquiry()
            fragmentEnquiry.arguments = bundle
            return fragmentEnquiry
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        enquiryBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_enquiry,
            container,
            false
        )
        return enquiryBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setSpinnerTitle()
        setObserver(savedInstanceState)
        setAction()
        setSurveyDateAndTime()
    }

    private fun setSurveyDateAndTime() {
      var dateTime:String =   convertDateTimeReturn(selectedData!!.surveyDate) + " " + updateTimeFromDateDashboard(selectedData!!.surveyTime)
      enquiryBinding.edtInputSurveyDate.setText(dateTime);
    }

    // For All list Observer
    private fun setObserver(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) onResumeFragment(null)
        enquiryBinding.edtInputMoveDate.doOnTextChanged { text, _, _, _ ->
            viewModel.onUsernameTextChanged(
                text
            )
        }

        viewModel.getCountryLiveList().observe(requireActivity()) {
            if (it != null) {
                viewModel.countryList.clear()
                viewModel.countryList.add("Select Country")
                for (data in it) {
                    viewModel.countryList.add(data.countryName)
                }
                viewModel.getCountrySpinnerAdapter()?.notifyDataSetChanged()
            }

            selectCountry(selectedData!!.originCountry.toString())
        }
        viewModel.getLanguageLiveList().observe(requireActivity()) {
            if (it != null) {
                viewModel.languageList.clear()
                for (data in it) {
                    viewModel.languageList.add(data.language)
                }
                viewModel.getLanguageSpinnerAdapter()?.notifyDataSetChanged()
            }

            selectLanguage(selectedData!!.languageId.toString())
        }


        viewModel.getEnquiryTypeLiveList().observe(requireActivity()) { list ->

            if (list != null) {
                viewModel.enquiryList.clear()
                viewModel.enquiryList.add("Select Enquiry Type")
                for (data in list) {
                    viewModel.enquiryList.add(data.enquiryTypeName)
                }
                viewModel.getEnquiryTypeSpinnerAdapter()?.notifyDataSetChanged()
            }

            if (selectedData?.enquiryType == "0" || selectedData?.enquiryType == "Private") {
                selectedData?.enquiryType = "Private"
            } else {
                selectedData?.enquiryType = "Corporate"
            }
            val indexEnquirySize = getSelectedEnquiryType(
                selectedData?.enquiryType,
                list as ArrayList<EnquiryTypeModel>
            )
            if (indexEnquirySize != -1) {
                enquiryBinding.spnEnquiryType.setSelection(indexEnquirySize + 1)
            }
        }

        viewModel.getRentalLiveList().observe(requireActivity()) {
            if (it != null) {
                viewModel.rentalList.clear()

                for (data in it) {
                    if (data.rentalCode == 0) {
                        data.rentalProperty = "Select Rental Property"
                    }
                    viewModel.rentalList.add(data.rentalProperty)
                }
                viewModel.getRentalTypeSpinnerAdapter()?.notifyDataSetChanged()
            }

            val indexRentProperty = getSelectedPropertyType(selectedData!!.rentalProperty.toString(), viewModel.getRentalLiveList())

            if (indexRentProperty != null && indexRentProperty != -1) {
                enquiryBinding.spnRental.setSelection(indexRentProperty)// todo need to check with Chirag - Jaimit
            }
        }

        viewModel.getSurveyTypeList().observe(requireActivity()) {
            if (it != null) {
                viewModel.surveyType.clear()
                for (data in it) {
                    if (data.surveyTypeCode == 0) {
                        data.surveyType = "Select Survey Type"
                    }
                    viewModel.surveyType.add(data.surveyType)
                }
                viewModel.getSurveyTypeSpinnerAdapter()?.notifyDataSetChanged()
            }
            val indexSurveyTypeProperty = getSelectedSurveyType(selectedData?.surveyType, viewModel.getSurveyTypeList())

            if (indexSurveyTypeProperty != null && indexSurveyTypeProperty != -1) {
                enquiryBinding.spnType.setSelection(indexSurveyTypeProperty)// todo need to check with Chirag - Jaimit
            }
        }

           viewModel.getSurveySizeLiveList().observe(requireActivity()) {
            if (it != null) {
                viewModel.surveySize.clear()
                viewModel.surveySize.add("Select Size")
                for (data in it) {
                    viewModel.surveySize.add(data.sizeDescription)
                }
                viewModel.getSurveySizeTypeSpinnerAdapter()?.notifyDataSetChanged()
            }

            val indexEnquirySize = getSelectedSurveySizeType(
                selectedData!!.surveySize.toString(),
                viewModel.getSurveySizeLiveList()
            )
            if (indexEnquirySize != null && indexEnquirySize != -1) {
                enquiryBinding.spnSize.setSelection(indexEnquirySize + 1)
            }else{
                enquiryBinding.spnSize.setSelection(0)
            }

        }

       // For Disable
       enquiryBinding.spnLanguage.setOnTouchListener(OnTouchListener { v, event -> true })
       enquiryBinding.edtInputBooker.setOnTouchListener(OnTouchListener { v, event -> true })

    }

    fun convertDateTimeReturn(value: String?) : String {
        if (value != null) {
            val cal = Calendar.getInstance()
            try {
                val input = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss")
                cal.time = input.parse(value)
                val output = SimpleDateFormat("dd/MM/yyyy")
                return output.format(cal.timeInMillis)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }
        return ""
    }


    fun updateTimeFromDateDashboard(value: String?) : String{
        val dt: Date
        var time : String = "";
        if (value != null)
            try {
                val input = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                dt = input.parse(value)
                val output = SimpleDateFormat("HH:mm")
                time = output.format(dt)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        return time;
    }



    private fun selectLanguage(langId: String) {
        val data = viewModel.getLanguageLiveList()
        if (data.value != null) {
            var selectedposition = data.value!!.indexOfFirst { langId == it.languageId }
            selectedposition = selectedposition
            enquiryBinding.spnLanguage.setSelection(selectedposition)
        }
    }



    // Set All click here
    private fun setAction() {

        enquiryBinding.spnEnquiryType.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (viewModel.getEnquiryTypeLiveList() != null && viewModel.getEnquiryTypeLiveList().value?.size!! > 0) {
                        var pos = 0
                        if (position != 0) {
                            pos = position - 1
                        } else {
                            pos = position
                        }
                        if (viewModel.getEnquiryTypeLiveList().value!![pos].enquiryTypeName != "Private") {
                            flagCompany = true
                        } else {
                            flagCompany = false
                            enquiryBinding.edtInputCompanyName.error = null
                            enquiryBinding.inputCompanyName.isErrorEnabled = false
                        }
                        enquiryBinding.tvEnquiryTypeSpinnerTitle.error = null
                    }
                }
            }
        enquiryBinding.spnCountry.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    enquiryBinding.tvCountryTitle.error = null
                }
            }
        enquiryBinding.spnSize.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    enquiryBinding.tvTitle.error = null
                }
            }

        enquiryBinding.btnSave.setOnClickListener {

            if (TextUtils.isEmpty(enquiryBinding.edtInputMoveDate.text.toString())) {
                enquiryBinding.edtInputMoveDate.error = "Please enter Move Date"
                enquiryBinding.edtInputMoveDate.requestFocus()
            } else if (TextUtils.isEmpty(enquiryBinding.edtInputFirstName.text.toString())) {
                enquiryBinding.edtInputFirstName.error = "Please enter First Name"
                enquiryBinding.edtInputFirstName.requestFocus()
            } else if (TextUtils.isEmpty(enquiryBinding.edtInputSurname.text.toString())) {
                enquiryBinding.edtInputSurname.error = "Please enter Surname"
                enquiryBinding.edtInputSurname.requestFocus()
            } else if (enquiryBinding.spnEnquiryType.selectedItemPosition == 0) {
                enquiryBinding.tvEnquiryTypeSpinnerTitle.error = "Please select Enquiry Type"
                if (enquiryBinding.spnEnquiryType.selectedItemPosition > 0) {
                }
            } else if (!TextUtils.isEmpty((enquiryBinding.edtInputPhone.text.toString())) && !isValidMobile(
                    enquiryBinding.edtInputPhone.text.toString()
                )
            ) {
                enquiryBinding.edtInputPhone.error = "Please enter valid Phone"
                enquiryBinding.edtInputPhone.requestFocus()
            } else if (!TextUtils.isEmpty((enquiryBinding.edtInputPhoneHome.text.toString())) && !isValidMobile(
                    enquiryBinding.edtInputPhoneHome.text.toString()
                )
            ) {
                enquiryBinding.edtInputPhoneHome.error = "Please enter Valid Phone(H)"
                enquiryBinding.edtInputPhoneHome.requestFocus()
            } else if (!TextUtils.isEmpty((enquiryBinding.edtInputMobile.text.toString())) && !isValidMobile(
                    enquiryBinding.edtInputMobile.text.toString()
                )
            ) {
                enquiryBinding.edtInputMobile.error = "Please enter Valid Mobile(B)"
                enquiryBinding.edtInputMobile.requestFocus()
            } else if (!TextUtils.isEmpty((enquiryBinding.edtInputPhoneP.text.toString())) && !isValidMobile(
                    enquiryBinding.edtInputPhoneP.text.toString()
                )
            ) {
                enquiryBinding.edtInputPhoneP.error = "Please enter Valid Phone(P)"
                enquiryBinding.edtInputPhoneP.requestFocus()
            } else if (!TextUtils.isEmpty((enquiryBinding.edtInputEmailPersonal.text.toString())) && !isEmailValid(
                    enquiryBinding.edtInputEmailPersonal.text.toString())) {
                enquiryBinding.edtInputEmailPersonal.error = "Please enter valid Email(P)"
                enquiryBinding.edtInputEmailPersonal.requestFocus()
            } else if (!TextUtils.isEmpty((enquiryBinding.edtInputEmailBroadcast.text.toString())) && !isEmailValid(
                    enquiryBinding.edtInputEmailBroadcast.text.toString())) {
                enquiryBinding.edtInputEmailBroadcast.error = "Please enter valid Email(B)"
                enquiryBinding.edtInputEmailBroadcast.requestFocus()
            }

            else if (enquiryBinding.spnSize.selectedItemPosition == 0) {
                enquiryBinding.tvTitle.error = "Please select Survey Size"
            } else if (flagCompany && enquiryBinding.edtInputCompanyName.isEmpty()) {
                enquiryBinding.edtInputCompanyName.error = "Please enter company name"
            } else {
                try {
                    var surveyType = ""
                    var enquirySize = ""
                    var enquiryType = ""
                    var rentalProperty = ""
                    var rentalCode = 0
                    var countryID = 0
                    var enquiryTypeId = 0
                    var languageId = 0


                    if (enquiryBinding.spnType.selectedItemPosition != 0 && enquiryBinding.spnType.selectedItemPosition != -1) {
                        surveyType = enquiryBinding.spnType.selectedItem.toString()
                    }
                    if (enquiryBinding.spnSize.selectedItemPosition != 0 && enquiryBinding.spnSize.selectedItemPosition != -1) {
                        enquirySize =
                            viewModel.getSurveySizeLiveList().value!![enquiryBinding.spnSize.selectedItemPosition - 1].surveySizeCode!!
                    }
                    if (enquiryBinding.spnEnquiryType.selectedItemPosition != 0 && enquiryBinding.spnEnquiryType.selectedItemPosition != -1) {
                        enquiryType = enquiryBinding.spnEnquiryType.selectedItem.toString()
                    }
                    if (enquiryBinding.spnRental.selectedItemPosition != -1) {
                        rentalCode = viewModel.getRentalLiveList().value!![enquiryBinding.spnRental.selectedItemPosition].rentalCode!!
                        rentalProperty = enquiryBinding.spnRental.selectedItem.toString()
                        Log.e("RentalCode",rentalCode.toString())
                    }


                    if (viewModel.getCountryLiveList().value != null && viewModel.getCountryLiveList().value!!.size > (enquiryBinding.spnCountry.selectedItemPosition - 1) && enquiryBinding.spnCountry.selectedItemPosition != 0) {
                        countryID =
                            viewModel.getCountryLiveList().value!![enquiryBinding.spnCountry.selectedItemPosition - 1].countryId.toInt()
                    }

                    if (viewModel.getLanguageLiveList().value != null && viewModel.getLanguageLiveList().value!!.size > 0) {
                        languageId =
                            viewModel.getLanguageLiveList().value!![enquiryBinding.spnLanguage.selectedItemPosition].languageId.toInt()
                    }

                    if (viewModel.getEnquiryTypeLiveList().value != null && viewModel.getEnquiryTypeLiveList().value!!.size > (enquiryBinding.spnEnquiryType.selectedItemPosition - 1) && enquiryBinding.spnEnquiryType.selectedItemPosition != 0) {
                        enquiryTypeId =
                            viewModel.getEnquiryTypeLiveList().value!![enquiryBinding.spnEnquiryType.selectedItemPosition - 1].enquiryTypeId!!.toInt()
                    }

                    val model = enquiryBinding.data!!
                    model.gender = if (enquiryBinding.rbMale.isChecked) "Male" else "Female"
                    model.isPetTrans = enquiryBinding.cbPetTransportation.isChecked
                    model.isForEx = enquiryBinding.cbForEx.isChecked
                    model.surveyType = surveyType
                    model.surveySize = enquirySize
                    /**
                     * Enquiry Type

                    Value	Description
                    0	Private
                    1	Corporate*/
                    if (enquiryType == "Private") {
                        model.enquiryType = "0"
                    } else {
                        model.enquiryType = "1"
                    }

                    model.originCountry = countryID
                    model.rentalProperty = rentalCode
                    model.languageId = languageId
                    model.surveyComment = enquiryBinding.edtInputComment.text.toString()
                    viewModel.onClickSave(model)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        enquiryBinding.edtInputMoveDate.setOnClickListener {
            DateAndTimeUtils.setDate(requireContext(), enquiryBinding.edtInputMoveDate)
        }

        enquiryBinding.edtInputMoveDate.addTextChangedListener(
            EditTextErrorResolver(
                enquiryBinding.edtInputMoveDate,
                enquiryBinding.inputMoveDate
            )
        )
        enquiryBinding.edtInputFirstName.addTextChangedListener(
            EditTextErrorResolver(
                enquiryBinding.edtInputFirstName,
                enquiryBinding.inputFirstName
            )
        )
        enquiryBinding.edtInputSurname.addTextChangedListener(
            EditTextErrorResolver(
                enquiryBinding.edtInputSurname,
                enquiryBinding.inputSurname
            )
        )
        enquiryBinding.edtInputCompanyName.addTextChangedListener(
            EditTextErrorResolver(
                enquiryBinding.edtInputCompanyName,
                enquiryBinding.inputCompanyName
            )
        )
        enquiryBinding.edtInputBooker.addTextChangedListener(
            EditTextErrorResolver(
                enquiryBinding.edtInputBooker,
                enquiryBinding.inputBooker
            )
        )
        enquiryBinding.edtInputPhone.addTextChangedListener(
            EditTextErrorResolver(
                enquiryBinding.edtInputPhone,
                enquiryBinding.inputPhone
            )
        )
        enquiryBinding.edtInputPhoneHome.addTextChangedListener(
            EditTextErrorResolver(
                enquiryBinding.edtInputPhoneHome,
                enquiryBinding.inputPhoneHome
            )
        )
        enquiryBinding.edtInputPhoneP.addTextChangedListener(
            EditTextErrorResolver(
                enquiryBinding.edtInputPhoneP,
                enquiryBinding.inputPhoneP
            )
        )
        enquiryBinding.edtInputMobile.addTextChangedListener(
            EditTextErrorResolver(
                enquiryBinding.edtInputMobile,
                enquiryBinding.inputMobile
            )
        )
        enquiryBinding.edtInputEmailPersonal.addTextChangedListener(
            EditTextErrorResolver(
                enquiryBinding.edtInputEmailPersonal,
                enquiryBinding.inputEmailPersonal
            )
        )
        enquiryBinding.edtInputEmailBroadcast.addTextChangedListener(
            EditTextErrorResolver(
                enquiryBinding.edtInputEmailBroadcast,
                enquiryBinding.inputEmailBroadcast
            )
        )
        enquiryBinding.edtInputAddress.addTextChangedListener(
            EditTextErrorResolver(
                enquiryBinding.edtInputAddress,
                enquiryBinding.inputAddress
            )
        )
        enquiryBinding.edtInputPostcode.addTextChangedListener(
            EditTextErrorResolver(
                enquiryBinding.edtInputPostcode,
                enquiryBinding.inputPostcode
            )
        )
        enquiryBinding.edtInputCity.addTextChangedListener(
            EditTextErrorResolver(
                enquiryBinding.edtInputCity,
                enquiryBinding.inputCity
            )
        )
        viewModel.isLoading.observe(requireActivity()) { isLoading ->
            if (isLoading && isAdded) {
                showProgressbar()
            } else if (!isLoading && isAdded) {
                hideProgressbar()
            }
        }

    }

    //  Set Tittle For Spinner
    private fun setSpinnerTitle() {
        enquiryBinding.spnEnquiryType.setTitle("");
        enquiryBinding.spnCountry.setTitle("");
        enquiryBinding.spnSize.setTitle("");
    }

    private fun selectCountry(countryId: String?) {
        val data = viewModel.getCountryLiveList()
        if (data.value != null) {
            var selectedposition = data.value!!.indexOfFirst { countryId == it.countryId }
            selectedposition = selectedposition + 1
            enquiryBinding.spnCountry.setSelection(selectedposition)
        }
    }

    private fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidMobile(phone: String): Boolean {
        return Patterns.PHONE.matcher(phone).matches()
    }

    override fun onPauseFragment() {

    }

    override fun onResumeFragment(s: String?) {
        if (arguments != null && requireArguments().containsKey(DATA) && requireArguments().get(DATA) != null) {
            selectedData = requireArguments().getSerializable(DATA) as DashboardModel?
            enquiryBinding.data = selectedData
            Log.e("Select", selectedData!!.enquiryType.toString())
        }

        viewModel.init(mContext)
        if (enquiryBinding.viewModel == null) {
            enquiryBinding.lifecycleOwner = this
            enquiryBinding.viewModel = viewModel
        }
        enquiryBinding.edtInputMoveDate.doOnTextChanged { text, _, _, _ ->
            viewModel.onUsernameTextChanged(
                text
            )
        }
    }



    private fun getSelectedEnquiryType(
        enquiryType: String?, list: ArrayList<EnquiryTypeModel>
    ): Int {
        return list.indexOfFirst { it.enquiryTypeName == enquiryType }
    }

    private fun getSelectedPropertyType(
        propertyType: String?,
        list: MutableLiveData<List<RentalTypeModel>>
    ): Int? {
        return list.value?.indexOfFirst { it.rentalCode.toString() == propertyType }
    }


    private fun getSelectedSurveySizeType(propertyType: String?, list: MutableLiveData<List<SurveySizeListModel>>): Int? {
        return list.value?.indexOfFirst { it.surveySizeCode == propertyType }
    }

    private fun getSelectedSurveyType(surveyType: String?, list: MutableLiveData<List<SurveyTypeModel>>): Int? {
        return list.value?.indexOfFirst { it.surveyType == surveyType }
    }
}