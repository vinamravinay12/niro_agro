package com.niro.niroapp.models.postdatamodels

import com.niro.niroapp.models.responsemodels.CommodityItem
import com.niro.niroapp.models.responsemodels.UserContact

data class CreateOrderPostData(val currentUserId : String?, val userContact : UserContact?, val orderAmount : Double?, val receivingDate : String?,
val orderCommodity : ArrayList<CommodityItem>?, val imageName : List<String>?){}