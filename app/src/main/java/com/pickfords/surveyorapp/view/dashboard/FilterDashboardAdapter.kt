package com.pickfords.surveyorapp.view.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pickfords.surveyorapp.databinding.ItemFilterDashboardBinding
import com.pickfords.surveyorapp.interfaces.OnItemSelected
import com.pickfords.surveyorapp.model.dashboard.FilterSurvey
import com.pickfords.surveyorapp.viewModel.dashboard.DashboardViewModel

class FilterDashboardAdapter(private val list: MutableList<FilterSurvey>, val viewModel: DashboardViewModel, private val onItemSelect: OnItemSelected<FilterSurvey>) : RecyclerView.Adapter<FilterDashboardAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemFilterDashboardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemFilterDashboardBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.binding.item = item
        holder.binding.root.setOnClickListener {
            list.forEach {
                it.isSelected = false
            }
            item.isSelected = true
            onItemSelect.onItemSelected(item,position)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}