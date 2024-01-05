package com.pickfords.surveyorapp.viewModel.auth

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.pickfords.surveyorapp.base.BaseViewModel
import com.pickfords.surveyorapp.model.auth.SignInModel
import com.pickfords.surveyorapp.model.auth.User
import com.pickfords.surveyorapp.network.BaseModel
import com.pickfords.surveyorapp.network.CallbackObserver
import com.pickfords.surveyorapp.network.Networking
import com.pickfords.surveyorapp.view.dialogs.MessageDialog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ForgotPasswordViewModel(val context: Context) : BaseViewModel() {
    var email: ObservableField<String> = ObservableField()

    var userLiveData: MutableLiveData<User> = MutableLiveData()
    private var signInMutableLiveData: MutableLiveData<SignInModel> = MutableLiveData()

    fun getSignInModel(): MutableLiveData<SignInModel> {
        return signInMutableLiveData
    }

    fun onBackToLoginClicked() {
        (context as AppCompatActivity).onBackPressed()
    }

    fun onForgotPasswordClicked() {
        val model = SignInModel()
        model.email = email.get()
        signInMutableLiveData.value = model
    }

// For get User
    fun getUser() {
        isLoading.postValue(true)

        Networking.with(context)
            .getServices()
            .forgotPassword(email.get().toString())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CallbackObserver<BaseModel<User>>() {
                override fun onSuccess(response: BaseModel<User>) {
                    isLoading.postValue(false)
                    MessageDialog(context, response.message).show()
                }

                override fun onFailed(code: Int, message: String) {
                    isLoading.postValue(false)
                    MessageDialog(context, message).show()
                }
            })
    }

}