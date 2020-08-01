package com.niro.niroapp.viewmodels.factories

import androidx.lifecycle.ViewModel
import com.niro.niroapp.viewmodels.ProfileDetailsViewModel
import com.niro.niroapp.models.responsemodels.User

class ProfileDetailsViewModelFactory(private val currentUser : User?) : AbstractViewModelFactory<ProfileDetailsViewModel>() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(User::class.java).newInstance(currentUser)
    }


}