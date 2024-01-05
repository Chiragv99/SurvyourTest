package com.pickfords.surveyorapp.view.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.pickfords.surveyorapp.R
import com.pickfords.surveyorapp.databinding.DialogItemDeselectBinding
import kotlinx.android.synthetic.main.dialog_comments.*

class ItemDeSelectDialog(var mContext: Context,var message : String) : Dialog(mContext, R.style.ThemeDialog) {
    private lateinit var binding: DialogItemDeselectBinding
    private var listener: OkButtonListener? = null
    private var isDataFilled: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCanceledOnTouchOutside(false)
        setCancelable(false)

        binding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext),
            R.layout.dialog_item_deselect,
            null,
            false
        )

        binding.tvMiscMessage.text = message
        setContentView(binding.root)

        btnOk.setOnClickListener {
            if (listener != null) {
                listener?.onOkPressed(this, "")
            } else {
                dismiss()
            }
        }
        btnCancel.setOnClickListener {
            if (listener != null) {
                listener?.onCancelPressed(this, "")
            }
            dismiss()
        }
    }

    fun setListener(listener: OkButtonListener?): ItemDeSelectDialog {
        this.listener = listener
        return this
    }

    interface OkButtonListener {
        fun onOkPressed(dialogDamage: ItemDeSelectDialog, comment: String?)
        fun onCancelPressed(dialogDamage: ItemDeSelectDialog, comment: String?)
    }
}