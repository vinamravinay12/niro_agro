package com.niro.niroapp.viewmodels.factories

import androidx.lifecycle.ViewModel
import com.niro.niroapp.models.responsemodels.User
import com.niro.niroapp.viewmodels.EditProfileViewModel
import com.niro.niroapp.viewmodels.PaymentsViewModel

class EditProfileViewModelFactory(private val currentUser : User?) : AbstractViewModelFactory<EditProfileViewModel>() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(User::class.java).newInstance(currentUser)
    }


}