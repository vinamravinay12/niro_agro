package com.niro.niroapp.models.postdatamodels

import com.niro.niroapp.models.responsemodels.UserContact

data class CreatePaymentPostData(private val currentUserId : String?, private val selectedContact : UserContact?,
                                 private val paymentMode : String, private val paymentAmount : Double, private val paymentDate : String)