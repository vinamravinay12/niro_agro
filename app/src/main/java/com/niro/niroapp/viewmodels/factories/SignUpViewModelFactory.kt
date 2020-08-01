package com.niro.niroapp.viewmodels.factories

import androidx.lifecycle.ViewModel
import com.niro.niroapp.viewmodels.LoginViewModel
import com.niro.niroapp.viewmodels.SignupViewModel

class SignUpViewModelFactory(private val phoneNumber : String?) : AbstractViewModelFactory<SignupViewModel>() {


    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        return modelClass.getConstructor(String::class.java).newInstance(phoneNumber)
    }
}