package com.pickfords.surveyorapp.view.dashboard

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.pickfords.surveyorapp.DashboardActivity
import com.pickfords.surveyorapp.R
import com.pickfords.surveyorapp.databinding.FragmentDashboardBinding
import com.pickfords.surveyorapp.extentions.hideShowView
import com.pickfords.surveyorapp.view.BaseActivity
import com.pickfords.surveyorapp.view.BaseFragment
import com.pickfords.surveyorapp.view.dialogs.FilterDialog
import com.pickfords.surveyorapp.view.dialogs.SyncDataDialog
import com.pickfords.surveyorapp.viewModel.dashboard.DashboardViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class FragmentDashboard : BaseFragment(),
    CompoundButton.OnCheckedChangeListener {
    private val viewModel by lazy { DashboardViewModel() }
    private lateinit var dashboardBinding: FragmentDashboardBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        dashboardBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_dashboard,
            container,
            false
        )
        return dashboardBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbarWithMenu(requireActivity().getString(R.string.dashboard))

        if (savedInstanceState == null) viewModel.init(mContext)
        dashboardBinding.lifecycleOwner = this
        dashboardBinding.viewmodel = viewModel

        viewModel.isLoading.observe(requireActivity()) { isLoading ->
            if (isLoading && isAdded) showProgressbar()
            else if (!isLoading && isAdded) hideProgressbar()
        }

        // Hide and Show view for No Data Found
        viewModel.dashboardLiveList.observeForever {
            hideShowView(!(it.isNullOrEmpty()), dashboardBinding.rvDashboard)
            hideShowView(it.isNullOrEmpty(), dashboardBinding.txtDataNotFound)
            if (it.isNullOrEmpty()){
              Log.e("DashboardData","Null")
//                SyncDataDialog(mContext, "There are no surveys to sync. Please try again later").apply {
//                    setTitleData(context.getString(R.string.dialog_sync_data_error_title))
//                    setCancelButton(false)
//                    setListener(object: SyncDataDialog.OkButtonListener {
//                        override fun onOkPressed(dialog: SyncDataDialog) {
//                            dialog.dismiss()
//                        }
//                    })
//                }.also { it ->
//                    if (!it.isShowing){
//                        it.show()
//                    }
//                }
            }
        }

        // For All click
        setAction()

        (context as DashboardActivity).changeSelectedSequencePosition(0);

        viewModel.setSelectedPosition.observeForever {
           dashboardBinding.recFilter.scrollToPosition(it)
            dashboardBinding.recFilter.smoothScrollToPosition(it)
            dashboardBinding.recFilter.layoutManager?.scrollToPosition(it)
            (dashboardBinding.recFilter.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(it,20)
        }
    }


    private fun setAction() {
        dashboardBinding.imgFilter.setOnClickListener {
            FilterDialog(
                requireContext(),
            ).setListener(object : FilterDialog.OkButtonListener {
                override fun onOkPressed(dialog: FilterDialog, selectedFilter: String) {
                    dialog.dismiss()
                    viewModel.filterText.value = selectedFilter
                    viewModel.getSurveyDetailList(requireActivity(),false)
                }

            }).show()
        }

       // dashboardBinding.pullToRefresh.setOnRefreshListener(this)
        dashboardBinding.swcSubmit.setOnCheckedChangeListener(this)


    }

  public fun onSubmitButtonClick(surveyId: String, dashboardActivity: DashboardActivity) {
       Log.e("OnSubmit","OnSubmit" + surveyId)
       viewModel.saveSurveyDetailList(surveyId,dashboardActivity)
    }

    fun setDefaultValueDashboard(){
        viewModel.setDefaultVale()
    }

    public fun setSelectedPosition(){

    }

    override fun onResume() {
        super.onResume()
        super.onResume()
//        viewModel.callFilterSurveyDetailApi(requireActivity(), false)
//
//        // For Refresh Data
//        viewModel.refreshData.observeForever {
//            if (it !=null){
//                Log.e("RefreshData",it)
//            }
//        }
    }
    public fun  onRefreshData(context: Context) {
        viewModel.callFilterSurveyDetailApi(context,false)
        viewModel.getSurveyDetailList(context, false)
    }
    override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
        when (p0?.id) {
            R.id.swcSubmit -> {
                viewModel.isSubmitted.value = !p1
                viewModel.getSurveyDetailList(requireContext(), false)
            }

        }
    }
}