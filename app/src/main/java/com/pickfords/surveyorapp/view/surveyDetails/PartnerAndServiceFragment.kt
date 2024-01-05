package com.pickfords.surveyorapp.view.surveyDetails


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.pickfords.surveyorapp.R
import com.pickfords.surveyorapp.databinding.FragmentPartnerAndServiceBinding
import com.pickfords.surveyorapp.interfaces.FragmentLifecycleInterface
import com.pickfords.surveyorapp.model.dashboard.DashboardModel
import com.pickfords.surveyorapp.utils.Session
import com.pickfords.surveyorapp.view.BaseFragment
import com.pickfords.surveyorapp.viewModel.surveyDetails.PartnerAndServiceViewModel

//todo Pranav Shah

class PartnerAndServiceFragment : BaseFragment(), FragmentLifecycleInterface {

    private var selectedData: DashboardModel? = null
    private lateinit var partnerAndServiceBinding: FragmentPartnerAndServiceBinding
    private val viewModel by lazy { PartnerAndServiceViewModel() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        partnerAndServiceBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_partner_and_service,
            container,
            false
        )

     //   viewModel.getPartnerList(requireContext())

        return partnerAndServiceBinding.root
    }


    companion object {
        fun newInstance(
            selectedData: DashboardModel?
        ): PartnerAndServiceFragment {
            val bundle = Bundle()
            bundle.putSerializable(Session.DATA, selectedData)
            val fragmentSequenceDetails = PartnerAndServiceFragment()
            fragmentSequenceDetails.arguments = bundle
            return fragmentSequenceDetails
        }
    }

    override fun onPauseFragment() {

    }

    override fun onResumeFragment(s: String?) {

        partnerAndServiceBinding.lifecycleOwner = this

        partnerAndServiceBinding.lifecycleOwner = this
        partnerAndServiceBinding.viewModel = viewModel

        if (arguments != null && requireArguments().containsKey(Session.DATA) && requireArguments().get(
                Session.DATA
            ) != null
        ) {
            selectedData = requireArguments().getSerializable(Session.DATA) as DashboardModel?
        }

        viewModel.init(mContext, selectedData)
        onClick()
    }

    private fun onClick() {
        partnerAndServiceBinding.imgAdd.setOnClickListener {
          //  viewModel.savePartnerApi(selectedData)
            viewModel.updateAdditionalAdapter(selectedData)
        }

        partnerAndServiceBinding.btnSave.setOnClickListener {
            viewModel.validate(context)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.isLoading.observe(requireActivity()) { isLoading ->
            if (isLoading && isAdded) showProgressbar()
            else if (!isLoading && isAdded) hideProgressbar()
        }
    }
    override fun onResume() {
        super.onResume()
   //     viewModel.getPartnerList(requireContext())
    }
}