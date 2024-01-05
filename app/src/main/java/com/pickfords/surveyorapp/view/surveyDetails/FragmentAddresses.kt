package com.pickfords.surveyorapp.view.surveyDetails

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import com.pickfords.surveyorapp.R
import com.pickfords.surveyorapp.databinding.FragmentAddressesBinding
import com.pickfords.surveyorapp.interfaces.FragmentLifecycleInterface
import com.pickfords.surveyorapp.model.address.AddressListModel
import com.pickfords.surveyorapp.model.address.AddressTypeListModel
import com.pickfords.surveyorapp.model.dashboard.DashboardModel
import com.pickfords.surveyorapp.utils.EditTextErrorResolver
import com.pickfords.surveyorapp.utils.Session
import com.pickfords.surveyorapp.utils.SyncManager
import com.pickfords.surveyorapp.view.BaseActivity
import com.pickfords.surveyorapp.view.BaseFragment
import com.pickfords.surveyorapp.view.dialogs.MessageDialog
import com.pickfords.surveyorapp.view.surveyhistory.SurveyDetailsSequenceFragment
import com.pickfords.surveyorapp.viewModel.surveyDetails.AddressesViewModel

class FragmentAddresses : BaseFragment(), FragmentLifecycleInterface {
    lateinit var addressBinding: FragmentAddressesBinding
    private val viewModel by lazy { AddressesViewModel(requireActivity()) }
    private var selectedData: DashboardModel? = null

    companion object {
        fun newInstance(selectedData: DashboardModel?): FragmentAddresses {
            val bundle = Bundle()
            bundle.putSerializable(Session.DATA, selectedData)
            val fragmentAddresses = FragmentAddresses()
            fragmentAddresses.arguments = bundle
            return fragmentAddresses
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        addressBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_addresses, container, false)
        return addressBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setSpinnerTitle()
        setObserver()
        setAction()
    }

    // For All List Observer
    private fun setObserver() {


        viewModel.getSelectAddressModel().observe(requireActivity()) { address ->
            if (address != null) {
                if (!viewModel.addressTypeLiveList.value.isNullOrEmpty()) {
                    val indexaddressType = getSelectedAddressType(addressBinding.spnAddress.getSelectedItem().toString(), viewModel.addressTypeLiveList)
                    if(indexaddressType != -1){
                        addressBinding.spnAddressType.post {
                            addressBinding.spnAddressType.setSelection(
                                indexaddressType!! + 1
                            )
                        }
                    }
                }

                addressBinding.data = address

                selectCountry(address.countryId)

                if ((viewModel.getSequenceLegPermitDestinationLiveList().value?.size ?: 0) > 1)
                    addressBinding.spinnerDestinationPermit.setSelection(if (address.parkingPermit) 0 else 1)

                /* if (!viewModel.permitLiveList.value.isNullOrEmpty()) {
                     viewModel.permitLiveList.value?.forEachIndexed { index, permitTypeListModel ->
                         if (address.parkingPermitTypeId == permitTypeListModel.parkingPermitTypeId.toString()) {
                            // addressBinding.spinnerDestinationPermitType.setSelection(index)

                         }
                     }
                 }*/
                addressBinding.spinnerDestinationPermitType.setText(address.parkingPermitTypeId)

                if ((viewModel.getpropertyTypeLiveList().value?.size ?: 0) > 1) {
                    for (i in 0 until viewModel.getpropertyTypeLiveList().value?.size!!) {
                        if (address.propertyType == viewModel.getpropertyTypeLiveList().value?.get(i)?.propertyCode) {
                            addressBinding.spnPropertyType.setSelection(i)
                        }
                    }
                }

            } else {
                addressBinding.data = AddressListModel()
                addressBinding.spnCountryTwo.setSelection(227)
            }
        }

        viewModel.selectedAddressType.observe(requireActivity()) { selection ->
            if (addressBinding.inputAddress.error != null) {
                addressBinding.inputAddress.error = null
                addressBinding.inputAddress.isErrorEnabled = false
            }
        }

        viewModel.getSelectAddressTypeModel().observe(requireActivity()) { addressList ->
            if (addressList != null) {
                if (viewModel.isGetAddressFromType) {
                    addressBinding.tvAddressesTypeSpinnerTitle.error = null
                    if (addressList.address != null) {
                        addressBinding.edtInputAddress.setText(addressList.address.toString())
                    } else {
                        addressBinding.edtInputAddress.text?.clear()
                    }

                    if (addressList.city != null) {
                        addressBinding.edtInputCity.setText(addressList.city.toString())
                    } else {
                        addressBinding.edtInputCity.text?.clear()
                    }

                    if (addressList.postcode != null) {
                        addressBinding.edtInputPostcode.setText(addressList.postcode.toString())
                    } else {
                        addressBinding.edtInputPostcode.text?.clear()
                    }

                    if (addressList.countryId != null) {
                        selectCountry(addressList.countryId)
                    }
                }
            } else {
                addressBinding.data = AddressListModel()
                addressBinding.spnCountryTwo.setSelection(227)
                viewModel.isGetAddressFromType = true
            }
        }

        viewModel.isLoading.observe(requireActivity()) { isLoading ->
            if (isLoading && isAdded) showProgressbar()
            else if (!isLoading && isAdded) hideProgressbar()
        }
    }

    // For Set All Clicks
    private fun setAction() {
        addressBinding.btnAddAddress.setOnClickListener {
            viewModel.isGetAddressFromType = true
            if (addressBinding.spnAddress.selectedItemPosition > 0) {
                addressBinding.data = AddressListModel()
                addressBinding.spnAddress.setSelection(0)
                addressBinding.spnAddressType.setSelection(0)
                addressBinding.spnCountryTwo.setSelection(227)
            }
        }

        addressBinding.edtInputTitleName.addTextChangedListener(
            EditTextErrorResolver(
                addressBinding.edtInputTitleName,
                addressBinding.inputTitleName
            )
        )
        addressBinding.edtInputAddress.addTextChangedListener(
            EditTextErrorResolver(
                addressBinding.edtInputAddress,
                addressBinding.inputAddress
            )
        )
        addressBinding.edtInputCity.addTextChangedListener(
            EditTextErrorResolver(
                addressBinding.edtInputCity,
                addressBinding.inputCity
            )
        )
        addressBinding.edtInputPostcode.addTextChangedListener(
            EditTextErrorResolver(
                addressBinding.edtInputPostcode,
                addressBinding.inputPostcode
            )
        )
        addressBinding.edtCounty.addTextChangedListener(
            EditTextErrorResolver(
                addressBinding.edtCounty,
                addressBinding.inputCounty
            )
        )
        addressBinding.spnCountryTwo.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    addressBinding.tvCountryTwoSpinnerTitle.error = null
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
        addressBinding.edtInputTelephoneNumber.addTextChangedListener(
            EditTextErrorResolver(
                addressBinding.edtInputTelephoneNumber,
                addressBinding.inputTelephoneNumber
            )
        )

        addressBinding.btnSave.setOnClickListener {

            val data = addressBinding.data
//            var isRevisit = data!!.isRevisit
//            if (isRevisit!!){
//            //    MessageDialog(mContext, mContext.getString(R.string.revisit_error)).show()
//
//                MessageDialog(mContext, mContext.getString(R.string.revisit_error)).setListener(
//                    object : MessageDialog.OkButtonListener{
//                        override fun onOkPressed(dialog: MessageDialog) {
//                            dialog.dismiss()
//                            resetValue()
//                            requireFragmentManager().beginTransaction().detach(
//                                FragmentAddresses()
//                            ).attach(FragmentAddresses()).commit();
//
//
//                        }
//                    }
//                ).show()
//            }else{
//
//            }

            val address = viewModel.getAddressSpinnerAdapter()?.getItem(addressBinding.spnAddress.selectedItemPosition)
            val addressType = viewModel.getAddressTypeSpinnerAdapter()?.getItem(addressBinding.spnAddressType.selectedItemPosition)

            if (addressBinding.spnAddressType.selectedItemPosition == 0) {
                addressBinding.tvAddressesTypeSpinnerTitle.error = "Please select address Type"
                addressBinding.tvAddressesTypeSpinnerTitle.requestFocus()
            } else if (address == "01COLLECT" && addressType == "Collection Address" && TextUtils.isEmpty(addressBinding.edtInputAddress.text.toString())) {
                addressBinding.inputAddress.error = "Please enter Address"
                addressBinding.edtInputAddress.requestFocus()
            } else if (TextUtils.isEmpty(addressBinding.edtInputCity.text.toString())) {
                addressBinding.inputCity.error = "Please enter City"
                addressBinding.edtInputCity.requestFocus()
            }
            else if (addressBinding.spnCountryTwo.selectedItemPosition == 0) {
                addressBinding.tvCountryTwoSpinnerTitle.error = "Please select country"
            } else {
                val data = addressBinding.data
                data?.createdBy = session.user!!.userId
                data?.userId = session.user!!.userId
                data?.surveyId = selectedData!!.surveyId
                data?.isChangedField
                Log.e("RevisitAddress",data?.isRevisit.toString())

                if (viewModel.getCountryLiveList().value != null &&
                    viewModel.getCountryLiveList().value!!.size > (addressBinding.spnCountryTwo.selectedItemPosition - 1) && addressBinding.spnCountryTwo.selectedItemPosition != 0
                )
                    data?.countryId =
                        viewModel.getCountryLiveList().value!![addressBinding.spnCountryTwo.selectedItemPosition - 1].countryId

                data?.isDestinationLongCarry = addressBinding.cbLongCarryDestination?.isChecked
                data?.isDestinationShittle = addressBinding.cbShuttleDestination?.isChecked
                data?.lift = addressBinding.inputElevator.isChecked
                data?.titleName =
                    viewModel.getAddressTypeLiveListData().value!![addressBinding.spnAddressType.selectedItemPosition - 1].addressCode
                data?.addressTypeId =
                    viewModel.getAddressTypeLiveListData().value!![addressBinding.spnAddressType.selectedItemPosition - 1].addressTypeId

                if (addressBinding.spnPropertyType.selectedItemPosition > 0) {
                    data?.propertyType =
                        viewModel.getpropertyTypeLiveList().value!![addressBinding.spnPropertyType.selectedItemPosition].propertyCode
                }

                if (viewModel.getSequenceLegPermitDestinationLiveList() != null && viewModel.getSequenceLegPermitDestinationLiveList().value?.size ?: 0 > 0) {
                    data?.parkingPermit =
                        viewModel.getSequenceLegPermitDestinationLiveList().value?.get(
                            addressBinding.spinnerDestinationPermit.selectedItemPosition
                        )?.name.toString().lowercase() == "yes"
                }

                /*if (viewModel.getSequenceLegDestinationDistanceTypeLiveList() != null && viewModel.getSequenceLegDestinationDistanceTypeLiveList().value?.size ?: 0 > 0) {
                    data?.distanceUnit =
                        viewModel.getSequenceLegDestinationDistanceTypeLiveList().value?.get(
                            addressBinding.spinnerDistanceUnitOrigin.selectedItemPosition
                        )?.distanceUnitId.toString()
                }*/
                if (viewModel.getSequenceLegDestinationDistanceTypeLiveList() != null && viewModel.getSequenceLegDestinationDistanceTypeLiveList().value?.size ?: 0 > 0) {
                    // data?.distanceUnit = viewModel.getSequenceLegDestinationDistanceTypeLiveList().value?.get(addressBinding.spinnerDistanceUnitOrigin.selectedItemPosition)?.distanceUnitId.toString()
                }

                /*if ((viewModel.getPermitTypeLiveList().value?.size ?: 0) > 0) {
                    data?.parkingPermitTypeId =
                        viewModel.getPermitTypeLiveList().value?.get(
                            addressBinding.spinnerDestinationPermitType.selectedItemPosition
                        )?.parkingPermitTypeId.toString()
                }*/
                data?.parkingPermitTypeId =
                    addressBinding.spinnerDestinationPermitType.text.toString()
                viewModel.saveSurveyAddress(data, addressBinding)
            }
        }
    }

    private fun resetValue() {
        try {
            viewModel.init(mContext)
            addressBinding.lifecycleOwner = this
            addressBinding.viewModel = viewModel
            if (arguments != null && requireArguments().containsKey(Session.DATA) && requireArguments().get(Session.DATA) != null) {
                selectedData = requireArguments().getSerializable(Session.DATA) as DashboardModel?
                addressBinding.mainData = selectedData
               viewModel.setSurveyId(selectedData!!.surveyId)
              //  Thread.sleep(200)
              //  setDefaultSequence()
            }
        }catch (e: Exception){
        }
    }

    // For set Spinner Title
    private fun setSpinnerTitle() {
        addressBinding.spnAddress.setTitle("");
        addressBinding.spnCountryTwo.setTitle("");
        addressBinding.spnAddressType.setTitle("");
    }

    // For Select Country
    private fun selectCountry(countryId: String?) {
        val data = viewModel.getCountryLiveList()
        if (!countryId.isNullOrEmpty()) {
            val countryModel =
                (context as BaseActivity).countryListDao.getCountryNameFromId(countryId.toInt())
            if (data.value != null && countryModel != null && !countryModel.countryName.isNullOrEmpty())
                for (i in data.value!!.indices) {
                    if (countryModel.countryName == data.value!![i].countryName) {
                        addressBinding.spnCountryTwo.setSelection(i + 1)
                    }
                }
        }
    }

    override fun onPauseFragment() {
    }

    // For Select Default Sequence First Time
    private fun setDefaultSequence() {
        viewModel.getAddressLiveList().observe(requireActivity()) { sequence ->
            if (sequence != null && sequence.isNotEmpty()) {
                addressBinding.spnAddress.setSelection(1)
            }
        }
    }

    override fun onResumeFragment(s: String?) {
        viewModel.init(mContext)
        addressBinding.lifecycleOwner = this
        addressBinding.viewModel = viewModel
        if (arguments != null && requireArguments().containsKey(Session.DATA) && requireArguments().get(Session.DATA) != null) {
            selectedData = requireArguments().getSerializable(Session.DATA) as DashboardModel?
            addressBinding.mainData = selectedData
            viewModel.setSurveyId(selectedData!!.surveyId)
            Thread.sleep(200)
            setDefaultSequence()
            addressBinding.tvAddressesTypeSpinnerTitle.error = null
        }
    }

    private fun getSelectedAddressType(
        title: String,
        addressTypeLiveList: MutableLiveData<List<AddressTypeListModel>>
    ): Int? {
        return addressTypeLiveList.value?.indexOfFirst { it.addressCode.equals(title, true) }
    }

}