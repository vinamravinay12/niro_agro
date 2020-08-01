package com.niro.niroapp.viewmodels

import android.content.Context
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.niro.niroapp.R
import com.niro.niroapp.models.APIResponse
import com.niro.niroapp.models.postdatamodels.CreateOrderPostData
import com.niro.niroapp.models.postdatamodels.CreatePaymentPostData
import com.niro.niroapp.models.responsemodels.CommodityItem
import com.niro.niroapp.models.responsemodels.UserContact
import com.niro.niroapp.utils.DateUtils
import com.niro.niroapp.utils.NIroAppConstants
import com.niro.niroapp.viewmodels.repositories.AddPaymentRepository
import com.niro.niroapp.viewmodels.repositories.CreateOrderRepository
import java.util.*


enum class PaymentMode( val mode: String) {
    CASH("Cash"),
    ONLINE("Online")
}
class CreatePaymentsViewModel : ViewModel() {


    private val selectedContact = MutableLiveData<UserContact>()
    private val paymentDate = MutableLiveData<String>()
    private val paymentMode = MutableLiveData<PaymentMode>()
    private val paymentAmount = MutableLiveData<String>("0")
    private val paymentDateDisplayValue = MediatorLiveData<String>()


    fun getPaymentMode() = paymentMode
    fun getPaymentDate() = paymentDate
    fun getPaymentAmount() = paymentAmount


    fun getPaymentDateDisplayValue() : MediatorLiveData<String> {
        if(paymentDate.value.isNullOrEmpty()) return paymentDateDisplayValue
        else {
           paymentDateDisplayValue.removeSource(paymentDate)
           paymentDateDisplayValue.addSource(paymentDate) {
               paymentDateDisplayValue.value = DateUtils.convertDate(paymentDate.value,NIroAppConstants.POST_DATE_FORMAT,NIroAppConstants.DISPLAY_DATE_FORMAT)
           }
       }

        return paymentDateDisplayValue
    }



    fun addPayment(context : Context?) : MutableLiveData<APIResponse> {
        val addPaymentPostData = CreatePaymentPostData(selectedContact.value?.currentUserId,selectedContact = selectedContact.value,
        paymentAmount = paymentAmount.value?.toDouble() ?: 0.0,paymentMode = paymentMode.value?.name ?: PaymentMode.ONLINE.name,
        paymentDate = paymentDate.value ?: getDefaultPaymentDate() )

        return AddPaymentRepository(addPaymentPostData,context).getResponse()

    }

    private fun getDefaultPaymentDate(): String {
        return DateUtils.getDateString(Date(),NIroAppConstants.POST_DATE_FORMAT) ?: ""
    }


    fun resetAllFields() {
        paymentAmount.value = ""
        paymentDate.value = ""



    }
}