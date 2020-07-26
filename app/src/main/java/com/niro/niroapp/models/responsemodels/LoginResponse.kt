package com.niro.niroapp.models.responsemodels

data class LoginResponse(
  val success : Boolean,
  val message : String,
  val token : String,
  val data : User
)