package com.niro.niroapp.viewmodels.factories


import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner

abstract class AbstractViewModelFactory<T : ViewModel>() : ViewModelProvider.Factory {

    inline fun <reified T: ViewModel> getViewModel( args : Any?, owner: ViewModelStoreOwner) : T {
        if(args == null) {
            return ViewModelProvider(owner).get(T::class.java)
        }

        return ViewModelProvider(owner,this).get(T::class.java)

    }
}