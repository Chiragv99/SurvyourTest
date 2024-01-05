package com.pickfords.surveyorapp.viewModel.surveyDetails

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.pickfords.surveyorapp.R
import com.pickfords.surveyorapp.databinding.ItemPicturesBinding
import com.pickfords.surveyorapp.model.surveyDetails.PicturesModel
import com.pickfords.surveyorapp.view.surveyDetails.PicturesViewHolder

class PicturesAdapter(private val list: MutableList<PicturesModel>, val viewModel:PicturesViewModel) :
    RecyclerView.Adapter<PicturesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PicturesViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        val binder = DataBindingUtil.inflate<ItemPicturesBinding>(
            layoutInflater,
            R.layout.item_pictures,
            parent,
            false
        )



        return PicturesViewHolder(binder,parent.context, viewModel)
    }

    override fun onBindViewHolder(holder: PicturesViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}