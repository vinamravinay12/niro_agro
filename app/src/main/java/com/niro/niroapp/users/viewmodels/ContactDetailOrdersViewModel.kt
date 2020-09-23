package com.niro.niroapp.users.viewmodels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.niro.niroapp.R
import com.niro.niroapp.models.responsemodels.OrderSummary
import com.niro.niroapp.models.responsemodels.User
import com.niro.niroapp.models.responsemodels.UserOrder
import com.niro.niroapp.users.adapters.ContactDetailOrdersAdapter
import com.niro.niroapp.users.models.GetOrdersForContactPostData
import com.niro.niroapp.users.viewmodels.repositories.GetOrdersForContactRepository
import com.niro.niroapp.utils.DateUtils
import com.niro.niroapp.utils.FilterResultsListener
import com.niro.niroapp.utils.NiroAppConstants
import com.niro.niroapp.viewmodels.ListViewModel

class ContactDetailOrdersViewModel(currentUser : User?) : ListViewModel(),
    FilterResultsListener<UserOrder> {

    private val currentUserData = MutableLiveData<User>()
    private val ordersSummary = MutableLiveData<OrderSummary>()
    private val userOrdersList = MutableLiveData<MutableList<UserOrder>>()
    private val amountPrefix = MutableLiveData<String> ("")
    private var adapter : ContactDetailOrdersAdapter
    private var selectedContactId = MutableLiveData<String>()

    init {
        currentUserData.value = currentUser
        adapter = ContactDetailOrdersAdapter(getViewType(),userOrdersList.value,this)
    }


    fun getAdapter() = adapter
    fun getCurrentUserData() = currentUserData
    fun getOrdersSummary() = ordersSummary
    fun getUserOrdersList() = userOrdersList
    fun getAmountPrefix() = amountPrefix
    fun getSelectedContactId() = selectedContactId


    fun getOrdersForContact(context: Context?)  = GetOrdersForContactRepository(GetOrdersForContactPostData(currentUserData.value?.id,selectedContactId.value),context).getResponse()


    fun getOrderReceivingDisplayDate(position : Int) : MutableLiveData<String> {
        return if(userOrdersList.value.isNullOrEmpty()) MutableLiveData("")
        else {
            val convertedDate : String? = DateUtils.convertDate(userOrdersList.value?.get(position)?.receivingDate,
                NiroAppConstants.POST_DATE_FORMAT,
                NiroAppConstants.DISPLAY_DATE_FORMAT)
            return MutableLiveData(convertedDate ?: "")
        }
    }


    fun getOrderAmountDisplayValue(position: Int) : MutableLiveData<String> {
        val amountDisplayValue = MutableLiveData<String>()
        amountDisplayValue.value =
            if(userOrdersList.value.isNullOrEmpty()) ""
            else {
                val amountString : String = userOrdersList.value?.get(position)?.orderAmount.toString() ?: "0"
                "${amountPrefix.value} $amountString".trim()
            }
        return amountDisplayValue
    }



   fun getSelectedCommodityName(position: Int) = MutableLiveData<String>().apply {
       this.value = when {
           userOrdersList.value.isNullOrEmpty() -> ""
           userOrdersList.value?.get(position)?.orderCommodity.isNullOrEmpty() -> ""
           else -> userOrdersList.value?.get(position)?.orderCommodity?.get(0)?.name
       }
   }


    fun getOrderNumber(position: Int) = MutableLiveData<String>().apply {
        this.value =  if(userOrdersList.value.isNullOrEmpty()) ""
        else "#00${userOrdersList.value?.get(position)?.orderNumber}"
    }

    override fun getViewType(): Int = R.layout.user_detail_card_order

    override fun updateList() {
        adapter.updateList(userOrdersList.value)
    }

    override fun onResultsFiltered(filteredList: List<UserOrder>) {
        userOrdersList.value?.clear()
        userOrdersList.value = filteredList.toMutableList()
        adapter.updateFilterList(filteredList.toMutableList())
    }

    fun getTotalOrderAmount(): Int {
        return if(userOrdersList.value.isNullOrEmpty()) 0
        else {
            val ordersList = userOrdersList.value
            val sum  = ordersList?.sumBy { userOrder -> (userOrder.orderAmount.toInt()) }
            return sum ?: 0
        }
    }


}