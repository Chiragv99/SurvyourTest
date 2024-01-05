package com.pickfords.surveyorapp.view.surveyhistory

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.pickfords.surveyorapp.DashboardActivity
import com.pickfords.surveyorapp.R
import com.pickfords.surveyorapp.databinding.FragmentSurveyDetailsSequenceAddLegsBinding
import com.pickfords.surveyorapp.interfaces.InventorySelection
import com.pickfords.surveyorapp.model.surveyDetails.InventoryTypeSelectionModel
import com.pickfords.surveyorapp.model.surveyDetails.ShowLegsModel
import com.pickfords.surveyorapp.utils.DateAndTimeUtils
import com.pickfords.surveyorapp.utils.EditTextErrorResolver
import com.pickfords.surveyorapp.utils.Session
import com.pickfords.surveyorapp.view.BaseFragment
import com.pickfords.surveyorapp.viewModel.surveyDetails.LegsViewModel
import java.io.Serializable


class SurveyDetailsSequenceAddLegsFragment : BaseFragment(), Serializable {

    private lateinit var addLegsBinding: FragmentSurveyDetailsSequenceAddLegsBinding
    private val viewModel by lazy { LegsViewModel(requireActivity()) }
    private var dataModel: ShowLegsModel = ShowLegsModel()

    //var onLegsSaved: OnLegsSaved? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        addLegsBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_survey_details_sequence_add_legs,
            container,
            false
        )
        return addLegsBinding.root
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbarWithMenu(requireActivity().getString(R.string.survey_details))

        if (savedInstanceState == null)
            viewModel.init(mContext)
            setAction()

        val session = Session(requireContext())
        addLegsBinding.lifecycleOwner = this
        addLegsBinding.viewModel = viewModel

        if (arguments != null && requireArguments().containsKey("surveyid")
        ) {
            //onLegsSaved = requireArguments().getSerializable("onLegsSaved") as OnLegsSaved?
            dataModel.surveyId = requireArguments().getString("surveyid")
            viewModel.getAddressOriginList(requireActivity(), dataModel.surveyId)
            dataModel.surveySequenceId = requireArguments().getString("surveySequenceId")
            dataModel.surveySequenceLeg = requireArguments().getString("leg")
            dataModel.createdBy = session.user?.userId
            addLegsBinding.data = dataModel
        }

    }

    private fun setAction() {
        addLegsBinding.tvPackingDate.doOnTextChanged { text, _, _, _ ->
            viewModel.onUsernameTextChanged(
                text
            )
        }

        addLegsBinding.btnSaveAddLegs.setOnClickListener {
            when {
                TextUtils.isEmpty(addLegsBinding.edOriginfloor.text.toString()) -> {
                    addLegsBinding.edOriginfloor.error = "Please enter value in floor Origin"
                    addLegsBinding.edOriginfloor.requestFocus()
                }
                TextUtils.isEmpty(addLegsBinding.edAddDestinationFloor.text.toString()) -> {
                    addLegsBinding.edAddDestinationFloor.error =
                        "Please enter value in floor Destination"
                    addLegsBinding.edAddDestinationFloor.requestFocus()
                }
                TextUtils.isEmpty(addLegsBinding.edaddStairOrigin.text.toString()) -> {
                    addLegsBinding.edaddStairOrigin.error = "Please enter value in stair Origin"
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
                TextUtils.isEmpty(addLegsBinding.edOriginaccess.text.toString()) -> {
                    addLegsBinding.edOriginaccess.error = "Please enter Access"
                    addLegsBinding.edOriginaccess.requestFocus()
                }
                TextUtils.isEmpty(addLegsBinding.edDestinationaccess.text.toString()) -> {
                    addLegsBinding.edDestinationaccess.error = "Please enter Access"
                    addLegsBinding.edDestinationaccess.requestFocus()
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
                    dataModel = addLegsBinding.data!!
                    setSpinnerValue()
                }
            }
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
        addLegsBinding.edOriginaccess.addTextChangedListener(
            EditTextErrorResolver(
                addLegsBinding.edOriginaccess,
                addLegsBinding.AccessOrigin
            )
        )
        addLegsBinding.edDestinationaccess.addTextChangedListener(
            EditTextErrorResolver(
                addLegsBinding.edDestinationaccess,
                addLegsBinding.AccessDestination
            )
        )

        viewModel.isLoading.observe(requireActivity()) { isLoading ->
            if (isLoading && isAdded) showProgressbar()
            else if (!isLoading && isAdded) hideProgressbar()
        }

        addLegsBinding.btnCancelAddLegs.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun setSpinnerValue() {
        this.dataModel.surveySequenceLegId = 0.toString()

        //Origin
        dataModel.originAddress = addLegsBinding.spinnerOriginAddress.selectedItem.toString()
        dataModel.originAddressType = addLegsBinding.spinnerOriginType.selectedItem.toString()
        //dataModel.originVehicleAccess = addLegsBinding.spinnerOriginAccess.selectedItem.toString()
        dataModel.originPermit = addLegsBinding.spinnerOriginPermit.selectedItem.toString()
        dataModel.originDistanceUnit =
            addLegsBinding.spinnerDistanceUnitOrigin.selectedItem.toString()
        dataModel.originDistanceUnitId =
            viewModel.getSequenceLegOriginDistanceTypeLiveList().value?.get(addLegsBinding.spinnerDistanceUnitOrigin.selectedItemPosition)?.distanceUnitId.toString()

        //Destination
        dataModel.destinationAddress =
            addLegsBinding.spinnerDestinationAddress.selectedItem.toString()
        dataModel.destinationAddressType =
            addLegsBinding.spinnerDestinationType.selectedItem.toString()
        dataModel.destinationDistanceUnit =
            addLegsBinding.spinnerDistanceUnitDestination.selectedItem.toString()
        dataModel.destinationDistanceUnitId =
            viewModel.getSequenceLegDestinationDistanceTypeLiveList().value?.get(addLegsBinding.spinnerDistanceUnitDestination.selectedItemPosition)?.distanceUnitId.toString()
        //dataModel.destinationVehicleAccess = addLegsBinding.spinnerDestinationAccess.selectedItem.toString()
        dataModel.destinationPermit =
            addLegsBinding.spinnerDestinationPermit.selectedItem.toString()

        dataModel.allowanceType = addLegsBinding.spinnerallowanceType.selectedItem.toString()
        dataModel.deliveryType = addLegsBinding.spinnerDeliveryType.selectedItem.toString()

        this.dataModel.isOriginLongCarry = addLegsBinding.cbLongCarryOrigin.isChecked
        dataModel.isOriginShittle = addLegsBinding.cbShuttleOrigin.isChecked
        dataModel.isDestinationLongCarry = addLegsBinding.cbLongCarryDestination.isChecked
        dataModel.isDestinationShittle = addLegsBinding.cbShuttleDestination.isChecked
        dataModel.destinationPermitId =
            viewModel.getSequenceLegPermitDestinationLiveList().value?.get(addLegsBinding.spinnerDestinationPermit.selectedItemPosition)?.permitId.toString()
        dataModel.originPermitId =
            viewModel.getSequenceLegPermitOriginLiveList().value?.get(addLegsBinding.spinnerOriginPermit.selectedItemPosition)?.permitId.toString()
        dataModel.originAddressTypeId =
            viewModel.getSequenceLegTypeOriginLiveList().value?.get(addLegsBinding.spinnerOriginType.selectedItemPosition)?.legTypeId.toString()
        dataModel.destinationAddressTypeId =
            viewModel.getSequenceLegTypeDestinationLiveList().value?.get(addLegsBinding.spinnerDestinationType.selectedItemPosition)?.legTypeId.toString()
        dataModel.pakingDate = viewModel.date.get()
        dataModel.deliveryTypeId =
            viewModel.getSequenceDeliveryTypeLiveList().value?.get(addLegsBinding.spinnerDeliveryType.selectedItemPosition)?.deliveryTypeId.toString()
        this.dataModel.originAddressId =
            viewModel.getSequenceLegAddressOriginLiveList().value?.get(addLegsBinding.spinnerOriginAddress.selectedItemPosition)?.surveyAddressId.toString()
        this.dataModel.destinationAddressId =
            viewModel.getSequenceLegAddressDestinationLiveList().value?.get(addLegsBinding.spinnerDestinationAddress.selectedItemPosition)?.surveyAddressId.toString()
        this.dataModel.allowanceTypeId =
            viewModel.getSequenceLegAllowanceTypeLiveList().value?.get(addLegsBinding.spinnerallowanceType.selectedItemPosition)?.allowanceTypeId.toString()
        this.dataModel.delivery = "1"
        viewModel.saveData(this.dataModel, object : InventorySelection {
            override fun onSelectedItem(name: String?, model: InventoryTypeSelectionModel?) {
                (activity as DashboardActivity).navController.previousBackStackEntry!!
                    .savedStateHandle.set<String>("id", dataModel.surveySequenceId)
                findNavController().navigateUp()
            }

            override fun onSelectedItem(
                name: String?,
                model: InventoryTypeSelectionModel?,
                position: Int?,
                isCallSaveAPI: Boolean,
                isChildItem: Boolean,
                childDescription: String
            ) {
                TODO("Not yet implemented")
            }


            override fun onLongClickDeSelectedItem(name: String?, model: InventoryTypeSelectionModel?, position: Int?) {
               // TODO("Not yet implemented")
            }

            override fun onCountIncreaseSelectedItem(
                model: InventoryTypeSelectionModel?,
                position: Int?
            ) {

            }

            override fun onAddInventoryAddSubItem(
                name: String?,
                model: InventoryTypeSelectionModel?,
                position: Int?,
                description: String?,
                count: String?
            ) {
                TODO("Not yet implemented")
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