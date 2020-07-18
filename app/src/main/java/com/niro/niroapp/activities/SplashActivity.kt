package com.niro.niroapp.activities

import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.TextView
import com.niro.niroapp.R
import com.niro.niroapp.database.DatabaseKeys
import com.niro.niroapp.utils.NiroAppUtils
import com.translabtechnologies.visitormanagementsystem.vmshost.database.SharedPreferenceManager

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        animateAppNameText(findViewById<TextView>(R.id.tvAppNameText))
        animateAppThemeText(findViewById<TextView>(R.id.tvAppThemeText))
        checkIfUserLoggedInAndLaunch()
    }

    private fun animateAppThemeText(view: TextView) {
        ObjectAnimator.ofFloat(view, "translationX", NiroAppUtils.getPxFromDP(this, -350f)).apply {
            duration = 1500
            startDelay = 800
            start()
        }
    }

    private fun animateAppNameText(view: TextView) {

        ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f).apply {
            duration = 1200
            start()
        }
    }

    private fun checkIfUserLoggedInAndLaunch() {
        Handler().postDelayed({
            val isLoggedIn =
                SharedPreferenceManager(this, DatabaseKeys.LOGIN_SP).getBooleanPreference(
                    DatabaseKeys.KEY_LOGGED_IN,
                    false
                )
            if (!isLoggedIn) launchLoginScreen()
        }, 3000)

    }

    private fun launchLoginScreen() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }


}