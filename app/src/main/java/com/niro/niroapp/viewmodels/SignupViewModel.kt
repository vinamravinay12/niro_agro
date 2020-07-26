package com.niro.niroapp.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SignupViewModel(phoneNumber: String): ViewModel() {

    private val phoneNumber = MutableLiveData(phoneNumber)
    private val fullName = MutableLiveData<String>()
    private val businessName = MutableLiveData<String>()



}