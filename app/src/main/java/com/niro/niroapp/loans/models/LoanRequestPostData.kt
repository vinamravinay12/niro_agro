package com.niro.niroapp.loans.models

data class LoanRequestPostData(
    val currentUserId : String?,
    val capitalRequired : Double = 0.0,
    val timePeriod : Int = 0
)