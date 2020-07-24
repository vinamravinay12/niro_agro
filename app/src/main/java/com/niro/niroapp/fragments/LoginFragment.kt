package com.niro.niroapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.niro.niroapp.R
import com.niro.niroapp.databinding.LayoutLoginScreenBinding
import com.niro.niroapp.viewmodels.LoginViewModel
import com.niro.niroapp.viewmodels.factories.LoginViewModelFactory
import java.util.concurrent.TimeUnit

class LoginFragment : Fragment() {


    private lateinit var bindingFragmentLogin : LayoutLoginScreenBinding
    private var loginViewModel : LoginViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        bindingFragmentLogin = DataBindingUtil.inflate(inflater,R.layout.layout_login_screen, container, false)
        loginViewModel = activity?.let { LoginViewModelFactory().getViewModel(this) }
        bindingFragmentLogin.lifecycleOwner = viewLifecycleOwner
        return bindingFragmentLogin.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeListeners()
    }

    private fun initializeListeners() {
        bindingFragmentLogin.etPhoneNumber.setOtpCompletionListener(){loginViewModel?.setMobileNumber(it)}
        bindingFragmentLogin.btnSendOtp.setOnClickListener { checkIfUserExists() }
    }

    private fun checkIfUserExists() {

    }




}