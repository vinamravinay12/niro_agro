package com.niro.niroapp.users.viewmodels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.niro.niroapp.R
import com.niro.niroapp.models.APIResponse
import com.niro.niroapp.models.responsemodels.User
import com.niro.niroapp.models.responsemodels.UserPayment
import com.niro.niroapp.users.adapters.ContactDetailPaymentsAdapter
import com.niro.niroapp.users.models.GetOrdersForContactPostData
import com.niro.niroapp.users.viewmodels.repositories.GetPaymentsForContactRepository
import com.niro.niroapp.utils.DateUtils
import com.niro.niroapp.utils.FilterResultsListener
import com.niro.niroapp.utils.NiroAppConstants
import com.niro.niroapp.viewmodels.ListViewModel

class ContactDetailPaymentsViewModel( currentUser : User?) : ListViewModel(),
    FilterResultsListener<UserPayment> {

    private val currentUserData = MutableLiveData<User>()
    private val paymentsList = MutableLiveData<MutableList<UserPayment>>()
    private val amountPrefix = MutableLiveData<String> ("")
    private val paymentAmountDisplay = MutableLiveData<String>("")
    private val selectedContactId = MutableLiveData<String>()
    private var adapter : ContactDetailPaymentsAdapter

    init {
        currentUserData.value = currentUser
        adapter = ContactDetailPaymentsAdapter(getViewType(),paymentsList.value,this)
    }


    fun getPaymentsList() = paymentsList

    fun getAmountPrefix() = amountPrefix

    fun getAdapter() = adapter

    fun getSelectedContactId() = selectedContactId


    fun getPaymentsForContact(context: Context?)  : MutableLiveData<APIResponse>? =
        GetPaymentsForContactRepository(GetOrdersForContactPostData(currentUserData.value?.id,selectedContactId.value),context).getResponse()


    fun getPaymentDate(position : Int) = MutableLiveData<String>().apply {

        this.value =  if(paymentsList.value.isNullOrEmpty()) ""
        else {
            DateUtils.convertDate(paymentsList.value?.get(position)?.paymentDate,
                NiroAppConstants.POST_DATE_FORMAT,
                NiroAppConstants.DISPLAY_DATE_FORMAT) ?: ""

        }


    }


    fun getPaymentAmountDisplayValue(position: Int) =  MutableLiveData<String>().apply {

        this.value =
            if(paymentsList.value.isNullOrEmpty()) ""
            else { "${amountPrefix.value} ${paymentsList.value?.get(position)?.paymentAmount.toString()}".trim() }
    }



    override fun getViewType(): Int = R.layout.user_detail_card_payment

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
