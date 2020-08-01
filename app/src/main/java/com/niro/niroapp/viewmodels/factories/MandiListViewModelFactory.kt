package com.niro.niroapp.viewmodels.factories

import androidx.lifecycle.ViewModel
import com.niro.niroapp.viewmodels.LoginViewModel
import com.niro.niroapp.viewmodels.MandiListViewModel

class MandiListViewModelFactory : AbstractViewModelFactory<MandiListViewModel>() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T = LoginViewModel() as T
}