package com.niro.niroapp.activities

import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.view.View
import android.widget.TextView
import com.google.firebase.FirebaseApp
import com.niro.niroapp.R
import com.niro.niroapp.database.DatabaseKeys
import com.niro.niroapp.database.SharedPreferenceManager
import com.niro.niroapp.firebase.FirebaseTokenGenerator

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        FirebaseApp.initializeApp(this)
        animateAppNameText(findViewById<TextView>(R.id.tvAppNameText))
        animateAppThemeText(findViewById<TextView>(R.id.tvAppThemeText))

        refreshIdToken()
        checkIfUserLoggedInAndLaunch()
    }

    private fun refreshIdToken() {
        FirebaseTokenGenerator(null).generateIdToken(this)
    }

    private fun getWidthOfScreen() : Float {
        val displayMetrics = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.widthPixels.toFloat()
    }

    private fun animateAppThemeText(view: TextView) {
        ObjectAnimator.ofFloat(view, "translationX",getWidthOfScreen()/2).apply {
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
            if (!isLoggedIn) launchLoginScreen() else launchHomeScreen()
        }, 3000)

    }

    private fun launchHomeScreen() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun launchLoginScreen() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }


}