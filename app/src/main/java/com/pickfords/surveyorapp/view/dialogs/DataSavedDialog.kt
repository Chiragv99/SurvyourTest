package com.pickfords.surveyorapp.view.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.pickfords.surveyorapp.R
import com.pickfords.surveyorapp.databinding.DialogDataSavedBinding

class DataSavedDialog(context: Context, val message: String?) :
    Dialog(context, R.style.ThemeDialog) {
    private lateinit var binding: DialogDataSavedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCanceledOnTouchOutside(true)
        setCancelable(true)

        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_data_saved,
            null,
            false
        )

        setContentView(binding.root)

        binding.messageText.text = message
        binding.btnOk.setOnClickListener {
            dismiss()
        }

    }
}