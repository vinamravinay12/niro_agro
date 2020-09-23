package com.niro.niroapp.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import carbon.dialog.ProgressDialog
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.niro.niroapp.R
import com.niro.niroapp.databinding.LayoutLoginScreenBinding
import com.niro.niroapp.utils.NiroAppUtils
import com.niro.niroapp.viewmodels.LoginViewModel
import com.niro.niroapp.viewmodels.factories.LoginViewModelFactory
import com.niro.niroapp.utils.FragmentUtils
import com.niro.niroapp.utils.OnBackPressedListener
import java.util.concurrent.TimeUnit

class LoginFragment : Fragment(), OnBackPressedListener {


    private lateinit var bindingFragmentLogin : LayoutLoginScreenBinding
    private var loginViewModel : LoginViewModel? = null
    private var mProgressDialog : ProgressDialog? = null
    private var mBackPressCount = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        bindingFragmentLogin = DataBindingUtil.inflate(inflater,R.layout.layout_login_screen, container, false)
        loginViewModel = activity?.let { LoginViewModelFactory().getViewModel(null,it) }
        bindingFragmentLogin.lifecycleOwner = viewLifecycleOwner

        return bindingFragmentLogin.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeListeners()
    }

    private fun initializeListeners() {

        NiroAppUtils.setBackPressedCallback(requireActivity(),viewLifecycleOwner,this)
        bindingFragmentLogin.etPhoneNumber.setOtpCompletionListener {
            loginViewModel?.setMobileNumber(it)
            FragmentUtils.hideKeyboard(bindingFragmentLogin.root,context)
        }
        bindingFragmentLogin.btnSendOtp.setOnClickListener { sendOtp() }
    }

    private fun sendOtp(){
        if(loginViewModel?.getMobileNumber()?.value.isNullOrEmpty()) return

        mProgressDialog = NiroAppUtils.showLoaderProgress(getString(R.string.sending_otp),requireContext())

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
            if(mProgressDialog?.isShowing == true) mProgressDialog?.dismiss()
            loginViewModel?.getCredential()?.value = credential
        }

        override fun onVerificationFailed(p0: FirebaseException) {
            Log.e("TAG","verification failed " + p0.message)
            if(mProgressDialog?.isShowing == true) mProgressDialog?.dismiss()
           NiroAppUtils.showSnackbar(getString(R.string.otp_sent_failed),bindingFragmentLogin.root)
        }

        override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
            super.onCodeSent(verificationId,token)
            if(mProgressDialog?.isShowing == true) mProgressDialog?.dismiss()
            loginViewModel?.getStoredVerificiationId()?.value = verificationId
            loginViewModel?.getResendToken()?.value = token
            NiroAppUtils.showSnackbar(getString(R.string.otp_sent),bindingFragmentLogin.root)
           launchOtpFragment("")
        }

    }

    private fun launchOtpFragment(smsCode: String) {
        findNavController().navigate(R.id.action_loginFragment_to_OTPFragment, bundleOf(ARG_OTP to smsCode))

    }

    override fun onBackPressed() {

        if(mBackPressCount == 1) {
            NiroAppUtils.showToast(getString(R.string.press_again_to_exit),requireContext(),Toast.LENGTH_SHORT)
            mBackPressCount += 1
        }
        else {
            mBackPressCount == 1
            requireActivity().finish()
        }
    }

}


