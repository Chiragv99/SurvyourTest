package com.pickfords.surveyorapp.view.planning

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.pickfords.surveyorapp.DashboardActivity
import com.pickfords.surveyorapp.R
import com.pickfords.surveyorapp.databinding.FragmentPlanningBinding
import com.pickfords.surveyorapp.interfaces.FragmentLifecycleInterface
import com.pickfords.surveyorapp.model.dashboard.DashboardModel
import com.pickfords.surveyorapp.model.surveyDetails.SurveyPlanningListModel
import com.pickfords.surveyorapp.utils.AppConstants
import com.pickfords.surveyorapp.utils.EditTextErrorResolver
import com.pickfords.surveyorapp.utils.Session
import com.pickfords.surveyorapp.utils.Utility
import com.pickfords.surveyorapp.view.BaseFragment
import com.pickfords.surveyorapp.view.dialogs.MessageDialog
import com.pickfords.surveyorapp.viewModel.surveyDetails.PlanningViewModel
import kotlinx.android.synthetic.main.fragment_planning.*


class FragmentPlanning : BaseFragment(), FragmentLifecycleInterface {
    lateinit var planningBinding: FragmentPlanningBinding
    private val viewModel by lazy { PlanningViewModel(requireActivity()) }
    private var selectedData: DashboardModel? = null
    private var isFirstTime: Boolean = true
    private var selectedPosition: Int = 0

    companion object {
        fun newInstance(selectedData: DashboardModel?): FragmentPlanning {
            val bundle = Bundle()
            bundle.putSerializable(Session.DATA, selectedData)
            val fragmentPlanningDetails = FragmentPlanning()
            fragmentPlanningDetails.arguments = bundle
            return fragmentPlanningDetails
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        planningBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_planning, container, false)
        setSpinnerTitle()
        return planningBinding.root
    }

    //  Set Tittle For Spinner
    private fun setSpinnerTitle() {
        planningBinding.spnSelectSurvey.setTitle("Select Sequence");
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        mActivity = context as Activity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver()
        setAction()
    }

    // For All list Observer
    private fun setObserver() {
        viewModel.isLoading.observe(requireActivity()) { isLoading ->
            if (isLoading && isAdded) showProgressbar()
            else if (!isLoading && isAdded) hideProgressbar()
        }
        viewModel.selectedPosition.observe(requireActivity()) {
            selectedPosition = it
            planningBinding.spnSelectSurvey.setSelection(selectedPosition)
            isFirstTime = false
        }
    }

    // Set All click here
    private fun setAction() {
        planningBinding.edCustomerName.addTextChangedListener(
            EditTextErrorResolver(
                planningBinding.edCustomerName,
                planningBinding.customerNameLayout
            )
        )
        planningBinding.edRefrenceNumber.addTextChangedListener(
            EditTextErrorResolver(
                planningBinding.edRefrenceNumber,
                planningBinding.refrenceNumberLayout
            )
        )
        btnSave.setOnClickListener() {
            if (viewModel.getSequenceDetailsLiveList().value!![selectedPosition].isRevisit) {
                MessageDialog(mContext, mContext.getString(R.string.revisit_error)).setListener(
                    object : MessageDialog.OkButtonListener{
                        override fun onOkPressed(dialog: MessageDialog) {
                            dialog.dismiss()
                            resetValue()
                        }
                    }
                ).show()
            } else {
                saveData()
            }
        }
    }

    private fun saveData() {

        var optioncount = 0

        var daycount = 0

        var allValidData = true

        val data = viewModel.getPlanningAdapter()?.getData()
        for (item in data?.indices!!) {
            data[item].optionId = item + 1
            optioncount += 1
            daycount = 0
            val isBreak = false

            for (dayModel in data[item].days!!) {
                daycount += 1
                val dayCount =
                    if (dayModel.optionDay == null || dayModel.optionDay == 0) daycount else dayModel.optionDay
                if (dayModel.optionDay == null || dayModel.optionDay == 0) {
                    MessageDialog(
                        requireContext(),
                        "Please enter Day Count in Option$optioncount Day $dayCount"
                    ).show()
                    allValidData = false
                    break
                } else if (dayModel.time!!.isEmpty()) {
                    MessageDialog(
                        requireContext(),
                        "Please enter the details in Time in Option$optioncount Day $dayCount"
                    ).show()
                    allValidData = false
                    break
                } else if (dayModel.address.toString().contains("Select")) {
                    //  if (Utility.isNetworkConnected(context)) {
                    MessageDialog(
                        requireContext(),
                        "Please select Address in Option$optioncount Day $dayCount"
                    ).show()
                    allValidData = false
                    break
                } else if (dayModel.activityId == 0) {
                    MessageDialog(
                        requireContext(),
                        "Please select Activity in Option$optioncount Day $dayCount"
                    ).show()
                    allValidData = false
                    break
                } else if (dayModel.code == null || dayModel.code!!.isEmpty()) {
                    MessageDialog(
                        requireContext(),
                        "Please select Code in Option$optioncount Day $dayCount"
                    ).show()
                    allValidData = false
                    break
                } else if (dayModel.notes!! == "0") {
                    MessageDialog(
                        requireContext(),
                        "Please enter the Notes in Option$optioncount Day $dayCount"
                    ).show()
                    allValidData = false
                    break
                } else if (dayModel.aMDriver!!.isEmpty()) {
                    MessageDialog(
                        requireContext(),
                        "Please enter the details in No. of Driver in Option$optioncount Day $dayCount"
                    ).show()
                    allValidData = false
                    break
                } else if (dayModel.aMPorter!!.isEmpty()) {
                    MessageDialog(
                        requireContext(),
                        "Please enter the details in No. of Porter in Option $optioncount Day $dayCount"
                    ).show()
                    allValidData = false
                    break
                } else {
                    Log.d("xxx", "am -> " + dayModel.am.toString())
                    Log.d("xxx", "amDriver -> " + dayModel.aMDriver.toString())
                    Log.d("xxx", "amPorter -> " + dayModel.aMPorter.toString())
                    Log.d("xxx", "pm -> " + dayModel.pm.toString())
                    Log.d("xxx", "pmDriver -> " + dayModel.pMDriver.toString())
                    Log.d("xxx", "pmPorter -> " + dayModel.pMPorter.toString())
                    Log.d("xxx", "address -> " + dayModel.addressId.toString())
                    Log.d("xxx", "activity -> " + dayModel.activityId.toString())
                    Log.d("xxx", "tempActivityId -> " + dayModel.tempAddressId.toString())
                }
            }
            if (isBreak) {
                break
            }
        }
        if (allValidData) {

            val modelData = SurveyPlanningListModel()
            if (planningBinding.edCustomerName.text!!.isNotEmpty()) {
                modelData.customerId =
                    planningBinding.edCustomerName.text.toString()
            } else
                modelData.customerId = "0"
            if (planningBinding.edRefrenceNumber.text!!.isNotEmpty()) {
                modelData.referenceId =
                    planningBinding.edRefrenceNumber.text.toString()
            } else {
                modelData.referenceId = "0"
            }
            modelData.surveyId = selectedData?.surveyId.toString()
            modelData.options = data
            modelData.sequenceId = viewModel.surveySequenceId
            // modelData.surveySequence = viewModel.surveySequenceName
            modelData.sequenceNo = viewModel.surveySequenceName
            modelData.createdBy = selectedData?.userId

            val json = Gson().toJson(modelData)
            Log.e("request data", json.toString())

            viewModel.saveSurveyPlanningDetails(modelData)
        }
    }

    override fun onPauseFragment() {

    }

    override fun onResumeFragment(s: String?) {
        viewModel.init(mContext)
        planningBinding.lifecycleOwner = this
        planningBinding.viewModel = viewModel

        if((context as DashboardActivity).setSelectedSequencePosition() > 0){
           var selectedPosition =  ((context as DashboardActivity).setSelectedSequencePosition() - 1)
            viewModel.selectedPosition.postValue(selectedPosition)
            Log.e("SelectedSequence",  selectedPosition.toString())
        }


        if (!isFirstTime) {
            viewModel.setSurveyId(AppConstants.surveyID, 0, true, false)
            planningBinding.spnSelectSurvey.post {
                planningBinding.spnSelectSurvey.setSelection(selectedPosition)
            }

        } else {
            viewModel.setSurveyId(AppConstants.surveyID, 0, isFirstTime, false)
        }

        if (arguments != null && requireArguments().containsKey(Session.DATA) && requireArguments().get(
                Session.DATA
            ) != null
        ) {
            selectedData = requireArguments().getSerializable(Session.DATA) as DashboardModel?
            viewModel.setData(selectedData)
            planningBinding.mainData = selectedData

            planningBinding.edCustomerName.setText(selectedData?.firstName + " " + selectedData?.surname)
        }

    }

    private fun resetValue() {
        try {
            viewModel.init(mContext)
            planningBinding.lifecycleOwner = this
            planningBinding.viewModel = viewModel
            if (arguments != null && requireArguments().containsKey(Session.DATA) && requireArguments().get(
                    Session.DATA) != null) {
                selectedData = requireArguments().getSerializable(Session.DATA) as DashboardModel?
            }

            if((context as DashboardActivity).setSelectedSequencePosition() > 0){
                var selectedPosition =  ((context as DashboardActivity).setSelectedSequencePosition() - 1)
                viewModel.selectedPosition.postValue(selectedPosition)
            }

        }catch (e: Exception){
        }
    }

}



