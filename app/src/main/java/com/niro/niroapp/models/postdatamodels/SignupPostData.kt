package com.niro.niroapp.models.postdatamodels

import com.niro.niroapp.models.responsemodels.Category
import com.niro.niroapp.models.responsemodels.CommodityItem
import com.niro.niroapp.models.responsemodels.MandiLocation
import com.niro.niroapp.models.responsemodels.UserType

data class SignupPostData(
    val name : String?,
    val businessName : String?,
    val userType: String?,
    val mandiLocation : MandiLocation?,
    val selectedCategories : List<CommodityItem>?,
    val phoneNumber : String?
)


data class UpdateProfilePostData(
    val currentUserId : String?,
    val name : String?,
    val businessName : String?,
    val mandiLocation : MandiLocation?,
    val phoneNumber : String?
)