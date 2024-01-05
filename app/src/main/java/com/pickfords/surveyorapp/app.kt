package com.pickfords.surveyorapp

import android.app.Application
import com.bugfender.android.BuildConfig
import com.bugfender.sdk.Bugfender

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        Bugfender.init(this,"kp3FK22QxfHBzujEuRJnLuoJvgWOenQT", BuildConfig.DEBUG)
        Bugfender.enableCrashReporting()
        Bugfender.enableUIEventLogging(this)
        Bugfender.enableLogcatLogging()
    }
}