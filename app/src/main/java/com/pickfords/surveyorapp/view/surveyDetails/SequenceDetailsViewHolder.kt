package com.pickfords.surveyorapp.view.surveyDetails

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.pickfords.surveyorapp.databinding.ItemSequenceDetailsBinding
import com.pickfords.surveyorapp.model.surveyDetails.SequenceDetailsModel


class SequenceDetailsViewHolder(val binding: ItemSequenceDetailsBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(data: SequenceDetailsModel) {
        binding.position = adapterPosition
        binding.holder = this
        binding.itemData = data

    }

     fun showMore() {
        binding.tvComments.visibility = View.VISIBLE
        binding.tvCommentsTitle.visibility = View.VISIBLE
        binding.tvDamageTitle.visibility = View.VISIBLE
        binding.tvDamage.visibility = View.VISIBLE
        binding.groupCb.visibility = View.VISIBLE

    }

     fun showLess() {
        binding.tvComments.visibility = View.GONE
        binding.tvCommentsTitle.visibility = View.GONE
        binding.tvDamageTitle.visibility = View.GONE
        binding.tvDamage.visibility = View.GONE
        binding.tvDimenstion.visibility = View.GONE
        binding.tvDimenstionTitle.visibility = View.GONE
        binding.groupCb.visibility = View.GONE

    }

}