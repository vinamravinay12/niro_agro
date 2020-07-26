package com.niro.niroapp.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.niro.niroapp.R
import com.niro.niroapp.databinding.LayoutLoginScreenBinding
import com.niro.niroapp.utils.NIroAppConstants
import com.niro.niroapp.utils.NiroAppUtils
import com.niro.niroapp.viewmodels.LoginViewModel
import com.niro.niroapp.viewmodels.factories.LoginViewModelFactory
import com.niro.niroapp.utils.FragmentUtils
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
        bindingFragmentLogin.etPhoneNumber.setOtpCompletionListener(){
            loginViewModel?.setMobileNumber(it)
            FragmentUtils.hideKeyboard(bindingFragmentLogin.root,context)
        }
        bindingFragmentLogin.btnSendOtp.setOnClickListener { sendOtp() }
    }

    private fun sendOtp(){
        if(loginViewModel?.getMobileNumber()?.value.isNullOrEmpty()) return
        activity?.let {activity ->
            loginViewModel?.getMobileNumber()?.value?.let { number ->

                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    "+91$number",60, TimeUnit.SECONDS,
                    activity,PhoneVerificationCallback())
            }
        }
    }


    inner class PhoneVerificationCallback : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
           if(!credential.smsCode.isNullOrEmpty()) {
                launchOtpFragment(credential.smsCode ?: "")
           }
        }

        override fun onVerificationFailed(p0: FirebaseException) {
            Log.e("TAG","verification failed " + p0.message)
           NiroAppUtils.showSnackbar(getString(R.string.otp_sent_failed),bindingFragmentLogin.root)
        }

        override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
            super.onCodeSent(verificationId,token)
            loginViewModel?.getStoredVerifciationId()?.value = verificationId
            loginViewModel?.getResendToken()?.value = token
            NiroAppUtils.showSnackbar(getString(R.string.otp_sent),bindingFragmentLogin.root)
        }

    }

    private fun launchOtpFragment(smsCode: String) {
        FragmentUtils.launchFragment(activity?.supportFragmentManager,R.id.fl_login,OTPFragment.newInstance(smsCode),NIroAppConstants.TAG_OTP)
    }




}