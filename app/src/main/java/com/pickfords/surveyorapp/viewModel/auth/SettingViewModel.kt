package com.pickfords.surveyorapp.viewModel.auth

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.pickfords.surveyorapp.base.BaseViewModel
import com.pickfords.surveyorapp.model.surveyDetails.BasicModel
import com.pickfords.surveyorapp.network.CallbackObserver
import com.pickfords.surveyorapp.network.Networking
import com.pickfords.surveyorapp.view.dialogs.MessageDialog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class SettingViewModel(val context: Context) : BaseViewModel() {
    val isClearField = MutableLiveData<Boolean>()

    @SuppressLint("NotifyDataSetChanged")
    fun init(context: Context) {

    }
    // Method for save password
    fun savePassword(context: Context, userId: String, oldPassword: String, newPassword: String) {
        isClearField.postValue(false)
        isLoading.postValue(true)
        Networking(context)
            .getServices()
            .postChangePassword(userId, oldPassword, newPassword)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CallbackObserver<BasicModel>() {
                override fun onSuccess(response: BasicModel) {
                    isClearField.postValue(true)
                    isLoading.postValue(false)
                    MessageDialog(context, response.message).show()
                }

                override fun onFailed(code: Int, message: String) {
                    isClearField.postValue(false)
                    isLoading.postValue(false)
                    MessageDialog(context, message).show()
                }
            })
    }
}