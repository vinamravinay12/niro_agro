package com.niro.niroapp.models.responsemodels

data class Category(
    val Id: String,
    val commodities: List<Commodity>,
    val name: String
)