package com.niro.niroapp.users.viewmodels.factories

import androidx.lifecycle.ViewModel
import com.niro.niroapp.users.viewmodels.ContactsViewModel
import com.niro.niroapp.viewmodels.CommoditiesViewModel
import com.niro.niroapp.viewmodels.factories.AbstractViewModelFactory

class ContactsViewModelFactory : AbstractViewModelFactory<ContactsViewModel>() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T = ContactsViewModel() as T
}