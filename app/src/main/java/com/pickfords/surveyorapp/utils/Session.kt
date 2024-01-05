package com.pickfords.surveyorapp.utils

import android.app.NotificationManager
import android.content.Context
import com.google.gson.Gson
import com.pickfords.surveyorapp.R
import com.pickfords.surveyorapp.model.auth.User

class Session(val context: Context) {
    private val pref = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)

    var isLoggedIn: Boolean
        get() = pref.contains(KEY_IS_LOGIN) && pref.getBoolean(KEY_IS_LOGIN, false)
        set(isLoggedIn) = storeDataByKey(KEY_IS_LOGIN, isLoggedIn)

    var user: User?
        get() {
            val gson = Gson()
            val json = getDataByKey(KEY_USER_INFO, "")
            return gson.fromJson(json, User::class.java)
        }
        set(user) {
            val gson = Gson()
            val json = gson.toJson(user)
            pref.edit().putString(KEY_USER_INFO, json).apply()
            isLoggedIn = true
        }

    @JvmOverloads
    fun getDataByKey(Key: String, DefaultValue: String = ""): String {
        return if (pref.contains(Key)) {
            pref.getString(Key, DefaultValue).toString()
        } else {
            DefaultValue
        }
    }

    @JvmOverloads
    fun getDataByKey(Key: String, DefaultValue: Boolean = false): Boolean {
        return if (pref.contains(Key)) {
            pref.getBoolean(Key, DefaultValue)
        } else {
            DefaultValue
        }
    }

    fun storeDataByKey(key: String, Value: String) {
        pref.edit().putString(key, Value).apply()
    }

    fun storeDataByKey(key: String, Value: Boolean) {
        pref.edit().putBoolean(key, Value).apply()
    }


    fun storeIsFirstTimeKey(Value: Boolean) {
        pref.edit().putBoolean(FIRST_TIME, Value).apply()
    }
    fun getIsFirstTimeKey() : Boolean{
      return  pref.getBoolean(FIRST_TIME,true)
    }

    operator fun contains(key: String): Boolean {
        return pref.contains(key)
    }

    fun remove(key: String) {
        pref.edit().remove(key).apply()
    }

    fun clearSession() {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()

        pref.edit().remove(KEY_IS_LOGIN).apply()
        pref.edit().remove(FIRST_TIME).apply()
    }


    companion object {
        private const val KEY_IS_LOGIN = "isLogin"
        private const val KEY_USER_INFO = "user"
        const val KEY_USER_EMAIL = "email"
        const val KEY_USER_PASSWORD = "password"
        const val KEY_USER_NAME  = "name"
        const val KEY_USER_REMEMBER = "isRemember"
        const val DEVICE_TYPE = "A"
        const val DATA = "data"
        const val MeasurementPreference = "MeasurementPreference"
        const val INVENTORY = "inventory"
        const val FIRST_TIME = "first_time"
    }
}