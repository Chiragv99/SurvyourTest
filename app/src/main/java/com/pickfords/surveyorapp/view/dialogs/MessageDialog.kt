package com.pickfords.surveyorapp.view.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import com.pickfords.surveyorapp.R
import com.pickfords.surveyorapp.databinding.DialogWithOkButtonBinding
import kotlinx.android.synthetic.main.dialog_with_ok_button.*

class MessageDialog(context: Context, val message: String?) : Dialog(context, R.style.ThemeDialog) {
    private lateinit var binding: DialogWithOkButtonBinding
    private var listener: OkButtonListener? = null
    private var cancelListener: OkButtonListener? = null
    private var isCancelButton: Boolean? = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCanceledOnTouchOutside(false)
        setCancelable(false)

        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_with_ok_button,
            null,
            false
        )

        setContentView(binding.root)

        if (message != null && message.isNotEmpty()) tvMessage.text = message
        btnCancel.visibility = if (isCancelButton!!) View.VISIBLE else View.GONE
        btnOk.setOnClickListener {
            if (listener != null) listener?.onOkPressed(this)
            else dismiss()
        }
        btnCancel.setOnClickListener {
            dismiss()
        }
    }

    fun setListener(listener: OkButtonListener?): MessageDialog {
        this.listener = listener
        return this
    }

    fun setCancelButton(isCancelButton: Boolean?): MessageDialog {
        this.isCancelButton = isCancelButton
        return this
    }

    interface OkButtonListener {
        fun onOkPressed(dialog: MessageDialog)
    }

}