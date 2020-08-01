package com.niro.niroapp.viewmodels

import android.content.Context
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.niro.niroapp.models.APIResponse
import com.niro.niroapp.models.postdatamodels.SignupPostData
import com.niro.niroapp.models.responsemodels.*
import com.niro.niroapp.network.ApiClient
import com.niro.niroapp.viewmodels.repositories.CategoryListRepository
import com.niro.niroapp.viewmodels.repositories.MandiLocationRepository
import com.niro.niroapp.viewmodels.repositories.SignUpRepository
import java.util.*
import kotlin.collections.ArrayList

class SignupViewModel(phoneNumber: String?): ViewModel() {


    private val phoneNumber = MutableLiveData(phoneNumber)
    private val fullName = MutableLiveData<String>()
    private val businessName = MutableLiveData<String>()


    private val selectedCommodities = MutableLiveData<MutableList<CommodityItem>>(ArrayList<CommodityItem>().toMutableList())
    private val selectedMandiLocation = MutableLiveData<MandiLocation>()
    private val userType = MutableLiveData<UserType>()

    fun getPhoneNumber() = phoneNumber
    fun getFullName() = fullName
    fun getBusinessName() = businessName
    fun getSelectedCommodities() = selectedCommodities
    fun getSelectedMandiLocation() = selectedMandiLocation
    fun getUserType() = userType


    fun validateFullName() : Boolean {
        return (!fullName.value.isNullOrEmpty() && fullName.value?.length ?: 0 > 2)
    }

    fun validateBusinessName() : Boolean {
        return (!businessName.value.isNullOrEmpty() && businessName.value?.length ?: 0 > 2)
    }



    fun signUpUser(context: Context?) : MutableLiveData<APIResponse> {
        val signupPostData = SignupPostData(name = fullName.value,businessName = businessName.value,
            userType = userType.value?.name,mandiLocation = selectedMandiLocation.value,
            selectedCategories = selectedCommodities.value, phoneNumber = phoneNumber.value )

        val signUpRepository = SignUpRepository<LoginResponse>(signupPostData,context)
        return signUpRepository.getResponse()
    }

}