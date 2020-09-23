package com.niro.niroapp.viewmodels.factories

import androidx.lifecycle.ViewModel
import com.niro.niroapp.models.responsemodels.UserContact
import com.niro.niroapp.viewmodels.CreateOrderViewModel
import com.niro.niroapp.viewmodels.CreatePaymentsViewModel

class CreatePaymentsViewModelFactory constructor(
    private val currentUserId: String?, private val selectedContactId: String?,
    private val selectedContactType: String?
) : AbstractViewModelFactory<CreatePaymentsViewModel>() {


    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CreatePaymentsViewModel(currentUserId, selectedContactId,selectedContactType) as T
    }
}