package com.niro.niroapp.users.viewmodels.factories

import androidx.lifecycle.ViewModel
import com.niro.niroapp.models.responsemodels.Contact
import com.niro.niroapp.models.responsemodels.User
import com.niro.niroapp.users.viewmodels.ContactsViewModel
import com.niro.niroapp.users.viewmodels.UsersViewModel
import com.niro.niroapp.viewmodels.factories.AbstractViewModelFactory

class UsersViewModelFactory(private val currentUser : User?) : AbstractViewModelFactory<UsersViewModel>() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        return modelClass.getConstructor(User::class.java).newInstance(currentUser)
    }
}