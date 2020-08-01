package com.niro.niroapp.viewmodels.factories

import androidx.lifecycle.ViewModel
import com.niro.niroapp.models.responsemodels.User
import com.niro.niroapp.viewmodels.OrdersViewModel
import com.niro.niroapp.viewmodels.PaymentsViewModel

class PaymentsViewModelFactory(private val currentUser : User?) : AbstractViewModelFactory<PaymentsViewModel>() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(User::class.java).newInstance(currentUser)
    }


}