package com.pickfords.surveyorapp.picker

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.pickfords.surveyorapp.R
import com.pickfords.surveyorapp.view.BaseBottomSheetDialogFragment
import kotlinx.android.synthetic.main.common_dialog_image_picker.*

class ImagePickerBottomSheetDialog(context: Context) : BaseBottomSheetDialogFragment(),
    View.OnClickListener {
    private var lastClickTime: Long = 0

    companion object {
        private lateinit var onModeSelected: OnModeSelected

        fun newInstance(
            context: Context,
            listener: OnModeSelected
        ): ImagePickerBottomSheetDialog {
            this.onModeSelected = listener
            return ImagePickerBottomSheetDialog(context)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.common_dialog_image_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvCamera.setOnClickListener(this)
        tvGallery.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.tvCamera -> {
                pickImageFromSource(Sources.CAMERA)
            }
            R.id.tvGallery -> {
                pickImageFromSource(Sources.GALLERY)
            }
        }
        dismissDialog()
    }


    @SuppressLint("CheckResult")
    private fun pickImageFromSource(source: Sources) {
        val activity = context as AppCompatActivity
        val manager = activity.supportFragmentManager

        RxImagePicker.with(manager)
            .requestImage(source)
            .subscribe({
                onModeSelected.onMediaPicked(it)
            }, {
                it.message?.let { it1 -> onModeSelected.onError(it1) }
            })
    }

    interface OnModeSelected {
        fun onMediaPicked(uri: Uri)
        fun onError(message: String)
    }
}