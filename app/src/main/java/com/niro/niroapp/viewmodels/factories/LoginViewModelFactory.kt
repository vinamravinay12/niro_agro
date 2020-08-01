package com.niro.niroapp.viewmodels.factories

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import com.niro.niroapp.viewmodels.LoginViewModel
import java.lang.IllegalArgumentException

class LoginViewModelFactory : AbstractViewModelFactory<LoginViewModel>() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LoginViewModel() as T
    }
}