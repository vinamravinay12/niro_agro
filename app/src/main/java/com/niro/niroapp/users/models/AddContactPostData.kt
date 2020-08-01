package com.niro.niroapp.users.models

import com.niro.niroapp.models.responsemodels.CommodityItem
import com.niro.niroapp.models.responsemodels.MandiLocation
import com.niro.niroapp.models.responsemodels.User

data class AddContactPostData(val currentUserId : String?, val contactName : String?, val phoneNumber : String?,
                              val userLocation : MandiLocation?, val selectedCommodity : List<CommodityItem>?, val businessName : String?)