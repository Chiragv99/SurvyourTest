package com.pickfords.surveyorapp.view.surveyDetails

import androidx.recyclerview.widget.RecyclerView
import com.pickfords.surveyorapp.databinding.ListAddInventorySubItemBinding
import com.pickfords.surveyorapp.model.surveyDetails.InventoryTypeSelectionModel
import com.pickfords.surveyorapp.model.surveyDetails.SequenceDetailsModel


class InventoryAddInventorySubItemViewHolder(val binding: ListAddInventorySubItemBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(data: SequenceDetailsModel) {
        binding.position = adapterPosition
        binding.holder = this
        binding.itemData = data
    }
}