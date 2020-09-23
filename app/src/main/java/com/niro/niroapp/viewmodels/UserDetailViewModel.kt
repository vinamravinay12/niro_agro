package com.niro.niroapp.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.niro.niroapp.models.responsemodels.User
import com.niro.niroapp.models.responsemodels.UserContact
import kotlin.math.abs

enum class ListType {
    ORDERS,PAYMENTS
}
class UserDetailViewModel : ViewModel() {

    private val currentUserData = MutableLiveData<User>()
    private val userName = MutableLiveData<String>()

    private val phoneNumber = MutableLiveData<String>()
    private val businessName = MutableLiveData<String>()
    private val userCommoditiesDisplayName = MutableLiveData<String>()
    private val userLocationDisplayValue = MutableLiveData<String>()

    private val namePrefix = MutableLiveData<String>()
    private val phoneNumberPrefix = MutableLiveData<String>()
    private val addressPrefix = MutableLiveData<String>()
    private val businessNamePrefix = MutableLiveData<String>()
    private val selectedUserContact = MutableLiveData<UserContact>()
    private val totalPendingAmount = MutableLiveData<String>()


    fun getCurrentUserData() = currentUserData
    fun getSelectedContact() = selectedUserContact



    fun getUserName() : MutableLiveData<String> {
        userName.value = "${namePrefix.value}: ${selectedUserContact.value?.contactName ?: ""}"
        return userName
    }

    fun getBusinessName() : MutableLiveData<String> {
        businessName.value = "${businessNamePrefix.value}: ${selectedUserContact.value?.businessName ?: ""}"
        return businessName
    }

    fun getPhoneNumber() : MutableLiveData<String> {
        phoneNumber.value = "${phoneNumberPrefix.value}: ${selectedUserContact.value?.phoneNumber ?: ""}"
        return phoneNumber
    }


    fun getCommoditiesDisplayValue() : MutableLiveData<String> {
        userCommoditiesDisplayName.value = selectedUserContact.value?.selectedCommodity?.joinToString(separator = ", "){"${it.name}" }

        return userCommoditiesDisplayName
    }

    fun getMandiLocation() : MutableLiveData<String> {

        val location = "${currentUserData.value?.selectedMandi?.market ?:""}, ${currentUserData.value?.selectedMandi?.district}, ${currentUserData.value?.selectedMandi?.state}".trim()
        if(location[0] == ',') location.removePrefix(",")
        else if(location[location.lastIndex] == ',') location.removeSuffix(",")
        userLocationDisplayValue.value = "${addressPrefix.value}: $location"

        return userLocationDisplayValue
    }

    fun initializePrefix(name : String,phone : String, business :String, location : String) {
        namePrefix.value = name
        phoneNumberPrefix.value = phone
        businessNamePrefix.value = business
        addressPrefix.value = location
    }

    fun getDifferenceInPayment() = (selectedUserContact.value?.totalOrderAmount ?: 0.0) - (selectedUserContact.value?.totalPaymentsDone ?: 0.0)

    fun getTotalPendingAmount() = totalPendingAmount.apply {
        val difference  = getDifferenceInPayment()
       this.value = if(difference < 0) abs(difference).toString() else difference.toString()
    }



}