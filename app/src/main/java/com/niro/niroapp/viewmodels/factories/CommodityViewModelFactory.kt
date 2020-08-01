package com.niro.niroapp.viewmodels.factories

import androidx.lifecycle.ViewModel
import com.niro.niroapp.viewmodels.CommoditiesViewModel
import com.niro.niroapp.viewmodels.LoginViewModel

class CommodityViewModelFactory : AbstractViewModelFactory<CommoditiesViewModel>() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T = CommoditiesViewModel() as T
}