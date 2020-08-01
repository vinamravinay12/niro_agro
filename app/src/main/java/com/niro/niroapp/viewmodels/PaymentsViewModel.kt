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
import com.niro.niroapp.utils.NIroAppConstants
import com.niro.niroapp.viewmodels.repositories.GetPaymentsRepository

class PaymentsViewModel(private val currentUser : User?) : ListViewModel(),
    FilterResultsListener<UserPayment> {

    private val currentUserData = MutableLiveData<User>(currentUser)
    private val paymentsList = MutableLiveData<MutableList<UserPayment>>()
    private val amountPrefix = MutableLiveData<String> ("")
    private var adapter : PaymentsAdapter

    init {
        adapter = PaymentsAdapter(getViewType(),paymentsList.value,this)
    }


    fun getPaymentsList() = paymentsList

    fun getAmountPrefix() = amountPrefix

    fun getAdapter() = adapter


    fun getPayments(context: Context?)  : MutableLiveData<APIResponse>? = GetPaymentsRepository(currentUserData.value?.id,context).getResponse()


    fun getPaymentDate(position : Int) : MutableLiveData<String> {
        return if(paymentsList.value.isNullOrEmpty()) MutableLiveData("")
        else {
            val convertedDate : String? = DateUtils.convertDate(paymentsList.value?.get(position)?.paymentDate,
                NIroAppConstants.POST_DATE_FORMAT,
                NIroAppConstants.DISPLAY_DATE_FORMAT)
            return MutableLiveData(convertedDate ?: "")
        }
    }


    fun getOrderAmountDisplayValue(position: Int) : MutableLiveData<String> {
        return if(paymentsList.value.isNullOrEmpty()) MutableLiveData("")
        else {

            val amountString : String = paymentsList.value?.get(position)?.paymentAmount.toString() ?: "0"
            return MutableLiveData("$amountPrefix $amountString".trim())
        }
    }


    fun getContactName(position: Int) : MutableLiveData<String> {
        return if(paymentsList.value.isNullOrEmpty()) MutableLiveData("")
        else MutableLiveData(paymentsList.value?.get(position)?.userContact?.contactName ?: "")
    }

    fun getMandiLocation(position: Int) : MutableLiveData<String> {
        return if(paymentsList.value.isNullOrEmpty()) MutableLiveData("")
        else {
            val mandiLocation = paymentsList.value?.get(position)?.userContact?.userLocation

            return MutableLiveData("${mandiLocation?.district ?: ""}, ${mandiLocation?.state ?: "" }")
        }
    }

    override fun getViewType(): Int = R.layout.card_order_detail

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
