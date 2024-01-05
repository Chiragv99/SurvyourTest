package com.pickfords.surveyorapp.viewModel.auth

import android.content.Context
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.pickfords.surveyorapp.BuildConfig
import com.pickfords.surveyorapp.R
import com.pickfords.surveyorapp.base.BaseViewModel
import com.pickfords.surveyorapp.extentions.goToActivity
import com.pickfords.surveyorapp.model.auth.SignInModel
import com.pickfords.surveyorapp.model.auth.User
import com.pickfords.surveyorapp.network.BaseModel
import com.pickfords.surveyorapp.network.CallbackObserver
import com.pickfords.surveyorapp.network.Networking
import com.pickfords.surveyorapp.utils.Session.Companion.DEVICE_TYPE
import com.pickfords.surveyorapp.utils.Utility
import com.pickfords.surveyorapp.view.auth.ForgotPasswordActivity
import com.pickfords.surveyorapp.view.auth.SignInActivity
import com.pickfords.surveyorapp.view.dialogs.MessageDialog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class SignInViewModel(val context: Context) : BaseViewModel() {
    var email: ObservableField<String> = ObservableField()
    var password: ObservableField<String> = ObservableField()

    var userLiveData: MutableLiveData<User> = MutableLiveData()
    private var signInMutableLiveData: MutableLiveData<SignInModel> = MutableLiveData()

    fun getSignInModel(): MutableLiveData<SignInModel> {
        return signInMutableLiveData
    }

    fun onForgotPasswordClicked() {
        val optionsCompat = ActivityOptionsCompat
            .makeSceneTransitionAnimation(
                (context as SignInActivity),
                context.binding.ivLogo,
                "appLogo"
            )

        (context as AppCompatActivity).goToActivity<ForgotPasswordActivity>(optionsCompat.toBundle())
    }

    fun onSignInClicked() {
        val model = SignInModel()
        model.email = email.get()
        model.password = password.get()
        signInMutableLiveData.value = model
    }

//  For current User
    fun getUser() {
        val androidId =
            Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)

        val params = HashMap<String, Any>()
        params["EmailAddress"] = email.get().toString()
        params["Password"] = password.get().toString()
        params["DeviceUniqueId"] = androidId
        params["DeviceType"] = DEVICE_TYPE
        params["DeviceOS"] = Build.VERSION.SDK_INT.toString()
        params["DeviceModel"] = getDeviceName().toString()
        params["AppVersion"] = BuildConfig.VERSION_NAME

        if (Utility.isNetworkConnected(context)){
            isLoading.postValue(true)
            Networking.with(context)
                .getServices()
                .login(Networking.wrapParams(params))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CallbackObserver<BaseModel<User>>() {
                    override fun onSuccess(response: BaseModel<User>) {
                        isLoading.postValue(false)
                        if (response.success) {
                            userLiveData.postValue(response.data)
                        }
                        else
                            MessageDialog(context, response.message).show()
                    }

                    override fun onFailed(code: Int, message: String) {
                        isLoading.postValue(false)
                        MessageDialog(context, message).show()
                    }
                })
        }else{
            MessageDialog(context, context.resources.getString(R.string.nointernetconnection)).show()
        }
//            })
    }

    /** Returns the consumer friendly device name  */
    private fun getDeviceName(): String? {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        return if (model.startsWith(manufacturer)) {
            capitalize(model)
        } else capitalize(manufacturer) + " " + model
    }

    private fun capitalize(str: String): String {
        if (TextUtils.isEmpty(str)) {
            return str
        }
        val arr = str.toCharArray()
        var capitalizeNext = true
        val phrase = StringBuilder()
        for (c in arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(Character.toUpperCase(c))
                capitalizeNext = false
                continue
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true
            }
            phrase.append(c)
        }
        return phrase.toString()
    }

}