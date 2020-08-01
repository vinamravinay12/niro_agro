package com.niro.niroapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import com.google.firebase.FirebaseApp
import com.niro.niroapp.R
import com.niro.niroapp.fragments.LoginFragment
import com.niro.niroapp.utils.FragmentUtils
import com.niro.niroapp.utils.NIroAppConstants

class LoginActivity : AppCompatActivity() {

    private lateinit var childFrameLayout: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        childFrameLayout = findViewById(R.id.fl_login_child)
        launchLoginFragment()
    }

    private fun launchLoginFragment() {
        FragmentUtils.launchFragment(supportFragmentManager,R.id.fl_login,LoginFragment(),NIroAppConstants.TAG_LOGIN)
    }

    fun hideChildFrameLayout(toHide: Boolean) {
        childFrameLayout.visibility = if(toHide) View.GONE else View.VISIBLE
    }
}