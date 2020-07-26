package com.niro.niroapp.models.responsemodels

data class MandiLocationResponse(
    val `data`: List<MandiLocation>,
    val message: String,
    val success: Boolean
)