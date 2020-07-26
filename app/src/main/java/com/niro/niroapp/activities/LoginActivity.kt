package com.niro.niroapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.FirebaseApp
import com.niro.niroapp.R
import com.niro.niroapp.fragments.LoginFragment
import com.niro.niroapp.utils.FragmentUtils
import com.niro.niroapp.utils.NIroAppConstants

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        launchLoginFragment()
    }

    private fun launchLoginFragment() {
        FragmentUtils.launchFragment(supportFragmentManager,R.id.fl_login,LoginFragment(),NIroAppConstants.TAG_LOGIN)
    }
}