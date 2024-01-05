package com.pickfords.surveyorapp.utils

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.pickfords.surveyorapp.R
import kotlinx.android.synthetic.main.common_dialog_image_picker.*

class ImagePickerDialog(context: FragmentActivity?, private val itemClick: onItemClick) :
    AlertDialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.common_dialog_image_picker)

        tvCamera.setOnClickListener {
            itemClick.onCameraClicked()
            dismiss()
        }

        tvGallery.setOnClickListener {
            itemClick.onGalleryClicked()
            dismiss()
        }
    }
}

interface onItemClick {
    fun onCameraClicked()
    fun onGalleryClicked()
}