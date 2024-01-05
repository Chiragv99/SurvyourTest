package com.pickfords.surveyorapp.view.termsandcondition

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pickfords.surveyorapp.R
import com.pickfords.surveyorapp.view.BaseFragment

class TermsAndConditionFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_terms_and_condition, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbarWithMenu(requireActivity().getString(R.string.terms_amp_conditions))

    }
}