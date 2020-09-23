package com.niro.niroapp.models.postdatamodels

import com.niro.niroapp.models.responsemodels.CommodityItem
import com.niro.niroapp.models.responsemodels.UserContact
import com.niro.niroapp.users.fragments.ContactType

data class CreateOrderPostData(val currentUserId : String?, val selectedContactId : String?, val orderAmount : Double?, val receivingDate : String?,
val orderCommodity : ArrayList<CommodityItem>?, val imageName : List<String>?, val contactType : String = ContactType.MY_BUYERS.type){}