package com.niro.niroapp.models.postdatamodels

data class SubmitRatingsPostData(val currentUserId : String?, val selectedContactId : String?,val ratings : Float? = 0f, val contactType : String?)