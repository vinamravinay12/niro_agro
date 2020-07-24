package com.niro.niroapp.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

class LoginViewModel : ViewModel() {

    private val mobileNumber = MutableLiveData<String>()


    fun getMobileNumber() : MutableLiveData<String> = mobileNumber

    fun setMobileNumber(mobileNumber : String) {
        this.mobileNumber.value = mobileNumber
    }


}