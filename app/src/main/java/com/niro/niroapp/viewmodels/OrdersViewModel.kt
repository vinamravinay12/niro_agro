package com.niro.niroapp.viewmodels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.niro.niroapp.R
import com.niro.niroapp.adapters.OrdersAdapter
import com.niro.niroapp.models.APIResponse
import com.niro.niroapp.models.responsemodels.OrderSummary
import com.niro.niroapp.models.responsemodels.User
import com.niro.niroapp.models.responsemodels.UserOrder
import com.niro.niroapp.utils.DateUtils
import com.niro.niroapp.utils.FilterResultsListener
import com.niro.niroapp.utils.NiroAppConstants
import com.niro.niroapp.viewmodels.repositories.GetOrdersRepository
import com.niro.niroapp.viewmodels.repositories.OrdersRepository

class OrdersViewModel(currentUser : User?) : ListViewModel(), FilterResultsListener<UserOrder> {

    private val currentUserData = MutableLiveData<User>()
    private val ordersSummary = MutableLiveData<OrderSummary>()
    private val userOrdersList = MutableLiveData<MutableList<UserOrder>>()
    private val amountPrefix = MutableLiveData<String> ("")
    private var adapter : OrdersAdapter

    init {
        currentUserData.value = currentUser
        adapter = OrdersAdapter(getViewType(),userOrdersList.value,this)
    }


    fun getAdapter() = adapter
    fun getCurrentUserData() = currentUserData
    fun getOrdersSummary() = ordersSummary
    fun getUserOrdersList() = userOrdersList
    fun getAmountPrefix() = amountPrefix

    fun getOrderSummary(userId : String, context: Context?) : MutableLiveData<APIResponse> = OrdersRepository(userId,context).getResponse()

    fun getOrders(context: Context?)  : MutableLiveData<APIResponse>? = GetOrdersRepository(currentUserData.value?.id,context).getResponse()


    fun getOrderReceivingDisplayDate(position : Int) : MutableLiveData<String>  {
        return if(userOrdersList.value.isNullOrEmpty()) MutableLiveData("")
        else {
            val convertedDate : String? = DateUtils.convertDate(userOrdersList.value?.get(position)?.receivingDate,NiroAppConstants.POST_DATE_FORMAT,NiroAppConstants.DISPLAY_DATE_FORMAT)
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


    fun getContactName(position: Int) : MutableLiveData<String> {
        return if(userOrdersList.value.isNullOrEmpty()) MutableLiveData("")
        else MutableLiveData(userOrdersList.value?.get(position)?.userContact?.contactName ?: "")
    }

    fun getMandiLocation(position: Int) : MutableLiveData<String> {
        return if(userOrdersList.value.isNullOrEmpty()) MutableLiveData("")
        else {
            val mandiLocation = userOrdersList.value?.get(position)?.userContact?.userLocation

            return MutableLiveData("${mandiLocation?.market ?: ""}, ${mandiLocation?.state ?: "" }")
        }
    }


    override fun getViewType(): Int = R.layout.card_order_detail

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

    fun getCommodityName(position : Int) = MutableLiveData<String>().apply {
        this.value = when {
            userOrdersList.value.isNullOrEmpty() -> ""
            userOrdersList.value?.get(position)?.orderCommodity?.isNullOrEmpty() == true -> ""
            position < userOrdersList.value?.size ?: 0 -> userOrdersList.value?.get(position)?.orderCommodity?.get(0)?.name ?: ""
            else -> ""
        }
    }


}

