package com.niro.niroapp.viewmodels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.niro.niroapp.R
import com.niro.niroapp.adapters.PaymentsAdapter
import com.niro.niroapp.models.APIResponse
import com.niro.niroapp.models.responsemodels.User
import com.niro.niroapp.models.responsemodels.UserPayment
import com.niro.niroapp.utils.DateUtils
import com.niro.niroapp.utils.FilterResultsListener
import com.niro.niroapp.utils.NiroAppConstants
import com.niro.niroapp.viewmodels.repositories.GetPaymentsRepository

class PaymentsViewModel( currentUser : User?) : ListViewModel(),
    FilterResultsListener<UserPayment> {

    private val currentUserData = MutableLiveData<User>()
    private val paymentsList = MutableLiveData<MutableList<UserPayment>>()
    private val amountPrefix = MutableLiveData<String> ("")
    private val paymentAmountDisplay = MutableLiveData<String>("")
    private var adapter : PaymentsAdapter

    init {
        currentUserData.value = currentUser
        adapter = PaymentsAdapter(getViewType(),paymentsList.value,this)
    }


    fun getPaymentsList() = paymentsList

    fun getAmountPrefix() = amountPrefix

    fun getAdapter() = adapter


    fun getPayments(context: Context?)  : MutableLiveData<APIResponse>? = GetPaymentsRepository(currentUserData.value?.id,context).getResponse()


    fun getPaymentDate(position : Int) : MutableLiveData<String> {
        val paymentDate = MutableLiveData<String>()
        paymentDate.value =  if(paymentsList.value.isNullOrEmpty()) ""
        else {
            val convertedDate : String? = DateUtils.convertDate(paymentsList.value?.get(position)?.paymentDate,
                NiroAppConstants.POST_DATE_FORMAT,
                NiroAppConstants.DISPLAY_DATE_FORMAT)
            convertedDate ?: ""
        }

        return paymentDate
    }


    fun getPaymentAmountDisplayValue(position: Int) : MutableLiveData<String> {
        val amountDisplayValue = MutableLiveData<String>()
        amountDisplayValue.value =
         if(paymentsList.value.isNullOrEmpty()) ""
        else {
            val amountString : String = paymentsList.value?.get(position)?.paymentAmount.toString() ?: "0"
           "${amountPrefix.value} $amountString".trim()
        }
        return amountDisplayValue
    }


    fun getContactName(position: Int) : MutableLiveData<String> {
        val contactName = MutableLiveData<String>()
        contactName.value = if(paymentsList.value.isNullOrEmpty()) ""
        else paymentsList.value?.get(position)?.userContact?.contactName ?: ""

        return contactName
    }

    fun getMandiLocation(position: Int) : MutableLiveData<String> {

        val mandiLocation = MutableLiveData<String>()

        mandiLocation.value =  if(paymentsList.value.isNullOrEmpty()) ""
        else {
            val mandiLocation = paymentsList.value?.get(position)?.userContact?.userLocation
            "${mandiLocation?.district ?: ""}, ${mandiLocation?.state ?: "" }"
        }

        return mandiLocation
    }

    override fun getViewType(): Int = R.layout.card_payment_detail

    override fun updateList() {
        adapter.updateList(paymentsList.value)
    }

    override fun onResultsFiltered(filteredList: List<UserPayment>) {
        paymentsList.value?.clear()
        paymentsList.value = filteredList.toMutableList()
        adapter.updateFilterList(filteredList.toMutableList())
    }

    fun getTotalPaymentAmount(): Int {
        return if(paymentsList.value.isNullOrEmpty()) 0
        else {
            val paymentsList = paymentsList.value
            val sum  = paymentsList?.sumBy { userPayment -> (userPayment.paymentAmount.toInt()) }
            return sum ?: 0
        }
    }


}
