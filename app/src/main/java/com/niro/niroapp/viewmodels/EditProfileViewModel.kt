package com.niro.niroapp.viewmodels

import android.content.Context
import android.service.autofill.UserData
import android.text.TextUtils
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.niro.niroapp.R
import com.niro.niroapp.models.APIResponse
import com.niro.niroapp.models.postdatamodels.SignupPostData
import com.niro.niroapp.models.postdatamodels.UpdateProfilePostData
import com.niro.niroapp.models.responsemodels.Contact
import com.niro.niroapp.models.responsemodels.MandiLocation
import com.niro.niroapp.models.responsemodels.User
import com.niro.niroapp.users.models.AddContactPostData
import com.niro.niroapp.users.viewmodels.repositories.AddContactRepository
import com.niro.niroapp.viewmodels.repositories.UpdateUserProfileRepository

class EditProfileViewModel(private val currentUser : User?) : ViewModel() {

    private val currentUserData = MutableLiveData<User>()
    private val userName = MutableLiveData<String>()
    private val phoneNumber = MutableLiveData<String>()
    private val businessName = MutableLiveData<String>()
    private val mandiLocation = MutableLiveData<MandiLocation>()
    private val mandiDisplayName = MediatorLiveData<String>()


    init {
        currentUserData.value = currentUser
    }

    fun getUserName() = userName
    fun getPhoneNumber() = phoneNumber
    fun getBusinessName() = businessName
    fun getMandiLocation() = mandiLocation
    fun getCurrentUserData() =  currentUserData

    fun getMandiDisplayName() : MediatorLiveData<String> {
        if(mandiLocation.value == null) return mandiDisplayName
        mandiDisplayName.removeSource(mandiLocation)
        mandiDisplayName.addSource(mandiLocation) {
            mandiDisplayName.value = "${it.market}, ${it.state}"
        }

        return mandiDisplayName
    }


    fun validateUserName() : MutableLiveData<Int> {
        return when {
            userName.value.isNullOrEmpty() -> MutableLiveData(R.string.user_name_empty_error)
            userName.value?.length ?: 0 < 2 -> MutableLiveData(R.string.name_too_short)
            else -> MutableLiveData(-1)
        }
    }

    fun validatePhoneNumber() : MutableLiveData<Int> {

        return when {
            phoneNumber.value.isNullOrEmpty() -> MutableLiveData(R.string.phone_number_empty_error)
            phoneNumber.value?.length ?: 0 != 10 -> MutableLiveData(R.string.invalid_phone_length)
            !TextUtils.isDigitsOnly(phoneNumber.value) -> MutableLiveData(R.string.invalid_phone_number)
            else -> MutableLiveData(-1)
        }
    }

    fun validateBusinessName() : MutableLiveData<Int> {
        return when {
            businessName.value.isNullOrEmpty() -> MutableLiveData(R.string.business_name_missing)
            businessName.value?.length ?: 0 < 2 -> MutableLiveData(R.string.business_name_missing)
            else -> MutableLiveData(-1)
        }
    }

    fun validateSelectedMandi() : MutableLiveData<Int> {
        return when (mandiLocation.value) {
            null -> MutableLiveData(R.string.mandi_location_empty_error)
            else -> MutableLiveData(-1)
        }
    }

    fun validateAllFields() : Int? {
        return when {
            validateUserName().value ?: 0 > 0 -> validateUserName().value
            validatePhoneNumber().value ?: 0 > 0 -> validatePhoneNumber().value
            validateBusinessName().value ?: 0 > 0 -> validateBusinessName().value
            validateSelectedMandi().value ?: 0 > 0 -> validateSelectedMandi().value
            else -> -1
        }

    }


    fun fillUserDetails() {
        userName.value = currentUserData.value?.fullName ?: ""
        phoneNumber.value = currentUserData.value?.phoneNumber ?: ""
        businessName.value = currentUserData.value?.businessName ?: ""
        mandiLocation.value = currentUserData.value?.selectedMandi
    }

    fun updateUserProfile(context : Context?) : MutableLiveData<APIResponse> {
        val updateUserPostData = UpdateProfilePostData(currentUserId = currentUserData.value?.id,
            name = userName.value,phoneNumber = phoneNumber.value,mandiLocation = mandiLocation.value, businessName = businessName.value)

        return UpdateUserProfileRepository(updateProfileData = updateUserPostData,context = context).getResponse()
    }

    fun resetAllFields() {
        userName.value = ""
        phoneNumber.value = ""
        mandiLocation.value = null
        businessName.value = ""
    }
}