package com.niro.niroapp.viewmodels.factories

import androidx.lifecycle.ViewModel
import com.niro.niroapp.models.responsemodels.User
import com.niro.niroapp.viewmodels.CreatePaymentsViewModel
import com.niro.niroapp.viewmodels.CreateSellOrderViewModel

class CreateSellOrderViewModelFactory constructor(
    private val currentUser: User?
) : AbstractViewModelFactory<CreatePaymentsViewModel>() {


    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CreateSellOrderViewModel(currentUser = currentUser ) as T
    }
}