package com.niro.niroapp.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import carbon.dialog.ProgressDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.niro.niroapp.R
import com.niro.niroapp.activities.MainActivity
import com.niro.niroapp.database.SharedPreferenceManager
import com.niro.niroapp.databinding.LayoutEnterOtpScreenBinding
import com.niro.niroapp.models.APIError
import com.niro.niroapp.models.APILoader
import com.niro.niroapp.models.Success
import com.niro.niroapp.models.responsemodels.LoginResponse
import com.niro.niroapp.utils.NIroAppConstants
import com.niro.niroapp.utils.NiroAppUtils
import com.niro.niroapp.viewmodels.LoginViewModel
import com.niro.niroapp.viewmodels.factories.LoginViewModelFactory
import com.niro.niroapp.utils.FragmentUtils


private const val ARG_OTP = "ArgOtp"

class OTPFragment : Fragment() {

    private var otp: String? = null
    private lateinit var bindingOtpFragment : LayoutEnterOtpScreenBinding
    private var loginViewModel : LoginViewModel? = null

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
        loginViewModel = activity?.let { LoginViewModelFactory().getViewModel(it) }
        bindingOtpFragment.lifecycleOwner = viewLifecycleOwner

        return bindingOtpFragment.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindingOtpFragment.etOtp.setOtpCompletionListener { otpEntered ->
            FragmentUtils.hideKeyboard(bindingOtpFragment.root,context)
            verifyOtp(otpEntered)
         }
    }

    private fun verifyOtp(otpEntered: String?) {
        val credential = loginViewModel?.getStoredVerifciationId()?.value?.let {verificationId ->
            otpEntered?.let { otp ->
                PhoneAuthProvider.getCredential(
                    verificationId, otp
                )
            }
        }

        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential?) {
        if (credential != null) {
            FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener {task ->
                if(task.isSuccessful) {
                    loginUser()
                }
                else NiroAppUtils.showSnackbar(getString(R.string.invalid_otp),bindingOtpFragment.root)
            }
        }
    }

    private fun loginUser() {
         var progressDialog : ProgressDialog? = null
        loginViewModel?.loginUser()?.observe(viewLifecycleOwner, Observer { response ->
            when(response) {
                is APILoader -> {
                    progressDialog = context?.let {context ->
                        NiroAppUtils.showLoaderProgress(getString(R.string.logging_in), context)
                    }
                }
                is APIError -> {
                    progressDialog?.dismiss()
                    if(response.errorCode == 422) launchSignUpFragment(loginViewModel?.getMobileNumber()?.value) else NiroAppUtils.showSnackbar(response.errorMessage,bindingOtpFragment.root)
                }

                is Success<*> -> {
                    progressDialog?.dismiss()
                    saveUserData(response.data as? LoginResponse)
                }

            }

        })
    }

    private fun saveUserData(loginResponse: LoginResponse?) {
        context?.let {
            val sharedPreferenceManager = SharedPreferenceManager(it,NIroAppConstants.LOGIN_SP)
            sharedPreferenceManager.storeStringPreference(NIroAppConstants.USER_TOKEN,loginResponse?.token ?: "")
            sharedPreferenceManager.storeComplexObjectPreference(NIroAppConstants.USER_DATA,loginResponse?.data)
        }

        launchMainActivity()
    }

    private fun launchMainActivity() {
        startActivity(Intent(activity,MainActivity::class.java))
        activity?.finish()
    }

    private fun launchSignUpFragment(phoneNumber: String?) {
        FragmentUtils.launchFragment(activity?.supportFragmentManager,R.id.fl_login,
            phoneNumber?.let { EnterNameFragment.newInstance(it) },NIroAppConstants.TAG_ENTER_NAME)

    }

    companion object {

        @JvmStatic
        fun newInstance(otp: String) =
            OTPFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_OTP, otp)
                }
            }
    }
}