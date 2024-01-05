package com.pickfords.surveyorapp.view.contactus

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pickfords.surveyorapp.R

class ContactUsItemAdapter(
    private val mContext: Context,
    val viewModel: ContactUsViewModel,
    val list: ArrayList<ContactUsModel>
) : RecyclerView.Adapter<ContactUsItemAdapter.SimpleViewHolder1>() {

    class SimpleViewHolder1(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(mContext: Context, item: ContactUsModel, viewModel: ContactUsViewModel) {
            val tvAddress: TextView = itemView.findViewById(R.id.tvQuestion) as TextView
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ContactUsItemAdapter.SimpleViewHolder1 {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_frequentlyasked_contactus, parent, false)
        return ContactUsItemAdapter.SimpleViewHolder1(view)
    }

    override fun onBindViewHolder(
        viewHolder: ContactUsItemAdapter.SimpleViewHolder1,
        position: Int
    ) {
        val item = list[position]
        viewHolder.bind(mContext, item, viewModel)
    }

    override fun getItemCount(): Int {
        return list.size
    }

}