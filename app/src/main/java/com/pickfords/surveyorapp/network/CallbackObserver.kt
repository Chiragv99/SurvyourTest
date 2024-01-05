package com.pickfords.surveyorapp.network

import android.os.StrictMode
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.reactivex.observers.DisposableObserver
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.HttpException
import java.net.InetAddress

abstract class CallbackObserver<T> : DisposableObserver<T>() {

    abstract fun onSuccess(response: T)
    abstract fun onFailed(code: Int, message: String)

    override fun onComplete() {
        //Nothing happen here
    }

    override fun onNext(t: T) {
        onSuccess(t)
    }

    override fun onError(e: Throwable) {
        FirebaseCrashlytics.getInstance().log("CallbackObserver -> onError -> ${e.message}")
        FirebaseCrashlytics.getInstance().recordException(e)
        try {
            if (!isInternetAvailable()) {
                onFailed(0, "No Internet connection")
            } else if (e is HttpException) {
                val message = getErrorMessage(e.response()?.errorBody())
                FirebaseCrashlytics.getInstance().log("CallbackObserver -> HttpException -> $message")
                FirebaseCrashlytics.getInstance().recordException(e)
                onFailed(e.code(), message)
            } else {
                onFailed(
                    0,
                    if (e.localizedMessage == null) "Something went wrong" else e.localizedMessage
                )
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().log("CallbackObserver -> catch -> ${e.message}")
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    private fun getErrorMessage(responseBody: ResponseBody?): String {
        return try {
            val jsonObject = JSONObject(responseBody!!.string())
            jsonObject.getString("msg")
        } catch (e: Exception) {
            e.message.toString()
        }
    }

    private fun isInternetAvailable(): Boolean {
        return try {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
            val ipAddr = InetAddress.getByName("google.com")
            //You can replace it with your name
            !ipAddr.equals("")
        } catch (e: Exception) {
            false
        }
    }
}