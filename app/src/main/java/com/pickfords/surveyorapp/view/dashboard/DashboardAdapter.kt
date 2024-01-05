package com.pickfords.surveyorapp.view.dashboard

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.pickfords.surveyorapp.R
import com.pickfords.surveyorapp.databinding.ItemDashboardBinding
import com.pickfords.surveyorapp.interfaces.OnItemSelected
import com.pickfords.surveyorapp.model.dashboard.DashboardModel
import com.pickfords.surveyorapp.utils.AppConstants
import com.pickfords.surveyorapp.utils.Session.Companion.DATA
import com.pickfords.surveyorapp.utils.convertDateTimeReturn
import com.pickfords.surveyorapp.view.dialogs.MessageDialog
import com.pickfords.surveyorapp.viewModel.dashboard.DashboardViewModel
import java.util.*

class DashboardAdapter(
    val context: Context,
    private val list: MutableList<DashboardModel>,
    val viewModel: DashboardViewModel,
    val onItemSelected: OnItemSelected<DashboardModel>,
) :
    RecyclerView.Adapter<DashboardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {

        val layoutInflater = LayoutInflater.from(parent.context)

        val binder = DataBindingUtil.inflate<ItemDashboardBinding>(
            layoutInflater,
            R.layout.item_dashboard,
            parent,
            false
        )
        return DashboardViewHolder(binder, viewModel)
    }

    override fun onBindViewHolder(holder: DashboardViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.bind(list[position])


        if (list[position].submittedDate != null) {
            holder.binding.cvSubmittedDateTime.visibility = View.VISIBLE
        } else {
            holder.binding.cvSubmittedDateTime.visibility = View.GONE
        }

        if (list[position].enquiryType != null) {
            if(list[position].enquiryType == "0"){
                holder.binding.tvSequenceType.text = "Private"
            }else{
                holder.binding.tvSequenceType.text = "Corporate"
            }

        } else {
            holder.binding.tvSequenceType.text = "N/A"
        }

        holder.binding.tvDateTimeSurvey.text = "Survey Date: ${convertDateTimeReturn(list[position].surveyDate)} "
        holder.binding.tvDateTime.text = "Estimated Move Date: ${convertDateTimeReturn(list[position].moveDate)} "
        if (list[position].isSubmitted) {
            holder.binding.btnSubmit.visibility = View.GONE
        } else {
            holder.binding.btnSubmit.visibility = View.VISIBLE
        }


        holder.itemView.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable(DATA, list[position])
            AppConstants.surveyID = list[position].surveyId
            it.findNavController().navigate(R.id.surveyDetailsFragment, bundle)
        }

        holder.binding.btnSubmit.setOnClickListener {
            onItemSelected.onItemSelected(list[position], position)
        }
        holder.binding.delete.setOnClickListener {
            MessageDialog(context, context.getString(R.string.delete_survey_alert))
                .setListener(object : MessageDialog.OkButtonListener {
                    override fun onOkPressed(dialog: MessageDialog) {
                        dialog.dismiss()
                        holder.onDelete(holder.binding.delete, position, list[position])
                    }
                })
                .setCancelButton(true)
                .show()

        }
    }
    override fun getItemCount(): Int {
        return list.size
    }
}