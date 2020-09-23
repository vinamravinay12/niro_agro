package com.niro.niroapp.fragments

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import carbon.dialog.ProgressDialog
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.niro.niroapp.R
import com.niro.niroapp.activities.MainActivity
import com.niro.niroapp.database.DatabaseKeys
import com.niro.niroapp.database.SharedPreferenceManager
import com.niro.niroapp.databinding.LayoutEnterOtpScreenBinding
import com.niro.niroapp.firebase.FirebaseTokenGeneratedDelegate
import com.niro.niroapp.firebase.FirebaseTokenGenerator
import com.niro.niroapp.models.APIError
import com.niro.niroapp.models.Success
import com.niro.niroapp.models.responsemodels.LoginResponse
import com.niro.niroapp.utils.FragmentUtils
import com.niro.niroapp.utils.NiroAppConstants
import com.niro.niroapp.utils.NiroAppUtils
import com.niro.niroapp.viewmodels.LoginViewModel
import com.niro.niroapp.viewmodels.factories.LoginViewModelFactory
import java.util.concurrent.TimeUnit


const val ARG_OTP = "ArgOtp"

class OTPFragment : AbstractBaseFragment(), FirebaseTokenGeneratedDelegate {

    private var otp: String? = null
    private lateinit var bindingOtpFragment : LayoutEnterOtpScreenBinding
    private var enteredOtp : String = ""
    private var loginViewModel : LoginViewModel? = null
    private var mProgressDialog : ProgressDialog? = null
    private lateinit var mAuth : FirebaseAuth



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            otp = it.getString(ARG_OTP)

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        bindingOtpFragment = DataBindingUtil.inflate(inflater,R.layout.layout_enter_otp_screen, container, false)
        loginViewModel = activity?.let { LoginViewModelFactory().getViewModel(null,it) }

        mAuth = FirebaseAuth.getInstance()
        bindingOtpFragment.lifecycleOwner = viewLifecycleOwner

        return bindingOtpFragment.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindingOtpFragment.etOtp.setOtpCompletionListener { otpEntered ->
            FragmentUtils.hideKeyboard(bindingOtpFragment.root,context)
            enteredOtp = otpEntered

         }

        startCountdownTimer()

        initializeListeners()

    }


    override fun onStart() {
        super.onStart()

        loginViewModel?.getMaxOtpRetry()?.value = 3
    }

    private fun initializeListeners() {

        super.registerBackPressedCallback(R.id.loginFragment)

        bindingOtpFragment.btnVerifyNumber.setOnClickListener { verifyOtp(enteredOtp) }

        bindingOtpFragment.tvResendOtpLink.setOnClickListener { resendOtp() }
    }

    private fun resendOtp() {
        if(loginViewModel?.getMaxOtpRetry()?.value ?: 0 <= 0) {
            NiroAppUtils.showSnackbar(getString(R.string.max_otp_resend_message),bindingOtpFragment.root)
            return
        }

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            "+91${loginViewModel?.getMobileNumber()?.value}",
            60,
            TimeUnit.SECONDS,
            requireActivity(),
            OtpResendCallback(),
            loginViewModel?.getResendToken()?.value);
    }


    private fun verifyOtp(otpEntered: String?) {
        if(otpEntered.isNullOrEmpty() || otpEntered.length < 6) return
        val credential = loginViewModel?.getStoredVerificiationId()?.value?.let {verificationId ->
            otpEntered.let { otp ->
                PhoneAuthProvider.getCredential(
                    verificationId, otp
                )
            }
        }

        mProgressDialog = context?.let {context ->
            NiroAppUtils.showLoaderProgress(getString(R.string.logging_in), context)
        }
        signInWithPhoneAuthCredential(credential)
    }


    private fun startCountdownTimer() {
        bindingOtpFragment.tvResendOtpLink.isEnabled = false
        bindingOtpFragment.tvResendOtpLink.alpha = 0.54f
        ResendOtpCountDownTimer(TimeUnit.SECONDS.toMillis(30),1000).start()
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential?) {
        if (credential != null) {
            mAuth.signInWithCredential(credential).addOnCompleteListener {task ->
                if(task.isSuccessful) {
                    loginUser()
                }
                else { if(mProgressDialog != null && mProgressDialog?.isShowing == true) mProgressDialog?.dismiss()
                    NiroAppUtils.showSnackbar(getString(R.string.invalid_otp),bindingOtpFragment.root)
                }
            }
        }
    }

    private fun loginUser() {

        loginViewModel?.loginUser(context)?.observe(viewLifecycleOwner, Observer { response ->
            when(response) {

                is APIError -> {
                    if(mProgressDialog != null && mProgressDialog?.isShowing == true) mProgressDialog?.dismiss()
                    if(response.errorCode == 422) launchSignUpFragment(loginViewModel?.getMobileNumber()?.value) else NiroAppUtils.showSnackbar(response.errorMessage,bindingOtpFragment.root)
                }

                is Success<*> -> {
                    signInWithCustomToken(response.data as? LoginResponse)
                }

            }

        })
    }

    private fun signInWithCustomToken(loginResponse: LoginResponse?) {

        loginResponse?.token?.let { activity?.let { activity ->
            mAuth.signInWithCustomToken(it).addOnCompleteListener(
                activity) { task ->
                    if(task.isSuccessful) {
                        FirebaseTokenGenerator(this).generateIdToken(activity)
                        saveUserData(loginResponse)
                    }

                    else {
                        if(mProgressDialog != null && mProgressDialog?.isShowing == true)  mProgressDialog?.dismiss()
                        NiroAppUtils.showSnackbar(getString(R.string.login_failed),bindingOtpFragment.root)
                    }
                }

        } }

    }

    private fun saveUserData(loginResponse: LoginResponse) {
        context?.let {
            val sharedPreferenceManager = SharedPreferenceManager(it,NiroAppConstants.LOGIN_SP)
            sharedPreferenceManager.storeComplexObjectPreference(NiroAppConstants.USER_DATA,loginResponse.data)

        }

    }

    private fun launchMainActivity() {
        startActivity(Intent(activity,MainActivity::class.java))
        activity?.finish()
    }

    private fun launchSignUpFragment(phoneNumber: String?) {
        findNavController().navigate(R.id.action_OTPFragment_to_enterNameFragment, bundleOf(
            ARG_MOBILE_NUMBER to phoneNumber))

    }

    companion object {

        @JvmStatic
        fun newInstance() = OTPFragment()
    }

    override fun onTokenGenerated(isSuccess: Boolean) {
        if(mProgressDialog != null && mProgressDialog?.isShowing == true) mProgressDialog?.dismiss()
        if(isSuccess) {
            val sharedPreferenceManager = context?.let { SharedPreferenceManager(it,NiroAppConstants.LOGIN_SP) }
            sharedPreferenceManager?.storeBooleanPreference(DatabaseKeys.KEY_LOGGED_IN,true)
            launchMainActivity()
        }

        else NiroAppUtils.showSnackbar(getString(R.string.login_failed),bindingOtpFragment.root)
    }


    inner class ResendOtpCountDownTimer(private val timeDifference: Long, private val interval: Long) : CountDownTimer(timeDifference, interval) {

        override fun onFinish() {
            bindingOtpFragment.tvResendOtpLink.isEnabled = true
            bindingOtpFragment.tvResendOtpLink.alpha = 1.0f

        }

        override fun onTick(millisUntilFinished: Long) {

            loginViewModel?.getOtpExpiryTime()?.value = "" + String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
                    TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)))

            bindingOtpFragment.tvOtpExpireTime.text = loginViewModel?.getOtpExpiryTime()?.value ?: ""


        }

    }



    inner class OtpResendCallback : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            if(!credential.smsCode.isNullOrEmpty()) {
                // launchOtpFragment(credential.smsCode ?: "")
            }
        }

        override fun onVerificationFailed(p0: FirebaseException) {
            Log.e("TAG","verification failed " + p0.message)
            if(mProgressDialog != null && mProgressDialog?.isShowing == true) mProgressDialog?.dismiss()
            NiroAppUtils.showSnackbar(getString(R.string.otp_sent_failed),bindingOtpFragment.root)
        }

        override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
            super.onCodeSent(verificationId,token)
            if(mProgressDialog != null && mProgressDialog?.isShowing == true) mProgressDialog?.dismiss()
            loginViewModel?.getStoredVerificiationId()?.value = verificationId
            loginViewModel?.getResendToken()?.value = token
            NiroAppUtils.showSnackbar(getString(R.string.otp_sent),bindingOtpFragment.root)
            startCountdownTimer()
            loginViewModel?.getMaxOtpRetry()?.value = (loginViewModel?.getMaxOtpRetry()?.value  ?: 3) - 1

        }

    }

    override fun onStop() {
        super.onStop()
        loginViewModel?.resetOtpValues()
    }

}