package com.niro.niroapp.models

sealed class APIResponse

data class APIError( val errorCode : Int,  val errorMessage : String) : APIResponse()

data class Success<T>( val data : T? ) : APIResponse()

data class APILoader( val isLoading : Boolean) : APIResponse()