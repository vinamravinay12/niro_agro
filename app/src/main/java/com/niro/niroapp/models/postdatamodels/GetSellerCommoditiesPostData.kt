package com.niro.niroapp.models.postdatamodels

data class GetSellerCommoditiesPostData(val userId : String?, val userType : String?, val offset : Int = 0, val limit : Int = 100)