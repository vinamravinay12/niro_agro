package com.niro.niroapp.models.responsemodels

import com.google.gson.annotations.SerializedName

data class Category(
    val id: String,
    @SerializedName("commodties") val commodities: List<Commodity>,
    val name: String
)