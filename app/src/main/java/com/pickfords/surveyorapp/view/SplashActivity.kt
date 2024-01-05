package com.pickfords.surveyorapp.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import androidx.core.app.ActivityOptionsCompat
import com.pickfords.surveyorapp.DashboardActivity
import com.pickfords.surveyorapp.R
import com.pickfords.surveyorapp.extentions.goToActivityAndClearTask
import com.pickfords.surveyorapp.utils.Utility
import com.pickfords.surveyorapp.view.auth.SignInActivity
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // for changing base URL
        Utility.changeBaseURL(0)
        findById()
        setDelay()
    }
     //  Find By Id0
    private fun findById() {
        ivAppLogo.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_fad_in))
    }
    // Set Delay for Splash
    private fun setDelay() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (!session.isLoggedIn) {
                val optionsCompat = ActivityOptionsCompat
                    .makeSceneTransitionAnimation(this, ivAppLogo, "appLogo")
                goToActivityAndClearTask<SignInActivity>(optionsCompat.toBundle())
            } else {
                //todo offline support
                //setSyncWorker()
                goToActivityAndClearTask<DashboardActivity>()
            }
        }, 2500)
    }
}
