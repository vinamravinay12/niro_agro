package com.niro.niroapp.users.viewmodels.factories

import androidx.lifecycle.ViewModel
import com.niro.niroapp.models.responsemodels.User
import com.niro.niroapp.users.viewmodels.ContactDetailOrdersViewModel
import com.niro.niroapp.viewmodels.OrdersViewModel
import com.niro.niroapp.viewmodels.factories.AbstractViewModelFactory

class ContactDetailOrderViewModelFactory(private val currentUser : User?) : AbstractViewModelFactory<ContactDetailOrdersViewModel>() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(User::class.java).newInstance(currentUser)
    }


}