package com.pickfords.surveyorapp.view.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.databinding.DataBindingUtil
import com.pickfords.surveyorapp.R
import com.pickfords.surveyorapp.databinding.FragmentMeasurementPreferenceBinding
import com.pickfords.surveyorapp.utils.Session
import com.pickfords.surveyorapp.view.BaseFragment

class MeasurementPreferenceFragment : BaseFragment() {

    private lateinit var measurementBinding: FragmentMeasurementPreferenceBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        measurementBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_measurement_preference,
            container,
            false
        )
        return measurementBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbarWithMenu(requireActivity().getString(R.string.measurement_preference))
        val measurementValue =
            session.getDataByKey(Session.MeasurementPreference, getString(R.string.inches_feet))
        if (measurementValue == getString(R.string.centimeters_metres)) {
            measurementBinding.radioMeter.isChecked = true
        } else {
            measurementBinding.radioFeet.isChecked = true
        }

        measurementBinding.btnSave.setOnClickListener {
            val selectedId: Int = measurementBinding.radioGroup.checkedRadioButtonId
            val radioButton = requireActivity().findViewById(selectedId) as RadioButton

            session.storeDataByKey(Session.MeasurementPreference, radioButton.text.toString())
            requireActivity().supportFragmentManager.popBackStack()
        }
    }
}