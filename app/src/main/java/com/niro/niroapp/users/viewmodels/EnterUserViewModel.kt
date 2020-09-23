package com.niro.niroapp.users.viewmodels

import android.content.Context
import android.text.TextUtils
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.niro.niroapp.R
import com.niro.niroapp.models.APIResponse
import com.niro.niroapp.models.responsemodels.CommodityItem
import com.niro.niroapp.models.responsemodels.Contact
import com.niro.niroapp.models.responsemodels.MandiLocation
import com.niro.niroapp.models.responsemodels.User
import com.niro.niroapp.users.models.AddContactPostData
import com.niro.niroapp.users.viewmodels.repositories.AddContactRepository
import com.niro.niroapp.users.viewmodels.repositories.GetUserByNumberRepository

class EnterUserViewModel(selectedContact : Contact?) : ViewModel() {

    private val selectedContactData = MutableLiveData<Contact>()
    private val contactName = MutableLiveData<String>()
    private val phoneNumber = MutableLiveData<String>()
    private val businessName = MutableLiveData<String>()
    private val mandiLocationData = MutableLiveData<MandiLocation>()
    private val selectedCommodity = MutableLiveData<ArrayList<CommodityItem>>(ArrayList())
    private val currentUser = MutableLiveData<User>()

    private val mandiDisplayName = MediatorLiveData<String>()
    private val selectedCommodityDisplayName = MediatorLiveData<String>()
    private val checkedPhoneNumbers = MutableLiveData<MutableList<String>>()

    init {
        selectedContactData.value = selectedContact
    }

    fun getContactName() = contactName.apply {
        if(!contactName.value.equals(selectedContactData.value?.name,true)) {
            selectedContactData.value?.name = this.value
        }
    }
    fun getPhoneNumber() = phoneNumber
    fun getBusinessName() = businessName
    fun getMandiLocation() = mandiLocationData
    fun getSelectedCommodity() = selectedCommodity
    fun getCurrentUser() =  currentUser



    fun getMandiDisplayName() = mandiDisplayName.apply {
        if(mandiLocationData.value != null) {
            addSource(mandiLocationData) {

                mandiDisplayName.value = "${it.market}, ${it.state}"
                removeSource(mandiLocationData)
            }
        }
    }

    fun getSelectedCommodityDisplayName() : MediatorLiveData<String> {
        if(selectedCommodity.value?.isEmpty() == true) return selectedCommodityDisplayName
        selectedCommodityDisplayName.removeSource(selectedCommodity)
        selectedCommodityDisplayName.addSource(selectedCommodity) {
            if(!it.isNullOrEmpty()) {
                val commodityItem = it[0]
                selectedCommodityDisplayName.value = "${commodityItem.name}"
            }

        }

        return selectedCommodityDisplayName
    }

    fun validateUserName() : MutableLiveData<Int> {
        return when {
            contactName.value.isNullOrEmpty() -> MutableLiveData(R.string.user_name_empty_error)
            contactName.value?.length ?: 0 < 2 -> MutableLiveData(R.string.name_too_short)
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
        return when (mandiLocationData.value) {
            null -> MutableLiveData(R.string.mandi_location_empty_error)
            else -> MutableLiveData(-1)
        }
    }

    fun validateSelectedCommodity() : MutableLiveData<Int> {
        return when (mandiLocationData.value) {
            null -> MutableLiveData(R.string.commodity_empty_error)
            else -> MutableLiveData(-1)
        }
    }


    fun validateAllFields() : Int? {
       return when {
           validateUserName().value ?: 0 > 0 -> validateUserName().value
           validatePhoneNumber().value ?: 0 > 0 -> validatePhoneNumber().value
           validateBusinessName().value ?: 0 > 0 -> validateBusinessName().value
           validateSelectedCommodity().value ?: 0 > 0 -> validateSelectedCommodity().value
           validateSelectedMandi().value ?: 0 > 0 -> validateSelectedMandi().value
           else -> -1
       }

    }

    fun fillContactDetails(contact : Contact?) {
        if(contact?.name.isNullOrEmpty() || contact?.number.isNullOrEmpty()) { return }
        selectedContactData.value = contact
        contactName.value = selectedContactData.value?.name ?: ""
        phoneNumber.value = selectedContactData.value?.number ?: ""
    }




    fun createContact(context : Context?) : MutableLiveData<APIResponse> {
        val addContactPostData = AddContactPostData(currentUserId = currentUser.value?.id,
        contactName = contactName.value,phoneNumber = phoneNumber.value,userLocation = mandiLocationData.value,
            selectedCommodity = selectedCommodity.value,businessName = businessName.value)

        return AddContactRepository(addContactPostData,context).getResponse()
    }


    fun getUserFromPhoneNumber(context: Context?) : MutableLiveData<APIResponse> = GetUserByNumberRepository(context,phoneNumber.value).getResponse()


    fun resetAllFields() {
        contactName.value = ""
        phoneNumber.value = ""
        selectedCommodity.value = null
        mandiLocationData.value = null
        businessName.value = ""
        checkedPhoneNumbers.value?.clear()

    }

    fun fillUserDetails(user: User?) {
        if(currentUser.value?.userType?.equals(user?.userType,true) != true) {
                contactName.value = user?.fullName ?: contactName.value
                businessName.value = user?.businessName ?: businessName.value
                mandiLocationData.value = user?.selectedMandi ?: mandiLocationData.value
                checkedPhoneNumbers.value?.add(phoneNumber.value ?: "")
            }

    }


}