package com.niro.niroapp.viewmodels.factories

import androidx.lifecycle.ViewModel
import com.niro.niroapp.viewmodels.CreateOrderViewModel
import com.niro.niroapp.viewmodels.CreatePaymentsViewModel

class CreatePaymentsViewModelFactory: AbstractViewModelFactory<CreatePaymentsViewModel>() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CreatePaymentsViewModel() as T
    }
}