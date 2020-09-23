package com.niro.niroapp.models.postdatamodels

import com.niro.niroapp.models.responsemodels.UserContact

data class CreatePaymentPostData(private val currentUserId : String?, private val selectedContactId : String?,
                                 private val paymentMode : String, private val paymentAmount : Double,
                                 private val paymentDate : String, private val contactType : String)