package com.niro.niroapp.viewmodels

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

    fun getMobileNumber() : MutableLiveData<String> = mobileNumber

    fun setMobileNumber(mobileNumber : String) {
        this.mobileNumber.value = mobileNumber
    }

    fun getStoredVerifciationId() = storedVerificationId
    fun getResendToken() = resendToken


    fun loginUser() : MutableLiveData<APIResponse>? {

        val loginRepository = mobileNumber.value?.let { LoginRepository<LoginResponse>(it) }
        return loginRepository?.getResponse(ApiClient.getAPIInterface())

    }

}