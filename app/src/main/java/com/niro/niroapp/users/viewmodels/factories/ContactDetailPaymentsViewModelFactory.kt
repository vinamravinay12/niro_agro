package com.niro.niroapp.users.viewmodels.factories

import androidx.lifecycle.ViewModel
import com.niro.niroapp.models.responsemodels.User
import com.niro.niroapp.users.viewmodels.ContactDetailPaymentsViewModel
import com.niro.niroapp.viewmodels.PaymentsViewModel
import com.niro.niroapp.viewmodels.factories.AbstractViewModelFactory

class ContactDetailPaymentsViewModelFactory(private val currentUser : User?) : AbstractViewModelFactory<ContactDetailPaymentsViewModel>() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(User::class.java).newInstance(currentUser)
    }


}