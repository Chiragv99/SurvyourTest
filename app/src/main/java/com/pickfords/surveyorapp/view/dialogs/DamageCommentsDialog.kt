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
import com.pickfords.surveyorapp.databinding.DialogCommentsBinding
import kotlinx.android.synthetic.main.dialog_comments.*

class DamageCommentsDialog(var mContext: Context, val comment: String?) : Dialog(mContext, R.style.ThemeDialog) {
    private lateinit var binding: DialogCommentsBinding
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
            R.layout.dialog_comments,
            null,
            false
        )

        setContentView(binding.root)

        binding.etCommentMessage.setText(comment)
        binding.etCommentMessage.text?.let { binding.etCommentMessage.requestFocus(it.length) }
        val imm = mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        
        tvDamageMessage.visibility = if (isDamageSelected!!) View.VISIBLE else View.GONE
        tvMiscMessage.visibility = if (isMiscSelected!!) View.VISIBLE else View.GONE
        btnOk.setOnClickListener {
            if (listener != null) {
                if (binding.etCommentMessage.text.isNullOrEmpty()) {
                    Toast.makeText(mContext, mContext.getString(R.string.enter_comments_val), Toast.LENGTH_SHORT).show()
                } else {
                    isDataFilled = !binding.etCommentMessage.text.isNullOrEmpty()
                    listener?.onOkPressed(this, binding.etCommentMessage.text.toString())
                }

            } else {
                this.listener?.onCancelPressed(this)
            }
        }
        btnCancel.setOnClickListener {
            listener?.onCancelPressed(this)
        }
    }

    fun setListener(listener: OkButtonListener?): DamageCommentsDialog {
        this.listener = listener
        return this
    }

    fun setDamageTitle(isDamageSelected: Boolean?): DamageCommentsDialog {
        this.isDamageSelected = isDamageSelected
        return this
    }

    fun setMiscTitle(isMiscSelected: Boolean?): DamageCommentsDialog {
        this.isMiscSelected = isMiscSelected
        return this
    }

    interface OkButtonListener {
        fun onOkPressed(dialogDamage: DamageCommentsDialog, comment: String?)
        fun onCancelPressed(dialogDamage: DamageCommentsDialog)
    }
}