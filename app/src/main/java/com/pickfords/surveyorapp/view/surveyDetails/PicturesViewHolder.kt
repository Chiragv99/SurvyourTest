package com.pickfords.surveyorapp.view.surveyDetails

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pickfords.surveyorapp.R
import com.pickfords.surveyorapp.databinding.ItemPicturesBinding
import com.pickfords.surveyorapp.model.surveyDetails.PicturesModel
import com.pickfords.surveyorapp.utils.Utility
import com.pickfords.surveyorapp.view.dialogs.MessageDialog
import com.pickfords.surveyorapp.viewModel.surveyDetails.PicturesViewModel


class PicturesViewHolder(val binding: ItemPicturesBinding, val context: Context, val viewModel: PicturesViewModel) :
    RecyclerView.ViewHolder(binding.root) {

    @SuppressLint("ResourceAsColor")
    fun bind(data: PicturesModel) {
        binding.position = adapterPosition
        binding.holder = this
        binding.itemData = data
        binding.viewmodel = viewModel

        if (!data.pictureByte.isNullOrEmpty()){
            val  imageBytes : ByteArray = Base64.decode(Utility.resizeBase64Image(data.pictureByte!!), Base64.DEFAULT)
            val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            binding.ivPicture.setImageBitmap(decodedImage)
        }else{
            Glide.with(context)
                .load(data.pictureName)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(binding.ivPicture);
        }
    }

    private fun deletePictureButtonClicked(id: String,position: Int,viewModel:PicturesViewModel) {
        MessageDialog(context, context.getString(R.string.are_you_sure_delete_image))
            .setListener(object : MessageDialog.OkButtonListener {
                override fun onOkPressed(dialog: MessageDialog) {
                    viewModel.deleteSurveyPicture(context, id,position)
                    dialog.dismiss()
                }

            })
            .setCancelButton(true)
            .show()

    }

      fun onViewFullMode(position: Int){
        // Log.e("OnPicture","FullScreen" + position.toString())
          viewModel.openPictureInFullscreenMode(context,viewModel,position)
    }

    fun onClickDelete(view: View, position: Int, data: PicturesModel?,viewModel:PicturesViewModel) {
        deletePictureButtonClicked(data!!.surveyPicId!!,position,viewModel)
    }
}