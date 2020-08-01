package com.niro.niroapp.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.niro.niroapp.models.responsemodels.User

class ProfileDetailsViewModel(private val currentUser : User?) : ViewModel() {

    private val currentUserData = MutableLiveData<User>()
    private val userName = MutableLiveData<String>()

    private val phoneNumber = MutableLiveData<String>()
    private val businessName = MutableLiveData<String>()
    private val userLocationDisplayValue = MutableLiveData<String>()
    private val userCommoditiesDisplayName = MutableLiveData<String>()

    private val namePrefix = MutableLiveData<String>()
    private val phoneNumberPrefix = MutableLiveData<String>()
    private val addressPrefix = MutableLiveData<String>()
    private val businessNamePrefix = MutableLiveData<String>()


    fun getUserName() : MutableLiveData<String> {
        userName.value = "${namePrefix.value}: ${currentUserData.value?.fullName ?: ""}"
        return userName
    }

    fun getBusinessName() : MutableLiveData<String> {
        businessName.value = "${businessNamePrefix.value}: ${currentUserData.value?.businessName ?: ""}"
        return businessName
    }

    fun getPhoneNumber() : MutableLiveData<String> {
        phoneNumber.value = "${phoneNumberPrefix.value}: ${currentUserData.value?.phoneNumber ?: ""}"
        return phoneNumber
    }

    fun getMandiLocation() : MutableLiveData<String> {

       val location = "${currentUserData.value?.selectedMandi?.market ?:""}, ${currentUserData.value?.selectedMandi?.district}, ${currentUserData.value?.selectedMandi?.state}".trim()
        if(location[0] == ',') location.removePrefix(",")
        else if(location[location.lastIndex] == ',') location.removeSuffix(",")
        userLocationDisplayValue.value = "${addressPrefix.value}: $location"

        return userLocationDisplayValue
    }

    fun getCommoditiesDisplayValue() : MutableLiveData<String> {
       userCommoditiesDisplayName.value = currentUserData.value?.selectedCategories?.joinToString(separator = ", "){"${it.name}" }

        return userCommoditiesDisplayName
    }

    fun initializePrefix(name : String,phone : String, business :String, location : String) {
        namePrefix.value = name
        phoneNumberPrefix.value = phone
        businessNamePrefix.value = business
        addressPrefix.value = location
    }

}