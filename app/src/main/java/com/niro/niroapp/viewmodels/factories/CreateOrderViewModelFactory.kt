package com.niro.niroapp.viewmodels.factories

import androidx.lifecycle.ViewModel
import com.niro.niroapp.models.responsemodels.User
import com.niro.niroapp.viewmodels.CreateOrderViewModel
import com.niro.niroapp.viewmodels.LoginViewModel

class CreateOrderViewModelFactory(private val currentUserId : String?) : AbstractViewModelFactory<CreateOrderViewModel>() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(String::class.java).newInstance(currentUserId)
    }
}