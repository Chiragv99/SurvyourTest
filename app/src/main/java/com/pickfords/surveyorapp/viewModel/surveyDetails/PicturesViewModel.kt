package com.pickfords.surveyorapp.viewModel.surveyDetails


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.os.Build
import android.util.Base64
import androidx.lifecycle.MutableLiveData
import com.pickfords.surveyorapp.R
import com.pickfords.surveyorapp.base.BaseViewModel
import com.pickfords.surveyorapp.model.dashboard.DashboardModel
import com.pickfords.surveyorapp.model.surveyDetails.PicturesModel
import com.pickfords.surveyorapp.network.BaseModel
import com.pickfords.surveyorapp.network.CallbackObserver
import com.pickfords.surveyorapp.network.Networking
import com.pickfords.surveyorapp.utils.Session
import com.pickfords.surveyorapp.utils.Utility
import com.pickfords.surveyorapp.view.BaseActivity
import com.pickfords.surveyorapp.view.dialogs.MessageDialog
import com.pickfords.surveyorapp.view.dialogs.ShowSequenceDetailImage
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import kotlin.random.Random


class PicturesViewModel(val context: Context) : BaseViewModel() {
    private val picturesList: MutableList<PicturesModel> = mutableListOf()
    private var picturesLiveList: MutableLiveData<List<PicturesModel>> = MutableLiveData()

    private var picturesAdapter: PicturesAdapter? = null
    fun getPicturesAdapter(): PicturesAdapter? = picturesAdapter
    var selectedData: DashboardModel? = null

    @SuppressLint("NotifyDataSetChanged")
    fun init() {
        picturesAdapter = PicturesAdapter(picturesList, this)
        picturesLiveList.observeForever {
            if (it != null) {
                picturesList.clear()
                picturesList.addAll(it)
                picturesAdapter?.notifyDataSetChanged()
            }
        }
    }
    //    For Get  Survey Picture
    fun getSurveyPictureList(context: Context, survey: String) =
        if ((context as BaseActivity).picturesDao.getPicturesList() != null && context.picturesDao.getPicturesListBySurveyId(survey,false).size > 0) {
            picturesLiveList.postValue(context.picturesDao.getPicturesListBySurveyId(survey,false))
        } else {
        }


    private fun getByteArrayFromImageURL(bitmap: Bitmap): String? {
        val stream = ByteArrayOutputStream()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            bitmap.compress(CompressFormat.JPEG, 70, stream)
        }
        // get the base 64 string
        return Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT)
    }
    //    For Save Survey Picture
    fun saveSurveyPicture(data: Bitmap, path: String) {
        val partMap: Map<String, RequestBody> = mapOf(
            "SurveyPicId" to "0".toRequestBody("text/plain".toMediaTypeOrNull()),
            "SurveyId" to selectedData!!.surveyId.toRequestBody("text/plain".toMediaTypeOrNull()),
            "PictureName" to path.toRequestBody("text/plain".toMediaTypeOrNull()),
            "CreatedBy" to Session(context).user!!.userId!!.toRequestBody("text/plain".toMediaTypeOrNull()),
            "PictureByte" to getByteArrayFromImageURL(data)!!.toRequestBody("text/plain".toMediaTypeOrNull())
        )

        Utility.setOfflineTag(context, "Picture Uploaded Successfully")

        val model = PicturesModel()
        model.surveyPicId =
            "ADD" + (Random.nextInt().toString())
        model.surveyId = selectedData!!.surveyId
        model.pictureName = path
        model.createdBy = Session(context).user!!.userId!!.toString()

        (context as BaseActivity).picturesDao.insert(model)
        picturesLiveList.postValue(
            (context as BaseActivity).picturesDao.getPicturesListBySurveyId(model.surveyId!!,false)
        )
        return

    }

//    For Delete Survey Picture
    @SuppressLint("NotifyDataSetChanged")
    fun deleteSurveyPicture(context: Context, surveyPicId: String, position: Int) {

    Utility.setOfflineTag(context, "Picture Deleted Successfully")
    if (surveyPicId.contains("ADD")) {
        (context as BaseActivity).picturesDao.delete(surveyPicId)
        picturesList.removeAt(position)
        picturesAdapter?.notifyDataSetChanged()
    } else {
        val data =
            (context as BaseActivity).picturesDao.getPicturesListByPicId(surveyPicId)
        data?.isDelete = true
        (context as BaseActivity).picturesDao.update(data)
        picturesList.removeAt(position)
        picturesAdapter?.notifyDataSetChanged()
    }
    return
    }

    fun openPictureInFullscreenMode(context: Context, viewModel: PicturesViewModel, position: Int) {

        if (picturesList == null || picturesList.isEmpty()){
            return
        }
        if (position < picturesList.size){
            ShowSequenceDetailImage(
                context,
                picturesList[position].pictureByte.toString(),
                picturesList[position].pictureName.toString(),
                "Picture"
            )
                .setListener(object : ShowSequenceDetailImage.OkButtonListener {
                    override fun onOkPressed(
                        dialogDamage: ShowSequenceDetailImage,
                        roomName: String?
                    ) {
                        dialogDamage.dismiss()
                    }
                })
                .setDamageTitle(true)
                .show()
        }


    }
    fun setData(selectedData: DashboardModel?) {
        this.selectedData = selectedData
    }
}