package com.pickfords.surveyorapp.view.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.pickfords.surveyorapp.R
import com.pickfords.surveyorapp.databinding.DialougViewGetSequenceimageBinding
import com.pickfords.surveyorapp.utils.Utility
import kotlinx.android.synthetic.main.dialog_comments.btnCancel
import kotlinx.android.synthetic.main.dialog_comments.btnOk
import kotlinx.android.synthetic.main.dialog_comments.tvDamageMessage
import kotlinx.android.synthetic.main.dialog_comments.tvMiscMessage

class ShowSequenceDetailImage(
    var mContext: Context,
    private val picturebyte: String?,
    private val imageUrl: String?,
    val inventoryItemName: String?
) : Dialog(mContext, R.style.ThemeDialog) {
    private lateinit var binding: DialougViewGetSequenceimageBinding
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
            R.layout.dialoug_view_get_sequenceimage,
            null,
            false
        )

        setContentView(binding.root)

        // For Load sequence image
        if (!picturebyte.isNullOrEmpty() && picturebyte != "null"){
            val  imageBytes : ByteArray = Base64.decode(Utility.resizeBase64Image(picturebyte), Base64.DEFAULT)
            val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            binding.ivSequencedetail.setImageBitmap(decodedImage)
        }else{
            Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(binding.ivSequencedetail);
        }

        tvDamageMessage.text = inventoryItemName
        tvMiscMessage.visibility = if (isMiscSelected!!) View.VISIBLE else View.GONE
        btnOk.setOnClickListener {
            dismiss()
        }
        btnCancel.setOnClickListener {
            dismiss()
        }
    }

    fun setListener(listener: OkButtonListener?): ShowSequenceDetailImage {
        this.listener = listener
        return this
    }

    fun setDamageTitle(isDamageSelected: Boolean?): ShowSequenceDetailImage {
        this.isDamageSelected = isDamageSelected
        return this
    }

    fun setMiscTitle(isMiscSelected: Boolean?): ShowSequenceDetailImage {
        this.isMiscSelected = isMiscSelected
        return this
    }

    interface OkButtonListener {
        fun onOkPressed(dialogDamage: ShowSequenceDetailImage, comment: String?)
    }
}