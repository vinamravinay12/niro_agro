package com.niro.niroapp.loans.viewmodels.factories

import androidx.lifecycle.ViewModel
import com.niro.niroapp.loans.viewmodels.LoanRequirementViewModel
import com.niro.niroapp.models.responsemodels.User
import com.niro.niroapp.viewmodels.CreateOrderViewModel
import com.niro.niroapp.viewmodels.OrdersViewModel
import com.niro.niroapp.viewmodels.factories.AbstractViewModelFactory

class LoanRequirementViewModelFactory(private val currentUser : User?) : AbstractViewModelFactory<LoanRequirementViewModel>() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(User::class.java).newInstance(currentUser)
    }


}