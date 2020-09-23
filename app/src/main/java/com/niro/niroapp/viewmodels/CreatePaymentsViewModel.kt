package com.niro.niroapp.viewmodels

import android.content.Context
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.niro.niroapp.models.APIResponse
import com.niro.niroapp.models.postdatamodels.CreatePaymentPostData
import com.niro.niroapp.models.responsemodels.UserContact
import com.niro.niroapp.users.fragments.ContactType
import com.niro.niroapp.utils.DateUtils
import com.niro.niroapp.utils.NiroAppConstants
import com.niro.niroapp.viewmodels.repositories.AddPaymentRepository
import java.util.*


enum class PaymentMode( val mode: String) {
    CASH("Cash"),
    ONLINE("Online")
}
class CreatePaymentsViewModel(currentUserId: String?, selectedContactId: String?, selectedContactType : String?) : ViewModel() {

    private val mCurrentUserId = MutableLiveData<String>()
    private val selectedContactId = MutableLiveData<String>()
    private val selectedContactType = MutableLiveData<String>()
    private val paymentDate = MutableLiveData<String>()
    private val paymentMode = MutableLiveData<PaymentMode>(PaymentMode.ONLINE)
    private val paymentAmount = MutableLiveData<String>()
    private val paymentDateDisplayValue = MediatorLiveData<String>()

    init {
        mCurrentUserId.value = currentUserId
        this.selectedContactId.value = selectedContactId
        this.selectedContactType.value = selectedContactType
    }

    fun getPaymentMode() = paymentMode
    fun getPaymentDate() = paymentDate
    fun getPaymentAmount() = paymentAmount
    fun getSelectedContactId() = selectedContactId
    fun getSelectedContactType() = selectedContactType
    fun getCurrentUserId() = mCurrentUserId


    fun getPaymentDateDisplayValue() : MutableLiveData<String> {
        if(paymentDate.value.isNullOrEmpty()) return paymentDateDisplayValue
        else {
           paymentDateDisplayValue.removeSource(paymentDate)
           paymentDateDisplayValue.addSource(paymentDate) {
               paymentDateDisplayValue.value = DateUtils.convertDate(paymentDate.value,NiroAppConstants.POST_DATE_FORMAT,NiroAppConstants.DISPLAY_DATE_FORMAT)
           }
       }

        return paymentDateDisplayValue
    }



    fun addPayment(context : Context?) : MutableLiveData<APIResponse> {
        val addPaymentPostData = CreatePaymentPostData(currentUserId = mCurrentUserId.value,selectedContactId = selectedContactId.value,
        paymentAmount = paymentAmount.value?.toDouble() ?: 0.0,paymentMode = paymentMode.value?.name ?: PaymentMode.ONLINE.name,
        paymentDate = paymentDate.value ?: getDefaultPaymentDate(), contactType = selectedContactType.value ?: ContactType.MY_LOADERS.type )

        return AddPaymentRepository(addPaymentPostData,context).getResponse()

    }

    private fun getDefaultPaymentDate(): String {
        return DateUtils.getDateString(Date(),NiroAppConstants.POST_DATE_FORMAT) ?: ""
    }


    fun resetAllFields() {
        paymentAmount.value = ""
        paymentDate.value = ""
        paymentMode.value = PaymentMode.ONLINE
        mCurrentUserId.value = null
        selectedContactType.value = null
        selectedContactId.value = null



    }
}