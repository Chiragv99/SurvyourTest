package com.pickfords.surveyorapp.view.surveyDetails

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.pickfords.surveyorapp.R
import com.pickfords.surveyorapp.databinding.FragmentSurveyDetailsBinding
import com.pickfords.surveyorapp.interfaces.FragmentLifecycleInterface
import com.pickfords.surveyorapp.interfaces.onChangePageToInventory
import com.pickfords.surveyorapp.model.dashboard.DashboardModel
import com.pickfords.surveyorapp.model.surveyDetails.SequenceDetailsModel
import com.pickfords.surveyorapp.utils.Session.Companion.DATA
import com.pickfords.surveyorapp.view.BaseFragment
import com.pickfords.surveyorapp.view.planning.FragmentPlanning
import com.pickfords.surveyorapp.view.surveyhistory.SurveyDetailsSequenceFragment


class FragmentSurveyDetails : BaseFragment() {

    lateinit var surveyDetailsBinding: FragmentSurveyDetailsBinding
    var selectedData: DashboardModel? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        if (!this::surveyDetailsBinding.isInitialized) { surveyDetailsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_survey_details, container, false)
            setView()
        }
        return surveyDetailsBinding.root
    }

    private fun setView() {
        setupToolbarWithMenu(requireActivity().getString(R.string.survey_details))
        surveyDetailsBinding.lifecycleOwner = this
        if (arguments != null && requireArguments().containsKey(DATA)) {
            selectedData = requireArguments()[DATA] as DashboardModel?
        }
        setStatePageAdapter()

    }
    private fun setStatePageAdapter() {
        val viewPagerAdapter = SurveyDetailsViewPagerAdapter(requireActivity().supportFragmentManager, 0)
        viewPagerAdapter.addFragment(FragmentEnquiry.newInstance(selectedData), "ENQUIRY")
        viewPagerAdapter.addFragment(FragmentAddresses.newInstance(selectedData), "ADDRESSES")
        viewPagerAdapter.addFragment(SurveyDetailsSequenceFragment.newInstance(selectedData), "SEQUENCES")
        viewPagerAdapter.addFragment(FragmentInventory.newInstance(selectedData), "INVENTORY")
        viewPagerAdapter.addFragment(FragmentPlanning.newInstance(selectedData), "PLANNING")
        viewPagerAdapter.addFragment(FragmentComments.newInstance(selectedData), "COMMENTS")
        viewPagerAdapter.addFragment(FragmentPictures.newInstance(selectedData), "PICTURES")
        viewPagerAdapter.addFragment(FragmentSequenceDetails.newInstance(selectedData), "SEQUENCE DETAILS")
        //viewPagerAdapter.addFragment(PartnerAndServiceFragment.newInstance(selectedData), "PARTNER AND SERVICE")
        surveyDetailsBinding.viewPager.adapter = viewPagerAdapter
        surveyDetailsBinding.tabLayout.setupWithViewPager(surveyDetailsBinding.viewPager, true)
        surveyDetailsBinding.viewPager.currentItem = 0
        surveyDetailsBinding.viewPager.isSaveEnabled = true
        surveyDetailsBinding.viewPager.offscreenPageLimit = 8
        surveyDetailsBinding.viewPager.addOnPageChangeListener(object : OnPageChangeListener {
            var currentPosition = 0
            override fun onPageSelected(newPosition: Int) {

                val fragmentToHide: FragmentLifecycleInterface =
                    viewPagerAdapter.getItem(currentPosition) as FragmentLifecycleInterface
                fragmentToHide.onPauseFragment()
                val fragmentToShow: FragmentLifecycleInterface =
                    viewPagerAdapter.getItem(newPosition) as FragmentLifecycleInterface
                fragmentToShow.onResumeFragment(null)
                if(fragmentToShow is FragmentInventory){
                    fragmentToShow.resetValues()
                }
                currentPosition = newPosition

            }
            override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {}
            override fun onPageScrollStateChanged(arg0: Int) {}
        })
    }


}