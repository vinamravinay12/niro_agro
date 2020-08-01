package com.niro.niroapp.viewmodels

import android.content.Context
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.niro.niroapp.R
import com.niro.niroapp.models.APIResponse
import com.niro.niroapp.models.postdatamodels.CreateOrderPostData
import com.niro.niroapp.models.responsemodels.CommodityItem
import com.niro.niroapp.models.responsemodels.UserContact
import com.niro.niroapp.viewmodels.repositories.CreateOrderRepository

class CreateOrderViewModel() : ViewModel() {

    private val selectedContact = MutableLiveData<UserContact>()
    private val orderAmount = MutableLiveData<String>()
    private val receivingDate = MutableLiveData<String>()
    private val selectedCommodity = MutableLiveData<ArrayList<CommodityItem>>(ArrayList())
    private val orderImages = MutableLiveData<MutableList<String>>(ArrayList<String>().toMutableList())

    private val selectedCommodityDisplayName = MediatorLiveData<String>()

    private val selectedDateDisplayValue = MediatorLiveData<String>()



    fun getOrderAmount() = orderAmount
    fun getReceivingDate() = receivingDate
    fun getOrderImages() = orderImages
    fun getSelectedCommodity() = selectedCommodity




    fun getSelectedCommodityDisplayName() : MediatorLiveData<String> {
        if(selectedCommodity.value?.isEmpty() == true) return selectedCommodityDisplayName
        selectedCommodityDisplayName.removeSource(selectedCommodity)
        selectedCommodityDisplayName.addSource(selectedCommodity) {
            if(it.isNotEmpty()) {
                val commodityItem = it[0]
                selectedCommodityDisplayName.value = "${commodityItem.name}"
            }

        }

        return selectedCommodityDisplayName
    }


    fun getOrderAmountDisplayValue() = orderAmount
    fun getReceivingDateDisplayValue() = selectedDateDisplayValue



    fun validateOrderAmount() : MutableLiveData<Int> {
        return when {
            orderAmount.value.isNullOrEmpty() -> MutableLiveData(R.string.order_amount_empty)
            else -> MutableLiveData(-1)
        }
    }




    fun validateSelectedCommodity() : MutableLiveData<Int> {
        return when (selectedCommodity.value) {
            null -> MutableLiveData(R.string.commodity_empty_error)
            else -> MutableLiveData(-1)
        }
    }

    fun validateOrderImages() : MutableLiveData<Int> {
        return  if(orderImages.value.isNullOrEmpty()) MutableLiveData(R.string.order_image_empty)
        else MutableLiveData(-1)
    }


    fun validateAllFields() : Int? {
        return when {
            validateOrderAmount().value ?: 0 > 0 -> validateOrderAmount().value
            validateSelectedCommodity().value ?: 0 > 0 -> validateSelectedCommodity().value
            validateOrderAmount().value ?: 0 > 0 -> validateSelectedCommodity().value
            else -> -1
        }

    }



    fun createOrder(context : Context?) : MutableLiveData<APIResponse> {
        val createOrderPostData =  CreateOrderPostData(currentUserId = selectedContact.value?.currentUserId,
        userContact = selectedContact.value, orderAmount = orderAmount.value?.toDouble(),receivingDate = receivingDate.value,
        orderCommodity = selectedCommodity.value,imageName = orderImages.value?.toList())

        return CreateOrderRepository(createOrderPostData,context).getResponse()

    }





    fun resetAllFields() {
        receivingDate.value = ""
        orderAmount.value = ""
        selectedCommodity.value = null
        orderImages.value?.clear()


    }



}