package com.pickfords.surveyorapp.view.surveyhistory

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.pickfords.surveyorapp.R
import com.pickfords.surveyorapp.databinding.ItemShowLegsBinding
import com.pickfords.surveyorapp.interfaces.EditLegsInterface
import com.pickfords.surveyorapp.model.surveyDetails.ShowLegsModel

class ShowLegsAdapter(val list: ArrayList<ShowLegsModel>, private val onLegsEdit: EditLegsInterface?) :
    RecyclerView.Adapter<ViewHolderLegs>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderLegs {
        val layoutInflater = LayoutInflater.from(parent.context)

        val binder = DataBindingUtil.inflate<ItemShowLegsBinding>(
            layoutInflater, R.layout.item_show_legs, parent, false
        )
        return ViewHolderLegs(binder, onLegsEdit)
    }

    override fun onBindViewHolder(viewHolder: ViewHolderLegs, position: Int) {
        viewHolder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

}