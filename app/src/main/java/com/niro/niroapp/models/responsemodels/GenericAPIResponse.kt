package com.niro.niroapp.models.responsemodels

data  class GenericAPIResponse<T>(
    val success : Boolean,
    val message : String,
    val data : T?
)
