package com.pickfords.surveyorapp.view.contactus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.pickfords.surveyorapp.R
import com.pickfords.surveyorapp.view.BaseFragment
import kotlinx.android.synthetic.main.fragment_contact_us.*

class ContactUsFragment : BaseFragment() {

    private val viewModel by lazy { ContactUsViewModel() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contact_us, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbarWithMenu(requireActivity().getString(R.string.contact_us))
        if (savedInstanceState == null) viewModel.init(
            requireContext()
        )
        setAdapter()
    }

    private fun setAdapter() {
        recyclerViewContactus.layoutManager = LinearLayoutManager(activity)
        recyclerViewContactus.adapter = viewModel.getAdapter()

    }
}