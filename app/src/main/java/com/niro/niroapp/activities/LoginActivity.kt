package com.niro.niroapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import com.niro.niroapp.R

class LoginActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        launchLoginFragment()
    }

    private fun launchLoginFragment() {
        findNavController(R.id.login_host_fragment).navigate(R.id.loginFragment)
    }

}