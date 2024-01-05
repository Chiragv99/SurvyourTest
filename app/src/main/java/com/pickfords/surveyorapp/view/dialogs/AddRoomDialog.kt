package com.pickfords.surveyorapp.view.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.pickfords.surveyorapp.R
import com.pickfords.surveyorapp.databinding.DialogAddRoomBinding
import kotlinx.android.synthetic.main.dialog_comments.*

class AddRoomDialog(var mContext: Context, val roomName: String?) : Dialog(mContext, R.style.ThemeDialog) {
    private lateinit var binding: DialogAddRoomBinding
    private var listener: OkButtonListener? = null
    private var cancelListener: OkButtonListener? = null
    private var isDamageSelected: Boolean? = false
    private var isMiscSelected: Boolean? = false
    private var isDataFilled: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCanceledOnTouchOutside(false)
        setCancelable(false)

        binding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext),
            R.layout.dialog_add_room,
            null,
            false
        )

        setContentView(binding.root)

        binding.etRoomName.setText(roomName)
        binding.etRoomName.text?.let { binding.etRoomName.requestFocus(it.length) }
        val imm = mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)

        tvDamageMessage.visibility = if (isDamageSelected!!) View.VISIBLE else View.GONE
        tvMiscMessage.visibility = if (isMiscSelected!!) View.VISIBLE else View.GONE
        btnOk.setOnClickListener {
            if (listener != null) {
                if (binding.etRoomName.text.isNullOrEmpty()) {
                    Toast.makeText(mContext, mContext.getString(R.string.enter_room_val), Toast.LENGTH_SHORT).show()
                } else {
                    isDataFilled = !binding.etRoomName.text.isNullOrEmpty()
                    listener?.onOkPressed(this, binding.etRoomName.text.toString())
                }

            } else {
                dismiss()
            }
        }
        btnCancel.setOnClickListener {
            dismiss()
        }
    }

    fun setListener(listener: OkButtonListener?): AddRoomDialog {
        this.listener = listener
        return this
    }

    fun setDamageTitle(isDamageSelected: Boolean?): AddRoomDialog {
        return this
    }

    interface OkButtonListener {
        fun onOkPressed(dialogDamage: AddRoomDialog, comment: String?)
    }
}