package com.niro.niroapp.viewmodels

import android.content.Context
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.niro.niroapp.R
import com.niro.niroapp.models.APIResponse
import com.niro.niroapp.models.postdatamodels.CreateSellOrderPostData
import com.niro.niroapp.models.responsemodels.User
import com.niro.niroapp.utils.DateUtils
import com.niro.niroapp.utils.NiroAppConstants
import com.niro.niroapp.viewmodels.repositories.CreateSellOrderRepository

 enum class QuantityType(val type : String) {
    KG("Kg"),QUINTAL("Quintal"),TON("Ton")
}
class CreateSellOrderViewModel(currentUser : User?) : ViewModel(){

    private val currentUserDetails = MutableLiveData<User>()
    private val commodityName = MutableLiveData<String>()

    private val quantityAvailable = MutableLiveData<String>()
    private val quantityTypeSelected = MutableLiveData<String>(QuantityType.QUINTAL.type)

    private val dispatchDate = MutableLiveData<String>()
    private val price = MutableLiveData<String>()

    private val orderImages = MutableLiveData<MutableList<String>>(ArrayList<String>().toMutableList())
    private val orderImagesAbsolutePath = MutableLiveData<MutableList<String>>(ArrayList<String>().toMutableList())

    private val selectedDateDisplayValue = MutableLiveData<String>()

    init {
        currentUserDetails.value = currentUser
    }

    fun getCommodityName() = commodityName
    fun getDispatchDate() = dispatchDate
    fun getOrderImages() = orderImages
    fun getQuantityAvailable() = quantityAvailable
    fun getQuantityTypeSelected() = quantityTypeSelected
    fun getPrice() = price
    fun getCurrentUserDetails() = currentUserDetails
    fun getOrderImagesAbsolutePath() = orderImagesAbsolutePath




    fun getDispatchDateDisplayValue() = selectedDateDisplayValue.apply {
        this.value = if(dispatchDate.value.isNullOrEmpty()) ""
        else {  DateUtils.convertDate(dispatchDate.value,
            NiroAppConstants.POST_DATE_FORMAT,
            NiroAppConstants.DISPLAY_DATE_FORMAT)
        }

        return this
    }


    fun validateEnteredCommodity() : MutableLiveData<Int> {
        return if(commodityName.value.isNullOrEmpty()) MutableLiveData(R.string.commodity_empty_error) else MutableLiveData(-1)

    }

    fun validateDispatchDate() : MutableLiveData<Int> {
        return  if(dispatchDate.value.isNullOrEmpty()) MutableLiveData(R.string.dispatch_date_empty)
        else MutableLiveData(-1)
    }


    fun validateAllFields() : Int? {
        return when {
            validateEnteredCommodity().value ?: 0 > 0 -> validateEnteredCommodity().value
            validateDispatchDate().value ?: 0 > 0 -> validateDispatchDate().value
            else -> -1
        }

    }



    fun createSellOrder(context : Context?) : MutableLiveData<APIResponse> {


        val createSellOrderPostData =  CreateSellOrderPostData(currentUserId = currentUserDetails.value?.id,
            quantityAvailable ="${quantityAvailable.value}",quantityType = quantityTypeSelected.value,
            dispatchDate = "${dispatchDate.value}", commodity = commodityName.value,
            price = if(price.value?.isEmpty() == true) 0.0 else price.value?.toDouble(),
            imageName = orderImages.value?.toList(), userType = currentUserDetails.value?.userType)

        return CreateSellOrderRepository(createSellOrderPostData,context).getResponse()

    }




    fun resetAllFields() {
        currentUserDetails.value = null
        dispatchDate.value = ""
        quantityAvailable.value = ""
        price.value = ""
        commodityName.value = ""
        orderImages.value?.clear()
    }
}