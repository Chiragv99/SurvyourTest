package com.pickfords.surveyorapp.view.surveyDetails

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pickfords.surveyorapp.databinding.ItemAdditionalNameEmailBinding
import com.pickfords.surveyorapp.model.dashboard.DashboardModel
import com.pickfords.surveyorapp.model.surveyDetails.AdditionalInfoPartnerModel


class AdditionalDetailAdapter(
    private var additionalList: ArrayList<AdditionalInfoPartnerModel>,
    private var selectedDashboardData: DashboardModel?,
    private val clickListener: ContactClickListener
) :
    RecyclerView.Adapter<AdditionalDetailAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemAdditionalNameEmailBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItem(model: AdditionalInfoPartnerModel) {

            binding.edtInputFirstName.setOnTouchListener(OnTouchListener { arg0, arg1 ->
                Log.e("OnTouch", "FirstName")
                binding.edtInputFirstName.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {

                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {

                    }

                    override fun afterTextChanged(s: Editable?) { if (!s.isNullOrEmpty())
                            model.firstName = binding.edtInputFirstName.text.toString()
                           Log.e("Changed", "FirstName")
                    }
                })


                false
            })

            binding.edtInputLastName.setOnTouchListener(OnTouchListener { arg0, arg1 ->
                Log.e("OnTouch", "LastName")
                binding.edtInputLastName.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {

                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {

                    }

                    override fun afterTextChanged(s: Editable?) {
                        if (!s.isNullOrEmpty())
                            model.lastName = binding.edtInputLastName.text.toString()
                        Log.e("Changed", "LastName")
                    }
                })


                false
            })


            binding.edtInputPhone.setOnTouchListener(OnTouchListener { arg0, arg1 ->
                Log.e("OnTouch", "Phone")
                binding.edtInputPhone.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {

                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {

                    }

                    override fun afterTextChanged(s: Editable?) {
                        if (!s.isNullOrEmpty())
                            model.phone = binding.edtInputPhone.text.toString()
                        Log.e("Changed", "Phone")
                    }
                })


                false
            })

            binding.edtInputEmail.setOnTouchListener(OnTouchListener { arg0, arg1 ->
                Log.e("OnTouch", "Email")
                binding.edtInputEmail.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {

                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {

                    }

                    override fun afterTextChanged(s: Editable?) {
                        if (!s.isNullOrEmpty())
                            model.email = binding.edtInputEmail.text.toString()
                        Log.e("Changed", "Email")
                    }
                })


                false
            })


            binding.edtInputComment.setOnTouchListener(OnTouchListener { arg0, arg1 ->
                Log.e("OnTouch", "Comment")
                binding.edtInputComment.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {

                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {

                    }

                    override fun afterTextChanged(s: Editable?) {
                        if (!s.isNullOrEmpty())
                            model.comments = binding.edtInputComment.text.toString()
                        Log.e("Changed", "Comment")
                    }
                })


                false
            })

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemAdditionalNameEmailBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.item = additionalList[position]
        holder.binding.executePendingBindings()

        holder.bindItem(additionalList[position])
        holder.binding.btnSave.setOnClickListener {
            Log.e("Save Data", "Save Data")
        }

        holder.binding.btnRemove.setOnClickListener {

            clickListener.removeContact(
                position,
                additionalList[position].Id,
                holder.binding.root.context
            )

        }
    }

    override fun getItemCount(): Int {
        return additionalList.size
    }

    fun updateList(additionalList: ArrayList<AdditionalInfoPartnerModel>) {
        this.additionalList = additionalList
        notifyDataSetChanged()
    }

    /**
     * Interface handles click event of Feeds ItemView
     * */
    interface ContactClickListener {
        fun removeContact(
            position: Int,
            id: Int?,
            context: Context
        )
    }
}