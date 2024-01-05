package com.pickfords.surveyorapp.view.surveyhistory

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.pickfords.surveyorapp.DashboardActivity
import com.pickfords.surveyorapp.R
import com.pickfords.surveyorapp.databinding.FragmentSurveyDetailsSequenceEditLegsBinding
import com.pickfords.surveyorapp.interfaces.InventorySelection
import com.pickfords.surveyorapp.model.surveyDetails.InventoryTypeSelectionModel
import com.pickfords.surveyorapp.model.surveyDetails.ShowLegsModel
import com.pickfords.surveyorapp.utils.DateAndTimeUtils
import com.pickfords.surveyorapp.utils.EditTextErrorResolver
import com.pickfords.surveyorapp.utils.Session
import com.pickfords.surveyorapp.view.BaseFragment
import com.pickfords.surveyorapp.viewModel.surveyDetails.LegsViewModel
import java.io.Serializable

class SurveyDetailsSequenceEditLegsFragment : BaseFragment(), Serializable {
    private lateinit var addLegsBinding: FragmentSurveyDetailsSequenceEditLegsBinding
    private val viewModel by lazy { LegsViewModel(requireActivity()) }

    var showLegsModel: ShowLegsModel? = null
    private var isUpdateLeg: Boolean = false
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        addLegsBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_survey_details_sequence_edit_legs,
            container,
            false
        )
        return addLegsBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbarWithMenu(requireActivity().getString(R.string.survey_details))
        if (savedInstanceState == null)
            viewModel.init(mContext)
        addLegsBinding.tvPackingDate.doOnTextChanged { text, _, _, _ ->
            viewModel.onUsernameTextChanged(text)
        }
        val session = Session(requireContext())
        addLegsBinding.lifecycleOwner = this
        addLegsBinding.viewModel = viewModel

        setSpinnerValue()
        getBundleData()
        setAction()
        setObserver()
    }
    // For set Observer
    private fun setObserver() {
        viewModel.isLoading.observe(requireActivity()) { isLoading ->
            if (isLoading && isAdded) showProgressbar()
            else if (!isLoading && isAdded) hideProgressbar()
        }
    }

    // For get Bundle Data
    private fun getBundleData() {
        if (arguments != null && requireArguments().containsKey("surveyid")) {
            if (requireArguments().containsKey("showLegsModel")) {
                showLegsModel = requireArguments().getSerializable("showLegsModel") as ShowLegsModel
            }
            if (requireArguments().containsKey("isUpdateLeg")) {
                isUpdateLeg = requireArguments().getBoolean("isUpdateLeg")
                addLegsBinding.isEdit = isUpdateLeg
                addLegsBinding.showLegsModel = showLegsModel
            }
            showLegsModel!!.surveyId = requireArguments().getString("surveyid").toString()
            viewModel.getAddressOriginList(requireActivity(), showLegsModel!!.surveyId)
            showLegsModel!!.surveySequenceId =
                requireArguments().getString("surveySequenceId").toString()
            showLegsModel!!.surveySequenceLeg = requireArguments().getString("leg")

            showLegsModel!!.createdBy = session.user?.userId.toString()

        }

    }

    // For Set Action
    private fun setAction() {
        addLegsBinding.btnUpdateLegs.setOnClickListener {
            checkValidation()
        }

        addLegsBinding.btnCancelAddLegs.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        addLegsBinding.tvPackingDate.setOnClickListener {
            DateAndTimeUtils.setDate(requireContext(), addLegsBinding.tvPackingDate)
        }
        addLegsBinding.edOriginfloor.addTextChangedListener(
            EditTextErrorResolver(
                addLegsBinding.edOriginfloor,
                addLegsBinding.floorOriginLayout
            )
        )
        addLegsBinding.edAddDestinationFloor.addTextChangedListener(
            EditTextErrorResolver(
                addLegsBinding.edAddDestinationFloor,
                addLegsBinding.addFloorDestinationLayout
            )
        )

        addLegsBinding.edaddStairOrigin.addTextChangedListener(
            EditTextErrorResolver(
                addLegsBinding.edaddStairOrigin,
                addLegsBinding.tvaddStairOriginLayout
            )
        )
        addLegsBinding.addStairDestination.addTextChangedListener(
            EditTextErrorResolver(
                addLegsBinding.addStairDestination,
                addLegsBinding.addStairDestinationLayout
            )
        )
        addLegsBinding.edaddDistanceOrigin.addTextChangedListener(
            EditTextErrorResolver(
                addLegsBinding.edaddDistanceOrigin,
                addLegsBinding.addStairOriginLayout
            )
        )
        addLegsBinding.edaddDistanceDestination.addTextChangedListener(
            EditTextErrorResolver(
                addLegsBinding.edaddDistanceDestination,
                addLegsBinding.addDistanceDestinationLayout
            )
        )
        addLegsBinding.edaddLocationOrigin.addTextChangedListener(
            EditTextErrorResolver(
                addLegsBinding.edaddLocationOrigin,
                addLegsBinding.addLocationOriginLayout
            )
        )
        addLegsBinding.edaddLocationDestination.addTextChangedListener(
            EditTextErrorResolver(
                addLegsBinding.edaddLocationDestination,
                addLegsBinding.addLocationDestinationLayout
            )
        )

        addLegsBinding.edaddElevatorOrigin.addTextChangedListener(
            EditTextErrorResolver(
                addLegsBinding.edaddElevatorOrigin,
                addLegsBinding.addOriginElevatorLayout
            )
        )
        addLegsBinding.edaddElevatorDestination.addTextChangedListener(
            EditTextErrorResolver(
                addLegsBinding.edaddElevatorDestination,
                addLegsBinding.addDestinationElevatorLayout
            )
        )
        addLegsBinding.edAllowance.addTextChangedListener(
            EditTextErrorResolver(
                addLegsBinding.edAllowance,
                addLegsBinding.addDestinationElevatorLayout
            )
        )

        addLegsBinding.edDistanceinPackagingLegs.addTextChangedListener(
            EditTextErrorResolver(
                addLegsBinding.edDistanceinPackagingLegs,
                addLegsBinding.DistanceinPackagingLegsLayout
            )
        )
    }

    // For Set Spinner Value
    private fun setSpinnerValue() {
        //Address
        viewModel.getSequenceLegAddressOriginLiveList()
            .observe(requireActivity()) { originAddress ->
                var i = 0
                for (data in originAddress) {

                    if (data.titleName == showLegsModel!!.originAddress) {
                        addLegsBinding.spinnerOriginAddress.setSelection(i)
                    }
                    i += 1
                }
            }
        viewModel.getSequenceLegAddressDestinationLiveList()
            .observe(requireActivity()) { destinationAddress ->
                var i = 0
                for (data in destinationAddress) {
                    if (data.titleName == showLegsModel!!.destinationAddress) {
                        addLegsBinding.spinnerDestinationAddress.setSelection(i)
                    }
                    i += 1
                }
            }

        //Type
        viewModel.getSequenceLegTypeOriginLiveList()
            .observe(requireActivity()) { originType ->
                var i = 0

                for (data in originType) {
                    if (data.name == showLegsModel!!.originAddressType) {
                        addLegsBinding.spinnerOriginType.setSelection(i)
                    }
                    i += 1
                }
            }
        viewModel.getSequenceLegTypeDestinationLiveList()
            .observe(requireActivity()) { destinationType ->
                var i = 0
                for (data in destinationType) {
                    if (data.name == showLegsModel!!.destinationAddressType) {
                        addLegsBinding.spinnerDestinationType.setSelection(i)
                    }
                    i += 1
                }
            }

        //Distance Unit
        viewModel.getSequenceLegOriginDistanceTypeLiveList()
            .observe(requireActivity()) { originUnit ->
                var i = 0
                for (data in originUnit) {
                    if (data.distanceUnit == showLegsModel!!.originDistanceUnit) {
                        addLegsBinding.spinnerDistanceUnitOrigin.setSelection(i)
                    }
                    i += 1
                }
            }
        viewModel.getSequenceLegDestinationDistanceTypeLiveList()
            .observe(requireActivity()) { originUnit ->
                var i = 0
                for (data in originUnit) {
                    if (data.distanceUnit == showLegsModel!!.destinationDistanceUnit) {
                        addLegsBinding.spinnerDistanceUnitDestination.setSelection(i)
                    }
                    i += 1
                }
            }

        //Permit
        viewModel.getSequenceLegPermitOriginLiveList()
            .observe(requireActivity()) { originPermit ->
                var i = 0
                for (data in originPermit) {
                    if (data.name == showLegsModel!!.originPermit) {
                        addLegsBinding.spinnerOriginPermit.setSelection(i)
                    }
                    i += 1
                }
            }
        viewModel.getSequenceLegPermitDestinationLiveList()
            .observe(requireActivity()) { destinationPermit ->
                var i = 0
                for (data in destinationPermit) {
                    if (data.name == showLegsModel!!.destinationPermit) {
                        addLegsBinding.spinnerDestinationPermit.setSelection(i)
                    }
                    i += 1
                }
            }

        //Allowance Type
        viewModel.getSequenceLegAllowanceTypeLiveList()
            .observe(requireActivity()) { allowance ->
                var i = 0
                for (data in allowance) {
                    if (data.name == showLegsModel!!.allowanceType) {
                        addLegsBinding.spinnerallowanceType.setSelection(i)
                    }
                    i += 1
                }
            }

        //Delivery Type
        viewModel.getSequenceDeliveryTypeLiveList()
            .observe(requireActivity()) { deliveryType ->
                var i = 0
                for (data in deliveryType) {
                    if (data.deliveryType == showLegsModel!!.deliveryType) {
                        addLegsBinding.spinnerDeliveryType.setSelection(i)
                    }
                    i += 1
                }
            }
    }

    private fun checkValidation() {
        Log.d("onClick", "Update Button CLicked")
        when {
            TextUtils.isEmpty(addLegsBinding.edOriginfloor.text.toString()) -> {
                addLegsBinding.edOriginfloor.error =
                    "Please enter value in floor Origin"
                addLegsBinding.edOriginfloor.requestFocus()
            }
            TextUtils.isEmpty(addLegsBinding.edAddDestinationFloor.text.toString()) -> {
                addLegsBinding.edAddDestinationFloor.error =
                    "Please enter value in floor Destination"
                addLegsBinding.edAddDestinationFloor.requestFocus()
            }
            TextUtils.isEmpty(addLegsBinding.edOriginaccess.text.toString()) -> {
                addLegsBinding.edOriginaccess.error = "Please enter Access"
                addLegsBinding.edOriginaccess.requestFocus()
            }
            TextUtils.isEmpty(addLegsBinding.edDestinationaccess.text.toString()) -> {
                addLegsBinding.edDestinationaccess.error = "Please enter Access"
                addLegsBinding.edDestinationaccess.requestFocus()
            }
            TextUtils.isEmpty(addLegsBinding.edaddStairOrigin.text.toString()) -> {
                addLegsBinding.edaddStairOrigin.error =
                    "Please enter value in stair Origin"
                addLegsBinding.edaddStairOrigin.requestFocus()
            }
            TextUtils.isEmpty(addLegsBinding.addStairDestination.text.toString()) -> {
                addLegsBinding.addStairDestination.error =
                    "Please enter value in Stair Destination"
                addLegsBinding.addStairDestination.requestFocus()
            }
            TextUtils.isEmpty(addLegsBinding.edaddDistanceOrigin.text.toString()) -> {
                addLegsBinding.edaddDistanceOrigin.error =
                    "Please enter value of Distance in Origin"
                addLegsBinding.edaddDistanceOrigin.requestFocus()
            }
            TextUtils.isEmpty(addLegsBinding.edaddDistanceDestination.text.toString()) -> {
                addLegsBinding.edaddDistanceDestination.error =
                    "Please enter value of Distance in Destination"
                addLegsBinding.edaddDistanceDestination.requestFocus()
            }
            TextUtils.isEmpty(addLegsBinding.edaddElevatorOrigin.text.toString()) -> {
                addLegsBinding.edaddElevatorOrigin.error =
                    "Please enter elevator value in origin"
                addLegsBinding.edaddElevatorOrigin.requestFocus()
            }
            TextUtils.isEmpty(addLegsBinding.edaddElevatorDestination.text.toString()) -> {
                addLegsBinding.edaddElevatorDestination.error =
                    "Please enter elevator value in Destination"
                addLegsBinding.edaddElevatorDestination.requestFocus()
            }
            TextUtils.isEmpty(addLegsBinding.edAllowance.text.toString()) -> {
                addLegsBinding.edAllowance.error = "Please enter allowance"
                addLegsBinding.edAllowance.requestFocus()
            }
            TextUtils.isEmpty(addLegsBinding.edDistanceinPackagingLegs.text.toString()) -> {
                addLegsBinding.edDistanceinPackagingLegs.error =
                    "Please enter distance in packaging"
                addLegsBinding.edDistanceinPackagingLegs.requestFocus()
            }

            TextUtils.isEmpty(addLegsBinding.tvPackingDate.text.toString()) -> {
                addLegsBinding.tvPackingDate.error = "Please enter Packing date"
                addLegsBinding.tvPackingDate.requestFocus()
            }
            addLegsBinding.spinnerOriginAddress.selectedItemPosition < 0 -> {
                Toast.makeText(
                    mContext,
                    mContext.getString(R.string.select_origin_address),
                    Toast.LENGTH_SHORT
                ).show()
                addLegsBinding.spinnerOriginAddress.requestFocus()
            }
            addLegsBinding.spinnerDestinationAddress.selectedItemPosition < 0 -> {
                Toast.makeText(
                    mContext,
                    mContext.getString(R.string.select_destination_address),
                    Toast.LENGTH_SHORT
                ).show()
                addLegsBinding.spinnerDestinationAddress.requestFocus()
            }
            addLegsBinding.spinnerOriginType.selectedItemPosition < 0 -> {
                Toast.makeText(
                    mContext,
                    mContext.getString(R.string.select_origin_address_type),
                    Toast.LENGTH_SHORT
                ).show()
                addLegsBinding.spinnerOriginType.requestFocus()
            }
            addLegsBinding.spinnerDestinationType.selectedItemPosition < 0 -> {
                Toast.makeText(
                    mContext,
                    mContext.getString(R.string.select_destination_address_type),
                    Toast.LENGTH_SHORT
                ).show()
                addLegsBinding.spinnerDestinationType.requestFocus()
            }
            addLegsBinding.spinnerOriginPermit.selectedItemPosition < 0 -> {
                Toast.makeText(
                    mContext,
                    mContext.getString(R.string.select_origin_permit),
                    Toast.LENGTH_SHORT
                ).show()
                addLegsBinding.spinnerOriginPermit.requestFocus()
            }
            addLegsBinding.spinnerDestinationPermit.selectedItemPosition < 0 -> {
                Toast.makeText(
                    mContext,
                    mContext.getString(R.string.select_destination_permit),
                    Toast.LENGTH_SHORT
                ).show()
                addLegsBinding.spinnerDestinationPermit.requestFocus()
            }
            addLegsBinding.spinnerallowanceType.selectedItemPosition < 0 -> {
                Toast.makeText(
                    mContext,
                    mContext.getString(R.string.select_allowance_type),
                    Toast.LENGTH_SHORT
                ).show()
                addLegsBinding.spinnerallowanceType.requestFocus()
            }
            addLegsBinding.spinnerDeliveryType.selectedItemPosition < 0 -> {
                Toast.makeText(
                    mContext,
                    mContext.getString(R.string.select_delivery_type),
                    Toast.LENGTH_SHORT
                ).show()
                addLegsBinding.spinnerDeliveryType.requestFocus()
            }
            else -> {
                showLegsModel = addLegsBinding.showLegsModel!!
                updateData()
            }
        }
    }

   //  For Update Data
    private fun updateData() {
        showLegsModel!!.isOriginLongCarry = addLegsBinding.cbLongCarryOrigin.isChecked
        showLegsModel!!.isOriginShittle = addLegsBinding.cbShuttleOrigin.isChecked
        showLegsModel!!.isDestinationLongCarry = addLegsBinding.cbLongCarryDestination.isChecked
        showLegsModel!!.isDestinationShittle = addLegsBinding.cbShuttleDestination.isChecked

        showLegsModel!!.originAddress = addLegsBinding.spinnerOriginAddress.selectedItem.toString()
        showLegsModel!!.originAddressType = addLegsBinding.spinnerOriginType.selectedItem.toString()
        showLegsModel!!.originPermit = addLegsBinding.spinnerOriginPermit.selectedItem.toString()
        showLegsModel!!.originDistanceUnit =
            addLegsBinding.spinnerDistanceUnitOrigin.selectedItem.toString()
        showLegsModel!!.originDistanceUnitId =
            viewModel.getSequenceLegOriginDistanceTypeLiveList().value?.get(addLegsBinding.spinnerDistanceUnitOrigin.selectedItemPosition)?.distanceUnitId.toString()

        //Destination
        showLegsModel!!.destinationAddress =
            addLegsBinding.spinnerDestinationAddress.selectedItem.toString()
        showLegsModel!!.destinationAddressType =
            addLegsBinding.spinnerDestinationType.selectedItem.toString()
        showLegsModel!!.destinationPermit =
            addLegsBinding.spinnerDestinationPermit.selectedItem.toString()
        showLegsModel!!.destinationDistanceUnit =
            addLegsBinding.spinnerDistanceUnitDestination.selectedItem.toString()
        showLegsModel!!.destinationDistanceUnitId =
            viewModel.getSequenceLegDestinationDistanceTypeLiveList().value?.get(addLegsBinding.spinnerDistanceUnitDestination.selectedItemPosition)?.distanceUnitId.toString()

        showLegsModel!!.allowanceType = addLegsBinding.spinnerallowanceType.selectedItem.toString()
        showLegsModel!!.deliveryType = addLegsBinding.spinnerDeliveryType.selectedItem.toString()

        showLegsModel?.destinationPermitId =
            viewModel.getSequenceLegPermitDestinationLiveList().value?.get(addLegsBinding.spinnerDestinationPermit.selectedItemPosition)?.permitId.toString()
        showLegsModel!!.originPermitId =
            viewModel.getSequenceLegPermitOriginLiveList().value?.get(addLegsBinding.spinnerOriginPermit.selectedItemPosition)?.permitId.toString()
        showLegsModel!!.originAddressTypeId =
            viewModel.getSequenceLegTypeOriginLiveList().value?.get(addLegsBinding.spinnerOriginType.selectedItemPosition)?.legTypeId.toString()
        showLegsModel!!.destinationAddressTypeId =
            viewModel.getSequenceLegTypeDestinationLiveList().value?.get(addLegsBinding.spinnerDestinationType.selectedItemPosition)?.legTypeId.toString()
        showLegsModel!!.pakingDate = viewModel.date.get()
        showLegsModel!!.deliveryTypeId =
            viewModel.getSequenceDeliveryTypeLiveList().value?.get(addLegsBinding.spinnerDeliveryType.selectedItemPosition)?.deliveryTypeId.toString()
        this.showLegsModel!!.originAddressId =
            viewModel.getSequenceLegAddressOriginLiveList().value?.get(addLegsBinding.spinnerOriginAddress.selectedItemPosition)?.surveyAddressId.toString()
        this.showLegsModel!!.destinationAddressId =
            viewModel.getSequenceLegAddressDestinationLiveList().value?.get(addLegsBinding.spinnerDestinationAddress.selectedItemPosition)?.surveyAddressId.toString()
        this.showLegsModel!!.allowanceTypeId =
            viewModel.getSequenceLegAllowanceTypeLiveList().value?.get(addLegsBinding.spinnerallowanceType.selectedItemPosition)?.allowanceTypeId.toString()
        this.showLegsModel!!.delivery = "1"
        viewModel.updateSequenceLeg(showLegsModel!!, object : InventorySelection {
            override fun onSelectedItem(name: String?, model: InventoryTypeSelectionModel?) {
                (activity as DashboardActivity).navController.previousBackStackEntry!!
                    .savedStateHandle.set<String>("id", showLegsModel!!.surveySequenceId)
                findNavController().navigateUp()
            }

            override fun onSelectedItem(
                name: String?,
                model: InventoryTypeSelectionModel?,
                position: Int?,
                isCallSaveAPI: Boolean,
                isChildItem: Boolean,
                childDescription: String) {

            }


            override fun onLongClickDeSelectedItem(name: String?, model: InventoryTypeSelectionModel?, position: Int?) {

            }

            override fun onCountIncreaseSelectedItem(model: InventoryTypeSelectionModel?, position: Int?) {

            }

            override fun onAddInventoryAddSubItem(
                name: String?,
                model: InventoryTypeSelectionModel?,
                position: Int?,
                description: String?,
                count: String?
            ) {

            }

            override fun onChildItemDelete() {

            }

            override fun onParentDelete(inventoryId: String?, position: Int?, name: String?) {

            }

            override fun onChildUpdate(name: String?, count: String?) {

            }
        })

    }
}