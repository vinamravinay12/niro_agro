package com.niro.niroapp.viewmodels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.niro.niroapp.models.APIResponse
import com.niro.niroapp.models.responsemodels.LoginResponse
import com.niro.niroapp.network.ApiClient
import com.niro.niroapp.viewmodels.repositories.LoginRepository

class LoginViewModel : ViewModel() {

    private val mobileNumber = MutableLiveData<String>()
    private val storedVerificationId = MutableLiveData<String>()
    private val resendToken = MutableLiveData<PhoneAuthProvider.ForceResendingToken>()
    private val otpExpiryTime = MutableLiveData<String>()
    private val maxOtpRetry = MutableLiveData(3)
    private val credential : MutableLiveData<PhoneAuthCredential> = MutableLiveData()


    fun getCredential() = credential

    fun getMobileNumber() : MutableLiveData<String> = mobileNumber

    fun setMobileNumber(mobileNumber : String) {
        this.mobileNumber.value = mobileNumber
    }

    fun getStoredVerificiationId() = storedVerificationId
    fun getResendToken() = resendToken

    fun getOtpExpiryTime() = otpExpiryTime


    fun loginUser(context : Context?) : MutableLiveData<APIResponse>? {

        val loginRepository = mobileNumber.value?.let { LoginRepository<LoginResponse>(it,context) }
        return loginRepository?.getResponse()

    }

    fun getMaxOtpRetry() = maxOtpRetry
    fun resetOtpValues() {
        otpExpiryTime.value = ""
        maxOtpRetry.value = 0
    }

}