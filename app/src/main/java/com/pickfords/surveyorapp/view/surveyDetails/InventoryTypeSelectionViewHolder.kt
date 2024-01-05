package com.pickfords.surveyorapp.view.surveyDetails

import androidx.recyclerview.widget.RecyclerView
import com.pickfords.surveyorapp.databinding.ItemInventoryTypeSelectionBinding
import com.pickfords.surveyorapp.model.surveyDetails.InventoryTypeSelectionModel


class InventoryTypeSelectionViewHolder(val binding: ItemInventoryTypeSelectionBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(data: InventoryTypeSelectionModel) {
        binding.position = adapterPosition
        binding.holder = this
        binding.data = data
    }

}