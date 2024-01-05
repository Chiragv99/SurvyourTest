package com.pickfords.surveyorapp.view.surveyDetails

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pickfords.surveyorapp.R
import com.pickfords.surveyorapp.databinding.ItemPartnerServiceBinding
import com.pickfords.surveyorapp.model.PartnerListModel
import com.pickfords.surveyorapp.viewModel.surveyDetails.PartnerAndServiceViewModel


//todo Pranav Shah  & janki
class PartnerAndServiceAdapter(
    val context: Context,
    val list: MutableList<PartnerListModel>,
    val viewModel: PartnerAndServiceViewModel
) : RecyclerView.Adapter<PartnerAndServiceAdapter.ViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            ItemPartnerServiceBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(list[position],position)

    }

    inner class ViewHolder(val binding: ItemPartnerServiceBinding) :
        RecyclerView.ViewHolder(binding.root) {
        /**
         *This will help to bind values to ItemView
         */
        fun bindItems(listModel: PartnerListModel, position: Int) {
            binding.itemData = listModel
            binding.executePendingBindings()

            binding.isCheck.setOnCheckedChangeListener { _, b ->
                listModel.IsLink = b
            }

            // For Load sequence image
            Glide.with(context)
                .load(listModel.Image)
                .placeholder(R.drawable.ic_scooter)
                .error(R.drawable.ic_scooter)
                .into(binding.ivProfilePhoto);


        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}