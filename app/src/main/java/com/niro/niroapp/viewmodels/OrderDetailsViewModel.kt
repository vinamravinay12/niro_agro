package com.niro.niroapp.viewmodels

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.niro.niroapp.models.responsemodels.User
import com.niro.niroapp.models.responsemodels.UserOrder
import com.niro.niroapp.models.responsemodels.UserType
import com.niro.niroapp.utils.DateUtils
import com.niro.niroapp.utils.NiroAppConstants
import kotlin.math.abs

class OrderDetailsViewModel : ViewModel() {

    private val currentUserData = MutableLiveData<User>()
    private val selectedOrder = MutableLiveData<UserOrder>()
    private val amountPrefix = MutableLiveData<String>("")

    private val contactName = MutableLiveData<String>("")
    private val mandiLocationDisplayName = MutableLiveData<String>("")

    private val totalAmount = MutableLiveData<String>("")
    private val amountPaid = MutableLiveData<String>("")
    private val amountPending = MutableLiveData<String>("")
    private val sourceLocation = MutableLiveData<String>("")
    private val destinationLocation = MutableLiveData<String>("")

    fun getCurrentUserData() = currentUserData
    fun getSelectedOrder() = selectedOrder
    fun getAmountPrefix() = amountPrefix
    fun getOrderImageName() : String {
       return if(selectedOrder.value?.imageNames.isNullOrEmpty()) ""
        else selectedOrder.value?.imageNames?.get(0) ?: ""
    }


    fun getOrderReceivingDisplayDate() = MediatorLiveData<String>().apply {
        addSource(selectedOrder) {
            this.value = DateUtils.convertDate(
                selectedOrder.value?.receivingDate,
                NiroAppConstants.POST_DATE_FORMAT,
                NiroAppConstants.DISPLAY_DATE_FORMAT
            )

            removeSource(selectedOrder)
        }
    }



    fun getOrderAmountDisplayValue(): MutableLiveData<String> {
        val amountDisplayValue = MutableLiveData<String>()
        amountDisplayValue.value =
            if (selectedOrder.value?.orderAmount?.isNaN() == true) ""
            else {
                val amountString: String = selectedOrder.value?.orderAmount.toString() ?: "0"
                "${amountPrefix.value} $amountString".trim()
            }
        return amountDisplayValue
    }


    fun getContactName() = contactName.apply {
        if (selectedOrder.value?.userContact?.contactName.isNullOrEmpty()) this.value = ""
        else this.value = selectedOrder.value?.userContact?.contactName ?: ""
    }

    fun getMandiLocation() = mandiLocationDisplayName.apply {
        val mandiLocation = selectedOrder.value?.userContact?.userLocation
        this.value = "${mandiLocation?.market ?: ""}, ${mandiLocation?.state ?: ""}"
    }

    fun getSourceLocation() = sourceLocation.apply {
        this.value = if(currentUserData.value?.userType == UserType.COMMISSION_AGENT.name) getSelectedContactMandiLocation()
        else getCurrentUserMandiLocation()
    }


    private fun getCurrentUserMandiLocation() : String {
        val mandiLocation = currentUserData.value?.selectedMandi
       return "${mandiLocation?.market ?: ""}, ${mandiLocation?.state ?: ""}"
    }

    private fun getSelectedContactMandiLocation() : String {
        val mandiLocation = selectedOrder.value?.userContact?.userLocation
        return "${mandiLocation?.market ?: ""}, ${mandiLocation?.state ?: ""}"
    }

    fun getDestinationLocation() = destinationLocation.apply {
        this.value = if(currentUserData.value?.userType == UserType.COMMISSION_AGENT.name) getCurrentUserMandiLocation()
        else getSelectedContactMandiLocation()
    }

    fun getTotalAmount() = totalAmount.apply {
       this.value =  "${amountPrefix.value} ${selectedOrder.value?.userContact?.totalOrderAmount}"
    }

    fun getAmountPaid() = amountPaid.apply {
        this.value = "${amountPrefix.value} ${selectedOrder.value?.userContact?.totalPaymentsDone}"
    }


    fun getAmountDifference() : Double? = (selectedOrder.value?.userContact?.totalOrderAmount ?: 0.0) - (selectedOrder.value?.userContact?.totalPaymentsDone ?: 0.0)

    fun getAmountPending() = amountPending.apply {
        this.value = if((getAmountDifference() ?: 0.0 ) < 0.0 )  "${amountPrefix.value} ${abs(getAmountDifference() ?: 0.0)}"
                    else  "${amountPrefix.value} ${getAmountDifference()}"
    }



    fun getCommodityName() = MutableLiveData<String>().apply {
        this.value = when {
            selectedOrder.value?.orderCommodity?.isNullOrEmpty() == true -> ""
            else -> selectedOrder.value?.orderCommodity?.get(0)?.name ?: ""
        }
    }

    fun getCommodityImage() = MutableLiveData<String>().apply {
        this.value = when {
            selectedOrder.value?.orderCommodity?.isNullOrEmpty() == true -> ""
            else -> selectedOrder.value?.orderCommodity?.get(0)?.image ?: ""
        }
    }

    fun getOrderNumber() = MutableLiveData<String>().apply {
        this.value =  if(selectedOrder.value?.orderNumber.isNullOrEmpty()) ""
        else "#00${selectedOrder.value?.orderNumber}"
    }


}