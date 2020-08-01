package com.niro.niroapp.viewmodels.factories

import androidx.lifecycle.ViewModel
import com.niro.niroapp.viewmodels.CreateOrderViewModel
import com.niro.niroapp.viewmodels.LoginViewModel

class CreateOrderViewModelFactory : AbstractViewModelFactory<CreateOrderViewModel>() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CreateOrderViewModel() as T
    }
}