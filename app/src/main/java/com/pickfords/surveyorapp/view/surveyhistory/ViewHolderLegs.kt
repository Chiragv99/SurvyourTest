package com.pickfords.surveyorapp.view.surveyhistory

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.pickfords.surveyorapp.databinding.ItemShowLegsBinding
import com.pickfords.surveyorapp.interfaces.EditLegsInterface
import com.pickfords.surveyorapp.model.surveyDetails.ShowLegsModel


class ViewHolderLegs(val binding: ItemShowLegsBinding, private val onLegsEdit: EditLegsInterface?) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(data: ShowLegsModel) {
        binding.holder = this
        binding.itemData = data
        binding.imgEditLegs.setOnClickListener {
            onLegsEdit!!.onEditLegs(adapterPosition, data)
            Log.d("onClick","Edit CLicked")
        }
    }
}