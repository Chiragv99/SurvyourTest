package com.pickfords.surveyorapp.view.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.pickfords.surveyorapp.R
import com.pickfords.surveyorapp.databinding.DialogAddItemBinding
import kotlinx.android.synthetic.main.dialog_dimensions.*


class AddItemDialog(
    var mContext: Context,
    var name: String,
    var volume: String,
    var length: String?,
    var width: String?,
    var height: String?
) : Dialog(mContext, R.style.ThemeDialog) {
    private lateinit var binding: DialogAddItemBinding
    private var listener: OkButtonListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCanceledOnTouchOutside(false)
        setCancelable(false)

        binding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext),
            R.layout.dialog_add_item,
            null,
            false
        )

        setContentView(binding.root)

        binding.etName.setText(name)
        binding.etVolume.setText(volume)
        binding.etLength.setText(length)
        binding.etHeight.setText(height)
        binding.etWidth.setText(width)
        binding.etName.text?.let { binding.etName.requestFocus(it.length) }
        this.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        binding.etName.requestFocus()

        btnOk.setOnClickListener {
            if (listener != null) {
                if (binding.etName.text.isNullOrEmpty()) {
                    Toast.makeText(
                        mContext,
                        mContext.getString(R.string.penter_name),
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (binding.etVolume.text.isNullOrEmpty()) {
                    Toast.makeText(
                        mContext,
                        mContext.getString(R.string.penter_volume),
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (binding.etLength.text.isNullOrEmpty()) {
                    Toast.makeText(
                        mContext,
                        mContext.getString(R.string.enter_length_val),
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (binding.etWidth.text.isNullOrEmpty()) {
                    Toast.makeText(
                        mContext,
                        mContext.getString(R.string.enter_width_val),
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (binding.etHeight.text.isNullOrEmpty()) {
                    Toast.makeText(
                        mContext,
                        mContext.getString(R.string.enter_height_val),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    name = binding.etName.text.toString()
                    volume = binding.etVolume.text.toString()
                    length = binding.etLength.text.toString()
                    width = binding.etWidth.text.toString()
                    height = binding.etHeight.text.toString()
                    listener?.onOkPressed(
                        this,
                        binding.etName.text.toString(),
                        binding.etVolume.text.toString(),
                        binding.etLength.text.toString(),
                        binding.etWidth.text.toString(),
                        binding.etHeight.text.toString()
                    )
                }
            } else {
                dismiss()
            }
        }
        btnCancel.setOnClickListener {
            dismiss()
        }
    }

    fun setListener(listener: OkButtonListener?): AddItemDialog {
        this.listener = listener
        return this
    }

    interface OkButtonListener {
        fun onOkPressed(
            dialog: AddItemDialog,
            name: String?,
            volume: String?,
            length: String?,
            width: String?,
            height: String?
        )
    }

}