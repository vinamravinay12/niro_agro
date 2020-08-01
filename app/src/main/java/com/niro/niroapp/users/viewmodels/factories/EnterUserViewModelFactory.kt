package com.niro.niroapp.users.viewmodels.factories

import androidx.lifecycle.ViewModel
import com.niro.niroapp.models.responsemodels.Contact
import com.niro.niroapp.users.viewmodels.ContactsViewModel
import com.niro.niroapp.users.viewmodels.EnterUserViewModel
import com.niro.niroapp.viewmodels.SignupViewModel
import com.niro.niroapp.viewmodels.factories.AbstractViewModelFactory

class EnterUserViewModelFactory(private val contact : Contact?) : AbstractViewModelFactory<EnterUserViewModel>() {


    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        return modelClass.getConstructor(Contact::class.java).newInstance(contact)
    }
}
