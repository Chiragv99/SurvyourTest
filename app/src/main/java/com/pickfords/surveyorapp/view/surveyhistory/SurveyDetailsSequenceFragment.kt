package com.pickfords.surveyorapp.view.surveyhistory

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.pickfords.surveyorapp.DashboardActivity
import com.pickfords.surveyorapp.R
import com.pickfords.surveyorapp.databinding.FragmentSurveyDetailsSequenceBinding
import com.pickfords.surveyorapp.interfaces.EditLegsInterface
import com.pickfords.surveyorapp.interfaces.FragmentLifecycleInterface
import com.pickfords.surveyorapp.model.dashboard.DashboardModel
import com.pickfords.surveyorapp.model.surveyDetails.SaveSequenceModel
import com.pickfords.surveyorapp.model.surveyDetails.SequenceTypeModel
import com.pickfords.surveyorapp.model.surveyDetails.ShowLegsModel
import com.pickfords.surveyorapp.utils.*
import com.pickfords.surveyorapp.view.BaseActivity
import com.pickfords.surveyorapp.view.BaseFragment
import com.pickfords.surveyorapp.view.dialogs.ItemDeSelectDialog
import com.pickfords.surveyorapp.view.dialogs.MessageDialog
import com.pickfords.surveyorapp.view.surveyDetails.FragmentEnquiry
import com.pickfords.surveyorapp.viewModel.surveyDetails.SurveyDetailsSequenceViewModel
import kotlinx.android.synthetic.main.fragment_survey_details_sequence.*

class SurveyDetailsSequenceFragment : BaseFragment(), FragmentLifecycleInterface {

    lateinit var surveyDetailsSequenceBinding: FragmentSurveyDetailsSequenceBinding
    private val viewModel by lazy { SurveyDetailsSequenceViewModel(requireActivity()) }
    private var selectedData: DashboardModel? = null
    private var isFirstTime: Boolean = true
    private var selectedPosition: Int = 0

    companion object {
        @SuppressLint("StaticFieldLeak")
        var sequenceBinding: FragmentSurveyDetailsSequenceBinding? = null
        fun newInstance(selectedData: DashboardModel?): SurveyDetailsSequenceFragment {
            val bundle = Bundle()
            bundle.putSerializable(Session.DATA, selectedData)
            val fragmentSequenceDetails = SurveyDetailsSequenceFragment()
            fragmentSequenceDetails.arguments = bundle
            return fragmentSequenceDetails
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        if (!this::surveyDetailsSequenceBinding.isInitialized) {
            surveyDetailsSequenceBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_survey_details_sequence,
                container,
                false
            )
        }
        sequenceBinding = surveyDetailsSequenceBinding

        viewModel.selectedPositionSequence.observe(requireActivity()) {
            selectedPosition = it
            surveyDetailsSequenceBinding.spinnerSequence.setSelection(selectedPosition)
            if (it == 0) {
                surveyDetailsSequenceBinding.edLableTouse.setText(
                    Utility.getNextSequenceNumber(
                        viewModel.getNextSequenceName()
                    )
                )
            } else {
                setSequence(it)
            }
            isFirstTime = false
        }

        setSpinnerTitle()

        return surveyDetailsSequenceBinding.root
    }

    private fun setSpinnerTitle() {
        surveyDetailsSequenceBinding.spinnerSequence.setTitle("")
        surveyDetailsSequenceBinding.spinnerSeqType.setTitle("")
        surveyDetailsSequenceBinding.spinnerPackingMethod.setTitle("")
        surveyDetailsSequenceBinding.spinnerShipmentmethod.setTitle("")
        surveyDetailsSequenceBinding.spinnerallowanceType.setTitle("")
        surveyDetailsSequenceBinding.spinnerFromAddress1.setTitle("Select From Address")
        surveyDetailsSequenceBinding.spinnerToAddress2.setTitle("Select To Address")
    }

    // For Select Sequence
    private fun setSequence(pos: Int) {
        try {
            if (viewModel.sequenceList != null && viewModel.sequenceList.size > 0) {
                surveyDetailsSequenceBinding.edLableTouse.setText(viewModel.sequenceList[pos].toString())
                isFirstTime = false
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        mActivity = context as Activity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val session = Session(requireContext())
        setObserver(savedInstanceState)
        setAction()
    }

    // For All List Observer
    private fun setObserver(savedInstanceState: Bundle?) {
        viewModel.getSequenceLiveList().observe(requireActivity()) { list ->
            if (!list.isNullOrEmpty()) {
                if (selectedPosition != 0 && selectedPosition <= viewModel.getSequenceLiveList().value?.size!!) {
                    surveyDetailsSequenceBinding.edInsuranceamount.setText(list[selectedPosition - 1].insuranceAmount)
                    surveyDetailsSequenceBinding.edInputAllowance.setText(list[selectedPosition - 1].allowance)
                    surveyDetailsSequenceBinding.edLableTouse.setText(list[selectedPosition - 1].labelToUse)
                    surveyDetailsSequenceBinding.edaddDistanceOrigin.setText(list[selectedPosition - 1].distance)
                } else {
                    if (selectedPosition <= viewModel.getSequenceLiveList().value?.size!!) {
                        surveyDetailsSequenceBinding.edInsuranceamount.setText(list[selectedPosition].insuranceAmount)
                        surveyDetailsSequenceBinding.edInputAllowance.setText(list[selectedPosition].allowance)
                        surveyDetailsSequenceBinding.edLableTouse.setText(list[selectedPosition].labelToUse)
                        surveyDetailsSequenceBinding.edaddDistanceOrigin.setText(list[selectedPosition].distance)
                    }
                }
            }
        }


        viewModel.selectedSequenceTypeList.observe(requireActivity()) { sequence ->
            if (sequence != null) {
                surveyDetailsSequenceBinding.itemData = sequence
                val sequenceOld = viewModel.getSelectedSequenceModel().value
                if (sequenceOld?.sequenceTypeId != sequence.sequenceTypeId) {
                    selectPackingMethod(sequence.packingMethodId)
                    selectShipmentMethod(sequence.shipmentMethodId)
                } else {
                    selectPackingMethod(sequenceOld?.packingMethodId)
                    selectShipmentMethod(sequenceOld?.shipmentMethodId)
                }
            } else {
                surveyDetailsSequenceBinding.itemData = SequenceTypeModel()
            }
        }

        viewModel.getSelectedSequenceModel().observe(requireActivity()) { sequence ->
            if (sequence != null) {
                surveyDetailsSequenceBinding.data = sequence

                if (!TextUtils.isEmpty(sequence.distance)) {
                    val indexDistanceUnitOrigin = viewModel.getSequenceLegDestinationDistanceTypeLiveList().value?.indexOfFirst { it.distanceUnitId == sequence.distanceUnitId.toInt() }
                    if (indexDistanceUnitOrigin != null) {
                        surveyDetailsSequenceBinding.spinnerDistanceUnitOrigin.setSelection(indexDistanceUnitOrigin)
                    }
                }

                setData(sequence)
            } else {
                surveyDetailsSequenceBinding.data = SaveSequenceModel()
                if (viewModel.selectedPosition.toString() == "0") {
                    surveyDetailsSequenceBinding.spinnerSequence.setSelection(0)
                    surveyDetailsSequenceBinding.spinnerSeqType.setSelection(0)
                    surveyDetailsSequenceBinding.spinnerMode.setSelection(0)
                    surveyDetailsSequenceBinding.spinnerSeqGroup.setSelection(0)
                    surveyDetailsSequenceBinding.spinnerPackingMethod.setSelection(0)
                    surveyDetailsSequenceBinding.spinnerShipmentmethod.setSelection(0)
                }
            }
            resetLegsList()
        }

        viewModel.getSelectedSequenceLegModel().observe(requireActivity()) { showLegsModel ->
            if (showLegsModel != null) {
                val dataLeg = viewModel.getAddressList()
                if (dataLeg != null)
                    for (i in dataLeg.indices) {
                        if (dataLeg[i]?.contains(showLegsModel.originAddress.toString() + " - ") == true) {
                            surveyDetailsSequenceBinding.spinnerFromAddressLeg.setSelection(i)
                        }
                        if (dataLeg[i]?.contains(showLegsModel.destinationAddress.toString() + " - ") == true) {
                            surveyDetailsSequenceBinding.spinnerToAddressLeg.setSelection(i)
                        }
                    }
            }
        }
    }

    // For Set All Clicks
    private fun setAction() {
        surveyDetailsSequenceBinding.edInputPacking.setOnClickListener {
            DateAndTimeUtils.setDateWithformat(
                requireContext(),
                surveyDetailsSequenceBinding.edInputPacking,
                "yyyy-MM-dd"
            )
        }

        surveyDetailsSequenceBinding.edInputDelivery.setOnClickListener {
            DateAndTimeUtils.setDateWithformat(
                requireContext(),
                surveyDetailsSequenceBinding.edInputDelivery,
                "yyyy-MM-dd"
            )
        }
        viewModel.isLoading.observe(requireActivity()) { isLoading ->
            if (isLoading && isAdded) showProgressbar()
            else if (!isLoading && isAdded) hideProgressbar()
        }
        surveyDetailsSequenceBinding.btnAddSequence.setOnClickListener {
            if (spinnerSequence.selectedItemPosition > 0) {
                surveyDetailsSequenceBinding.data = SaveSequenceModel()
                // surveyDetailsSequenceBinding.spinnerSequence.setSelection(0)
            } else {
                Toast.makeText(
                    context,
                    getString(R.string.please_select_sequence),
                    Toast.LENGTH_SHORT
                ).show()
            }
            surveyDetailsSequenceBinding.spinnerSequence.post {
                surveyDetailsSequenceBinding.spinnerSequence.setSelection(0)
            }
            viewModel.flagSelection = true


        }

        btnAddSequenceLeg.setOnClickListener {

            if (selectedPosition == 0) {
                Toast.makeText(context, "Please Select Sequence", Toast.LENGTH_SHORT).show()
            } else if (viewModel.getSequenceLiveList().value?.get(
                    surveyDetailsSequenceBinding.spinnerSequence.selectedItemPosition - 1
                )?.isRevisit == true
            ) {
                MessageDialog(mContext, mContext.getString(R.string.revisit_error)).setListener(
                    object : MessageDialog.OkButtonListener{
                        override fun onOkPressed(dialog: MessageDialog) {
                            dialog.dismiss()
                            resetValue()
                            viewModel.setSurveyId(AppConstants.surveyID, 0, isFirstTime, false)
                            requireFragmentManager().beginTransaction().detach(SurveyDetailsSequenceFragment()).attach(SurveyDetailsSequenceFragment()).commit();
                        }
                    }
                ).show()
            } else {
                if (surveyDetailsSequenceBinding.btnAddSequenceLeg.text.toString() == getString(R.string.add)) {
                    surveyDetailsSequenceBinding.btnAddSequenceLeg.text = getString(R.string.cancel)

                    surveyDetailsSequenceBinding.addlegsLayout.isEnabled = true
                    surveyDetailsSequenceBinding.addlegsLayout.visibility = View.VISIBLE

                    if (viewModel.sequenceLegLiveList.value != null && viewModel.sequenceLegLiveList.value?.size!! > 0) {
                        val maxObject: ShowLegsModel =
                            viewModel.sequenceLegLiveList.value!!.maxBy { it.surveySequenceLeg?.toInt()!! }
                        Log.d("maxObject", maxObject.surveySequenceLeg.toString())
                        if (maxObject.surveySequenceLeg.toString().isNotEmpty()) {
                            surveyDetailsSequenceBinding.edEnterLegs.setText((maxObject.surveySequenceLeg!!.toInt() + 1).toString())
                        }
                        /* surveyDetailsSequenceBinding.edEnterLegs.setText(
                             ((viewModel.sequenceLegLiveList.value?.get(viewModel.sequenceLegLiveList.value?.size!! - 1)?.surveySequenceLeg)?.toInt()!! + 1).toString()
                         )*/
                    } else {
                        surveyDetailsSequenceBinding.edEnterLegs.setText("2")
                    }

                    surveyDetailsSequenceBinding.edEnterLegs.visibility = View.VISIBLE

                    surveyDetailsSequenceBinding.spinnerFromAddressLeg.isEnabled = true
                    surveyDetailsSequenceBinding.spinnerToAddressLeg.isEnabled = true

                    surveyDetailsSequenceBinding.btnSaveLegs.visibility = View.VISIBLE
                } else {
                    resetLegsList()
                }
            }

        }

        btnRemoveSequenceLeg.setOnClickListener {

            try {
                var legdetail = ""
                legdetail = (context as BaseActivity).sequenceDetailsDao.getLegUseInInventory(
                    selectedData?.surveyId.toString(),
                    viewModel.getSequenceLiveList().value?.get(surveyDetailsSequenceBinding.spinnerSequence.selectedItemPosition - 1)?.surveySequenceId.toString(),
                    viewModel.getLegList().value?.get(
                        spinnerSequenceLeg.selectedItemPosition
                    )?.surveySequenceLeg.toString()
                )


                if (viewModel.getSequenceLiveList().value?.get(
                        surveyDetailsSequenceBinding.spinnerSequence.selectedItemPosition - 1
                    )?.isRevisit == true
                ) {
                    MessageDialog(mContext, mContext.getString(R.string.revisit_error)).show()
                } else if (!legdetail.isNullOrEmpty()) {
                    MessageDialog(
                        mContext,
                        "There are inventory item(s) linked to this leg. Please remove them first."
                    ).show()
                } else {
                    if (surveyDetailsSequenceBinding.spinnerSequenceLeg.selectedItemPosition >= 0) {
                        val surveySequenceLegId: String =
                            viewModel.getLegList().value?.get(spinnerSequenceLeg.selectedItemPosition)?.surveySequenceLegId.toString()
                        viewModel.deleteSequenceLegById(
                            surveySequenceLegId,
                            viewModel.getLegList().value?.get(spinnerSequenceLeg.selectedItemPosition)?.surveySequenceId.toString(),
                            viewModel.getLegList().value?.get(spinnerSequenceLeg.selectedItemPosition)?.surveySequence.toString()
                        )
                        resetLegsList()
                    }
                }
            }catch (e : Exception){
            }
        }

        btnRemoveSequence.setOnClickListener {
            var legdetail = ""
            if (surveyDetailsSequenceBinding.spinnerSequence.selectedItemPosition > 0)
                legdetail = (context as BaseActivity).sequenceDetailsDao.getSequenceUseInInventory(
                    selectedData?.surveyId.toString(),
                    viewModel.getSequenceLiveList().value?.get(surveyDetailsSequenceBinding.spinnerSequence.selectedItemPosition - 1)?.surveySequenceId.toString()
                )

            if (surveyDetailsSequenceBinding.spinnerSequence.selectedItemPosition <= 0) {
                MessageDialog(requireContext(), context?.getString(R.string.please_select_sequence))
                    .setListener(object : MessageDialog.OkButtonListener {
                        override fun onOkPressed(dialog: MessageDialog) {
                            dialog.dismiss()
                        }

                    })
                    .setCancelButton(false)
                    .show()
            } else if (viewModel.getSequenceLiveList().value?.get(surveyDetailsSequenceBinding.spinnerSequence.selectedItemPosition - 1)?.isRevisit == true) {
                MessageDialog(mContext, mContext.getString(R.string.revisit_error)).show()
            } else if (!legdetail.isNullOrEmpty()) {
                MessageDialog(
                    mContext,
                    "There are inventory item(s) linked to this sequence. Please remove them first."
                ).show()
            } else {
                MessageDialog(
                    requireContext(),
                    requireActivity().getString(R.string.are_you_sure_delete_sequence)
                )
                    .setListener(object : MessageDialog.OkButtonListener {
                        override fun onOkPressed(dialog: MessageDialog) {
                            dialog.dismiss()
                            deleteSequence()
                        }
                    })
                    .setCancelButton(true)
                    .show()
            }

        }

        btnSaveLegs.setOnClickListener {

             if (viewModel.getSequenceLiveList().value?.get(surveyDetailsSequenceBinding.spinnerSequence.selectedItemPosition - 1)?.isRevisit == true) {
                 MessageDialog(mContext, mContext.getString(R.string.revisit_error)).show()
             }else{

                 if (surveyDetailsSequenceBinding.addlegsLayout.isGone && viewModel.sequenceLegLiveList.value.isNullOrEmpty()) {
                     Toast.makeText(requireContext(),"Please Add or Select Leg first",Toast.LENGTH_SHORT).show()
                     return@setOnClickListener
                 }

                 if (checkValidation()) {
                     val modelData = if (surveyDetailsSequenceBinding.addlegsLayout.isVisible) {
                         ShowLegsModel()
                     } else {
                         viewModel.getSelectedSequenceLegModel().value!!
                     }

                     if (surveyDetailsSequenceBinding.addlegsLayout.isVisible) {
                         modelData.surveySequenceLeg =
                             surveyDetailsSequenceBinding.edEnterLegs.text.toString()
                     }

                     if (viewModel.getSequenceLegOriginAddress() != null &&
                         (viewModel.getSequenceLegOriginAddress().value?.size ?: 0) > 0
                     ) {
                         modelData.originAddressId =
                             surveyDetailsSequenceBinding.spinnerFromAddressLeg.selectedItemPosition.let { it1 ->
                                 viewModel.getSequenceLegOriginAddress().value?.get(
                                     it1
                                 )?.surveyAddressId.toString()
                             }
                         modelData.originAddress =
                             surveyDetailsSequenceBinding.spinnerFromAddressLeg.selectedItemPosition.let { it1 ->
                                 viewModel.getSequenceLegOriginAddress().value?.get(
                                     it1
                                 )?.titleName.toString()
                             }
                     }

                     if (viewModel.getSequenceLegOriginAddress() != null &&
                         (viewModel.getSequenceLegOriginAddress().value?.size ?: 0) > 0
                     ) {
                         modelData.destinationAddressId =
                             surveyDetailsSequenceBinding.spinnerToAddressLeg.selectedItemPosition.let { it1 ->
                                 viewModel.getSequenceLegOriginAddress().value?.get(
                                     it1
                                 )?.surveyAddressId.toString()
                             }
                         modelData.destinationAddress =
                             surveyDetailsSequenceBinding.spinnerToAddressLeg.selectedItemPosition.let { it1 ->
                                 viewModel.getSequenceLegOriginAddress().value?.get(
                                     it1
                                 )?.titleName.toString()
                             }
                     }

                     if (surveyDetailsSequenceBinding.spinnerSequence.selectedItemPosition > 0) {
                         modelData.surveySequenceId =
                             viewModel.getSequenceLiveList().value?.get(surveyDetailsSequenceBinding.spinnerSequence.selectedItemPosition - 1)?.surveySequenceId.toString()
                         modelData.surveySequence =
                             viewModel.getSequenceLiveList().value?.get(surveyDetailsSequenceBinding.spinnerSequence.selectedItemPosition - 1)?.surveySequence.toString()
                     }
                     modelData.createdBy = session.user!!.userId

                     viewModel.saveLegData(modelData)
                     resetLegsList()
             }
            }
        }

        btnCopySequence.setOnClickListener()
        {
            if (selectedPosition == 0) {
                Toast.makeText(context, "Please Select Sequence", Toast.LENGTH_SHORT).show()
            } /*else if (viewModel.getSequenceLiveList().value?.get(surveyDetailsSequenceBinding.spinnerSequence.selectedItemPosition - 1)?.isRevisit == true) {
                 MessageDialog(mContext, mContext.getString(R.string.revisit_error)).show()
            }*/ else {
                if (viewModel.getSequenceLiveList().value?.get(surveyDetailsSequenceBinding.spinnerSequence.selectedItemPosition - 1)?.isRevisit == true) {
                    MessageDialog(mContext, mContext.getString(R.string.revisit_error)).show()
                }else{
                    if (validate()) {
                        var name = session.getDataByKey(Session.KEY_USER_NAME, "")
                        if (!Utility.isNetworkConnected(requireContext())) {
                            surveyDetailsSequenceBinding.edLableTouse.setText(
                                Utility.getNextSequenceNumber(
                                    viewModel.getNextSequenceName()
                                )
                            )
                        } else {
                            surveyDetailsSequenceBinding.edLableTouse.setText(Utility.getNextSequenceNumber(viewModel.getNextSequenceName()))
                        }
                        if (viewModel.getSequenceLiveList().value?.get(surveyDetailsSequenceBinding.spinnerSequence.selectedItemPosition - 1)?.surveySequenceId.toString() != null) {

                            var surveySequenceId = viewModel.getSequenceLiveList().value?.get(surveyDetailsSequenceBinding.spinnerSequence.selectedItemPosition - 1)?.surveySequenceId.toString()

                            if ((context as BaseActivity).showLegsDao.getShowLegsList() != null && (context as BaseActivity).showLegsDao.getShowLegsBySurveySequenceId(surveySequenceId).isNotEmpty()) {
                                ItemDeSelectDialog(mContext,resources.getString(R.string.legcopyalert))
                                    .setListener(object : ItemDeSelectDialog.OkButtonListener {
                                        override fun onOkPressed(
                                            itemDeSelectDialog: ItemDeSelectDialog, comment: String?) {
                                            itemDeSelectDialog.dismiss()
                                            copySequence(true)
                                        }

                                        override fun onCancelPressed(dialogDamage: ItemDeSelectDialog, comment: String?) {
                                            copySequence(false)
                                        }
                                    })
                                    .show()
                            }
                             else{
                                copySequence(true)
                            }
                        } else {
                            viewModel.setCopySurveySequence(
                                session.user?.userId,
                                surveyDetailsSequenceBinding.data?.surveySequenceId!!,true)
                            try {
                                surveyDetailsSequenceBinding.spinnerSequence.setSelection(
                                    selectedPosition
                                )
                            } catch (e: Exception) {
                                Log.e("error",e.message.toString())
                            }
                        }
                        //Generate new sequence name and add it here -Viraj
                        surveyDetailsSequenceBinding.edLableTouse.setText(
                            Utility.getNextSequenceNumber(
                                viewModel.getNextSequenceName()
                            )
                        )


                    }
                }

            }

        }

        btnSaveSequence.setOnClickListener()
        {
            if (selectedPosition != 0 && viewModel.getSequenceLiveList().value?.get(
                    surveyDetailsSequenceBinding.spinnerSequence.selectedItemPosition - 1
                )?.isRevisit == true
            ) {


                MessageDialog(mContext, mContext.getString(R.string.revisit_error)).setListener(
                    object : MessageDialog.OkButtonListener{
                        override fun onOkPressed(dialog: MessageDialog) {
                            try {
                                dialog.dismiss()
                                resetValue()
                                viewModel.setSurveyId(AppConstants.surveyID, 0, isFirstTime, false)
                            }catch (e: Exception){
                            }
                        }
                    }
                ).show()

            } else if (validate()) {
                val modelData = surveyDetailsSequenceBinding.data
                modelData?.sequenceTypeId =
                    viewModel.getSequenceTypeLiveList().value?.get(surveyDetailsSequenceBinding.spinnerSeqType.selectedItemPosition - 1)?.sequenceTypeId

                modelData?.sequenceModeId = viewModel.getSequenceTypeLiveList().value?.get(surveyDetailsSequenceBinding.spinnerSeqType.selectedItemPosition - 1)?.sequenceModeId
                modelData?.sequenceGroupId = viewModel.getSequenceTypeLiveList().value?.get(surveyDetailsSequenceBinding.spinnerSeqType.selectedItemPosition - 1)?.sequenceGroupId
                if (viewModel.getSequenceLegOriginAddress() != null &&
                    (viewModel.getSequenceLegOriginAddress().value?.size ?: 0) > 0
                ) {
                    modelData?.originAddress = viewModel.getSequenceLegOriginAddress().value?.get(
                        surveyDetailsSequenceBinding.spinnerFromAddress1.selectedItemPosition
                    )?.titleName.toString()

                    modelData?.originAddressId =
                        surveyDetailsSequenceBinding.spinnerFromAddress1.selectedItemPosition.let { it1 ->
                            viewModel.getSequenceLegOriginAddress().value?.get(
                                it1
                            )?.surveyAddressId.toString()
                        }
                }

                if (viewModel.getSequenceLegOriginAddress() != null &&
                    (viewModel.getSequenceLegOriginAddress().value?.size ?: 0) > 0
                ) {
                    modelData?.destinationAddress =
                        surveyDetailsSequenceBinding.spinnerToAddress2.selectedItemPosition.let { it1 ->
                            viewModel.getSequenceLegOriginAddress().value?.get(
                                it1
                            )?.titleName.toString()
                        }
                    modelData?.destinationAddressId =
                        surveyDetailsSequenceBinding.spinnerToAddress2.selectedItemPosition.let { it1 ->
                            viewModel.getSequenceLegOriginAddress().value?.get(
                                it1
                            )?.surveyAddressId.toString()
                        }
                }
                if (surveyDetailsSequenceBinding.edInsuranceamount.text.toString().isEmpty())
                    modelData?.insuranceAmount = "0"
                /*if (surveyDetailsSequenceBinding.spinnerMode.isVisible)
                    modelData?.sequenceModeId =
                        viewModel.getSequenceModeLiveList().value?.get(surveyDetailsSequenceBinding.spinnerMode.selectedItemPosition - 1)?.sequenceModeId
                else
                    modelData?.sequenceModeId =
                        surveyDetailsSequenceBinding.itemData?.sequenceModeId

                if (surveyDetailsSequenceBinding.spinnerSeqGroup.isVisible)
                    modelData?.sequenceGroupId =
                        viewModel.getSequenceGroupLiveList().value?.get(surveyDetailsSequenceBinding.spinnerSeqGroup.selectedItemPosition - 1)?.sequenceGroupId
                else
                    modelData?.sequenceGroupId =
                        surveyDetailsSequenceBinding.itemData?.sequenceGroupId
*/

                if (surveyDetailsSequenceBinding.spinnerPackingMethod.isVisible)
                    modelData?.packingMethodId = viewModel.getShippingMethodLiveList().value?.get(surveyDetailsSequenceBinding.spinnerPackingMethod.selectedItemPosition - 1)?.shipmentMethodId
                else
                    modelData?.packingMethodId =
                        surveyDetailsSequenceBinding.itemData?.packingMethodId

                modelData?.surveyId = selectedData?.surveyId

                if (surveyDetailsSequenceBinding.spinnerShipmentmethod.isVisible)
                    modelData?.shipmentMethodId =
                        viewModel.getShippingMethodLiveList().value?.get(
                            surveyDetailsSequenceBinding.spinnerShipmentmethod.selectedItemPosition - 1
                        )?.shipmentMethodId
                else
                    modelData?.shipmentMethodId =
                        surveyDetailsSequenceBinding.itemData?.shipmentMethodId

                if (surveyDetailsSequenceBinding.spinnerSequence.selectedItemPosition > 0)
                    modelData?.surveySequenceId =
                        viewModel.getSequenceLiveList().value?.get(surveyDetailsSequenceBinding.spinnerSequence.selectedItemPosition - 1)?.surveySequenceId.toString()

                modelData?.createdBy = session.user!!.userId


                if (viewModel.getAllowanceTypeLiveList() != null &&
                    (viewModel.getAllowanceTypeLiveList().value?.size ?: 0) > 0
                ) {
                    modelData?.allowanceTypeId =
                        surveyDetailsSequenceBinding.spinnerallowanceType.selectedItemPosition.let { it1 ->
                            viewModel.getAllowanceTypeLiveList().value?.get(
                                it1
                            )?.allowanceTypeId.toString()
                        }
                }

                if (viewModel.getSequenceLegDestinationDistanceTypeLiveList() != null && viewModel.getSequenceLegDestinationDistanceTypeLiveList().value?.size ?: 0 > 0) {
                    modelData?.distanceUnitId =
                        viewModel.getSequenceLegDestinationDistanceTypeLiveList().value?.get(
                            surveyDetailsSequenceBinding.spinnerDistanceUnitOrigin.selectedItemPosition
                        )?.distanceUnitId.toString()
                }


                try {
                    modelData?.labelToUse =
                        surveyDetailsSequenceBinding.edLableTouse.text.toString()
                    viewModel.saveData(modelData)
                    surveyDetailsSequenceBinding.spinnerSequence.setSelection(selectedPosition)
                } catch (e: Exception) {
                }
            }
        }
    }

    private fun copySequence(copyLeg: Boolean) {
        viewModel.setCopySurveySequence(session.user?.userId, viewModel.getSequenceLiveList().value?.get(surveyDetailsSequenceBinding.spinnerSequence.selectedItemPosition - 1)?.surveySequenceId.toString(),copyLeg)
        try {
            surveyDetailsSequenceBinding.spinnerSequence.setSelection(selectedPosition)
        } catch (e: Exception) {
            Log.e("error",e.message.toString())
        }
    }

    // For Reset Leg List
    private fun resetLegsList() {
        surveyDetailsSequenceBinding.btnAddSequenceLeg.text = getString(R.string.add)

        surveyDetailsSequenceBinding.addlegsLayout.isEnabled = false
        surveyDetailsSequenceBinding.addlegsLayout.visibility = View.GONE
        surveyDetailsSequenceBinding.edEnterLegs.setText("")
        surveyDetailsSequenceBinding.edEnterLegs.visibility = View.GONE

        surveyDetailsSequenceBinding.spinnerFromAddressLeg.isEnabled = true
        surveyDetailsSequenceBinding.spinnerToAddressLeg.isEnabled = true

        surveyDetailsSequenceBinding.btnSaveLegs.visibility = View.VISIBLE


        val dataLeg = viewModel.getAddressList()
        if (dataLeg != null){
            surveyDetailsSequenceBinding.spinnerToAddressLeg.setSelection(1)
        }

    }

    private fun checkValidation(): Boolean {
        if (surveyDetailsSequenceBinding.addlegsLayout.isVisible) {
            if (TextUtils.isEmpty(surveyDetailsSequenceBinding.edEnterLegs.text.toString())) {
                Toast.makeText(
                    context,
                    getString(R.string.please_enter_leg_name),
                    Toast.LENGTH_SHORT
                )
                    .show()
                return false
            }
        }
        return true
    }

    // For Delete Sequence
    private fun deleteSequence() {
        val modelData: String =
            viewModel.getSequenceLiveList().value?.get(surveyDetailsSequenceBinding.spinnerSequence.selectedItemPosition - 1)?.surveySequenceId.toString()
        viewModel.deleteSurveySequence(modelData)
    }

    private var onLegsEdit = object : EditLegsInterface {
        override fun onEditLegs(position: Int, dataModel: ShowLegsModel) {
            //todo change code here
            val bundle = Bundle()
            bundle.putSerializable("showLegsModel", dataModel)
            bundle.putBoolean("isUpdateLeg", true)
            bundle.putString("leg", dataModel.surveySequenceLeg)
            bundle.putString("surveyid", selectedData?.surveyId)
            bundle.putString(
                "surveySequenceId", viewModel.getSequenceLiveList().value?.get(
                    surveyDetailsSequenceBinding.spinnerSequence.selectedItemPosition - 1
                )?.surveySequenceId.toString()
            )
            (activity as DashboardActivity).navController.navigate(R.id.editLegsFragment, bundle)
        }

    }

    private fun validate(): Boolean {
        when {

            surveyDetailsSequenceBinding.edLableTouse.text.toString().isEmpty() -> {
                Toast.makeText(
                    context,
                    getString(R.string.please_select_sequence_type),
                    Toast.LENGTH_SHORT
                ).show()
                return false
            }


            surveyDetailsSequenceBinding.spinnerSeqType.selectedItemPosition <= 0 -> {
                Toast.makeText(
                    context,
                    getString(R.string.please_select_sequence_type),
                    Toast.LENGTH_SHORT
                ).show()
                return false
            }
            surveyDetailsSequenceBinding.spinnerMode.isVisible && surveyDetailsSequenceBinding.spinnerMode.selectedItemPosition <= 0 -> {
                Toast.makeText(
                    context,
                    getString(R.string.please_select_sequence_mode),
                    Toast.LENGTH_SHORT
                ).show()
                return false
            }
            surveyDetailsSequenceBinding.spinnerSeqGroup.isVisible && surveyDetailsSequenceBinding.spinnerSeqGroup.selectedItemPosition <= 0 -> {
                Toast.makeText(
                    context,
                    getString(R.string.please_select_sequence_group),
                    Toast.LENGTH_SHORT
                ).show()
                return false
            }
            surveyDetailsSequenceBinding.spinnerPackingMethod.isVisible && surveyDetailsSequenceBinding.spinnerPackingMethod.selectedItemPosition <= 0 -> {
                Toast.makeText(
                    context,
                    getString(R.string.please_select_packing_method),
                    Toast.LENGTH_SHORT
                ).show()
                return false
            }

            surveyDetailsSequenceBinding.spinnerShipmentmethod.isVisible && surveyDetailsSequenceBinding.spinnerShipmentmethod.selectedItemPosition <= 0 -> {
                Toast.makeText(
                    context,
                    getString(R.string.please_select_shipping_method),
                    Toast.LENGTH_SHORT
                ).show()
                return false


            }
            else -> return true
        }
    }

    private fun setData(sequence: SaveSequenceModel) {
        val data = viewModel.getSequenceTypeLiveList()
        if (data.value != null)
            for (i in data.value!!.indices) {
                if (sequence.sequenceTypeId == data.value!![i].sequenceTypeId) {
                    surveyDetailsSequenceBinding.spinnerSeqType.setSelection(i + 1)
                }
            }

        val data1 = viewModel.getSequenceModeLiveList()
        if (data1.value != null)
            for (i in data1.value!!.indices) {
                if (sequence.sequenceModeId == data1.value!![i].sequenceModeId) {
                    surveyDetailsSequenceBinding.spinnerMode.setSelection(i + 1)
                }
            }

        val data2 = viewModel.getSequenceGroupLiveList()
        if (data2.value != null)
            for (i in data2.value!!.indices) {
                if (sequence.sequenceGroupId == data2.value!![i].sequenceGroupId) {
                    surveyDetailsSequenceBinding.spinnerSeqGroup.setSelection(i + 1)
                }
            }

        viewModel.getPackingMethodLiveList().observe(requireActivity()){
            if (it !=null){
                selectPackingMethod(sequence.packingMethodId!!)
            }
        }

        viewModel.getShippingMethodLiveList().observe(requireActivity()){
            if (it != null){
                selectShipmentMethod(sequence.shipmentMethodId!!)
            }
        }
 
        val data6 = viewModel.getAllowanceTypeLiveList()
        if (data6.value != null)
            for (i in data6.value!!.indices) {
                if (sequence.allowanceTypeId?.toInt() == data6.value!![i].allowanceTypeId) {
                    surveyDetailsSequenceBinding.spinnerallowanceType.setSelection(i)
                }
            }

        val data7 = viewModel.getDeliveryTypeLiveList()
        if (data7.value != null)
            for (i in data7.value!!.indices) {
                if (sequence.deliveryTypeId?.toInt() == data7.value!![i].deliveryTypeId) {
                    surveyDetailsSequenceBinding.spinnerDeliveryType.setSelection(i)
                }
            }

        val data8 = viewModel.getAddressList()
        if (data8 != null)
            for (i in data8.indices) {
                if (data8[i]?.contains(sequence.originAddress.toString() + " - ") == true) {
                    surveyDetailsSequenceBinding.spinnerFromAddress1.setSelection(i)
                }
                if (data8[i]?.contains(sequence.destinationAddress.toString() + " - ") == true) {
                    surveyDetailsSequenceBinding.spinnerToAddress2.setSelection(i)
                }
            }
    }

    private fun selectPackingMethod(packingMethodId: Int?) {
        val indexPackingMethod = viewModel.getPackingMethodLiveList().value?.indexOfFirst { it.packingMethodId == packingMethodId}
        if (indexPackingMethod != null) {
            sequenceBinding?.spinnerPackingMethod?.setSelection((indexPackingMethod + 1))
            Log.e("SelectedPacking",indexPackingMethod.toString())
        }
    }

    private fun selectShipmentMethod(shipmentId: Int?) {
        val indexPackingMethod = viewModel.getShippingMethodLiveList().value?.indexOfFirst { it.shipmentMethodId == shipmentId}
        if (indexPackingMethod != null) {
            sequenceBinding?.spinnerShipmentmethod?.setSelection((indexPackingMethod + 1))
            Log.e("SelectedShippment",indexPackingMethod.toString())
        }
    }

    override fun onResume() {
        try {
            (activity as DashboardActivity).navController.currentBackStackEntry?.savedStateHandle?.getLiveData<String>("id")?.observe(this) {
                if (it != null) {
                    viewModel.setSurveySequenceID(it)
                }
            }
            super.onResume()
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    override fun onPauseFragment() {

    }

    override fun onResumeFragment(s: String?) {
        Log.e("OnResume", "OnResume")
        try {
            viewModel.setEditLegs(onLegsEdit)
            viewModel.init(mContext)
            surveyDetailsSequenceBinding.lifecycleOwner = this
            surveyDetailsSequenceBinding.viewModel = viewModel

            if (arguments != null && requireArguments().containsKey(Session.DATA) && requireArguments().get(
                    Session.DATA
                ) != null
            ) {
                selectedData = requireArguments().getSerializable(Session.DATA) as DashboardModel?
                surveyDetailsSequenceBinding.data = SaveSequenceModel()
            }

            viewModel.setAdd(AppConstants.surveyID, 0, isFirstTime, false)


            if (!isFirstTime) {
                if (!Utility.isNetworkConnected(context) && (context as BaseActivity).saveSequenceDao.getSequenceList() != null && (context as BaseActivity).saveSequenceDao.getSequenceListBySurveyId(selectedData?.surveyId.toString(),false).isNotEmpty()) {
                    Log.d("adds", Gson().toJson((context as BaseActivity).saveSequenceDao.getSequenceList()))
                    if ((context as BaseActivity).saveSequenceDao.getSequenceListBySurveyId(
                            selectedData?.surveyId.toString(),false
                        ).size > 1
                    )
                        if (selectedPosition == 0)
                            selectedPosition = 1


                }
                surveyDetailsSequenceBinding.spinnerSequence.post {
                    surveyDetailsSequenceBinding.spinnerSequence.setSelection(selectedPosition)
                }
                viewModel.setSurveyId(AppConstants.surveyID, 0, isFirstTime, false)
            } else {
                viewModel.setSurveyId(AppConstants.surveyID, 0, isFirstTime, false)
            }


            // For select sequence code
            if((context as DashboardActivity).setSelectedSequencePosition() > 0){
                var selectedPosition =  ((context as DashboardActivity).setSelectedSequencePosition())
                viewModel.selectedPosition.postValue(selectedPosition)
            }

        } catch (e: Exception) {
            Log.e("Exception", e.message.toString())
        }

    }

    private fun resetValue() {
        try {
            viewModel.setEditLegs(onLegsEdit)
            viewModel.init(mContext)
            surveyDetailsSequenceBinding.lifecycleOwner = this
            surveyDetailsSequenceBinding.viewModel = viewModel

            if (arguments != null && requireArguments().containsKey(Session.DATA) && requireArguments().get(
                    Session.DATA
                ) != null
            ) {
                selectedData = requireArguments().getSerializable(Session.DATA) as DashboardModel?
                surveyDetailsSequenceBinding.data = SaveSequenceModel()
            }

            viewModel.setAdd(AppConstants.surveyID, 0, isFirstTime, false)
        }catch (e: Exception){
        }

    }
}