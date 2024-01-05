package com.pickfords.surveyorapp.view.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import com.pickfords.surveyorapp.R
import com.pickfords.surveyorapp.databinding.DialogSyncDataBinding
import kotlinx.android.synthetic.main.dialog_sync_data.*

class SyncDataDialog(context: Context, val message: String?, val isFirstTime: Boolean = false) :
    Dialog(context, R.style.ThemeDialog) {
    private lateinit var binding: DialogSyncDataBinding
    private var listener: OkButtonListener? = null
    private var cancelListener: OkButtonListener? = null
    private var isCancelButton: Boolean? = false
    private var title: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCanceledOnTouchOutside(false)
        setCancelable(false)

        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_sync_data,
            null,
            false
        )

        setContentView(binding.root)

        if (!message.isNullOrEmpty()) binding.tvMessage.text = message
        if (title != null && title!!.isNotEmpty()) binding.tvTitle.text = title

        if(isFirstTime)
        {
            binding.tvTitle.text = "Update Required"
            binding.tvMessage.text = "An update is required to fetch your survey data."
        }



        btnCancel.visibility = if (isCancelButton!!) View.VISIBLE else View.GONE
        btnOk.setOnClickListener {
            if (listener != null) listener?.onOkPressed(this)
            else dismiss()
        }
        btnCancel.setOnClickListener {
            dismiss()
        }
    }

    fun setListener(listener: OkButtonListener?): SyncDataDialog {
        this.listener = listener
        return this
    }
    fun setTitleData(title: String?): SyncDataDialog {
        this.title = title
        return this
    }

    fun setCancelButton(isCancelButton: Boolean?): SyncDataDialog {
        this.isCancelButton = isCancelButton
        return this
    }

    interface OkButtonListener {
        fun onOkPressed(dialog: SyncDataDialog)
    }
}