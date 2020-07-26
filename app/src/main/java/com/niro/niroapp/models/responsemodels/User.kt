package com.niro.niroapp.models.responsemodels

data class User(
    val id : String,
    val fullName : String,
    val phoneNumber : String,
    val businessName : String,
    val selectedCategories : List<Category>,
    val selectedMandi : MandiLocation,
    val userType : String
)

enum class UserType(private val userType: String) {
    LOADER("loader"),
    FARMER("farmer"),
    COMMISSION_AGENT("agent")
}