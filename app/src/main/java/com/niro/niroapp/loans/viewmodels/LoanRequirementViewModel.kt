package com.niro.niroapp.loans.viewmodels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.niro.niroapp.loans.models.LoanRequestPostData
import com.niro.niroapp.loans.viewmodels.repositories.LoanRequestRepository
import com.niro.niroapp.models.APIResponse
import com.niro.niroapp.models.responsemodels.User

class LoanRequirementViewModel(private val currentUser : User?) : ViewModel() {


    private val currentUserData = MutableLiveData<User>()
    private val capitalRequired = MutableLiveData<String>()
    private val timeInMonths = MutableLiveData<String>()

    fun getCapitalRequired() = capitalRequired
    fun getTimeInMonths() = timeInMonths

    init {
        currentUserData.value = currentUser
    }

    fun createLoanRequirement(context : Context?) : MutableLiveData<APIResponse>{
        val loanRequestPostData = LoanRequestPostData(currentUserId = currentUserData.value?.id,
            capitalRequired = if(capitalRequired.value.isNullOrEmpty()) capitalRequired.value?.toDouble() ?: 0.0 else 0.0,
            timePeriod = timeInMonths.value?.toInt() ?: 0)

        return LoanRequestRepository(loanRequestPostData,context).getResponse()
    }

    fun resetAllFields() {
        capitalRequired.value = ""
        timeInMonths.value = ""
    }
}