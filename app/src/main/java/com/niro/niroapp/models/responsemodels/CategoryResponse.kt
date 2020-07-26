package com.niro.niroapp.models.responsemodels

data class CategoryResponse(
    val `data`: List<Category>,
    val message: String,
    val success: Boolean
)