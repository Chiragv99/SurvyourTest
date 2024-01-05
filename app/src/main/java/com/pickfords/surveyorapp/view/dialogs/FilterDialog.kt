package com.pickfords.surveyorapp.view.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.pickfords.surveyorapp.R
import com.pickfords.surveyorapp.databinding.DialogFilterBinding

class FilterDialog(var mContext: Context) : Dialog(mContext, R.style.ThemeDialog) {

    private lateinit var binding: DialogFilterBinding
    private var listener: OkButtonListener? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext),
            R.layout.dialog_filter,
            null,
            false
        )

        setContentView(binding.root)

        binding.txtEnquiryNoSmallestToLargest.setOnClickListener {
            listener?.onOkPressed(this,mContext.getString(R.string.enquiryNo) )
        }
        binding.txtEnquiryNoLargestToSmallest.setOnClickListener {
            listener?.onOkPressed(this, mContext.getString(R.string.enquiryNodesc))
        }
        binding.txtMoveManager.setOnClickListener {
            listener?.onOkPressed(this, binding.txtMoveManager.text.toString().replace(" ", ""))
        }
        binding.txtFirstName.setOnClickListener {
            listener?.onOkPressed(this,mContext.getString(R.string.firstNameLabel))
        }
        binding.txtSurveyDateOldest.setOnClickListener {
            listener?.onOkPressed(this,mContext.getString(R.string.surveyDateOldestlabel))
        }
        binding.txtSurveyDateNewest.setOnClickListener {
            listener?.onOkPressed(this,mContext.getString(R.string.surveyDateNewestlabel))
        }


        binding.imgClose.setOnClickListener {
            dismiss()
        }
    }

    fun setListener(listener: OkButtonListener?): FilterDialog {
        this.listener = listener
        return this
    }

    interface OkButtonListener {
        fun onOkPressed(
            dialog: FilterDialog, selectedFilter: String
        )
    }


}