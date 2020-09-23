package com.niro.niroapp.viewmodels.factories

import androidx.lifecycle.ViewModel
import com.niro.niroapp.viewmodels.BuyCommoditiesViewModel

class SellerCommodityViewModelFactory(private val userId : String?, private val userType : String?) : AbstractViewModelFactory<BuyCommoditiesViewModel>() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return BuyCommoditiesViewModel(userId,userType) as T
    }


}