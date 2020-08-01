package com.niro.niroapp.models.postdatamodels

import com.niro.niroapp.models.responsemodels.CommodityItem
import com.niro.niroapp.models.responsemodels.MandiLocation

data class UpdateCommoditiesPostData(
    val currentUserId : String?,
    val selectedCategories : List<CommodityItem>?
)