package com.niro.niroapp.models.postdatamodels

import com.niro.niroapp.models.responsemodels.CommodityItem
import com.niro.niroapp.users.fragments.ContactType

class CreateSellOrderPostData(val currentUserId : String?, val quantityAvailable : String?, val quantityType : String?, val dispatchDate : String?,
                              val price : Double?, val commodity : String?, val imageName : List<String>?, val userType : String?)