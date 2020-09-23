package com.niro.niroapp.viewmodels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.niro.niroapp.R
import com.niro.niroapp.adapters.BuyCommoditiesListAdapter
import com.niro.niroapp.models.APIResponse
import com.niro.niroapp.models.postdatamodels.GetSellerCommoditiesPostData
import com.niro.niroapp.models.responsemodels.BuyCommodity
import com.niro.niroapp.models.responsemodels.UserType
import com.niro.niroapp.utils.DateUtils
import com.niro.niroapp.utils.FilterResultsListener
import com.niro.niroapp.utils.NiroAppConstants
import com.niro.niroapp.viewmodels.repositories.GetSellerCommoditiesRepository

class BuyCommoditiesViewModel(userId : String?, userType : String?) : ListViewModel(), FilterResultsListener<BuyCommodity> {


    private val buyCommodities = MutableLiveData<MutableList<BuyCommodity>>()
    private val amountPrefix = MutableLiveData<String> ("")
    private var adapter : BuyCommoditiesListAdapter
    private var quantityAvailablePrefix = MutableLiveData<String>("")
    private var dispatchDatePrefix = MutableLiveData<String>("")
    private val currentUserId = MutableLiveData<String>("")
    private val currentUserType = MutableLiveData<String>(UserType.COMMISSION_AGENT.name)

    init {
        currentUserId.value = userId
        currentUserType.value = userType
        adapter = BuyCommoditiesListAdapter(getViewType(),buyCommodities.value,this)
    }


    fun getAdapter() = adapter

    fun getBuyCommoditiesList() = buyCommodities

    fun getAmountPrefix() = amountPrefix


    fun fetchBuyCommoditiesList(context: Context?)  : MutableLiveData<APIResponse>? {
       return GetSellerCommoditiesRepository(GetSellerCommoditiesPostData(currentUserId.value,currentUserType.value,0,200),context).getResponse()
    }

    fun getCommodityPrice(position : Int) = MutableLiveData<String>().apply {
        this.value =  if (buyCommodities.value.isNullOrEmpty() || position >= buyCommodities.value?.size ?: 0) ""
        else {
            "${amountPrefix.value} ${buyCommodities.value?.get(position)?.price ?: 0}/${buyCommodities.value?.get(position)?.quantityType}"
        }
        return this
    }


    fun getContactName(position: Int) = MutableLiveData<String>().apply {

        this.value =  if (buyCommodities.value.isNullOrEmpty() || position >= buyCommodities.value?.size ?: 0) ""
        else {
            "${buyCommodities.value?.get(position)?.userDetails?.fullName}"
        }
        return this
    }

    fun getBusinessName(position : Int) = MutableLiveData<String>().apply {
        this.value = if(buyCommodities.value.isNullOrEmpty() || position >= buyCommodities.value?.size ?: 0) ""
        else "${buyCommodities.value?.get(position)?.userDetails?.businessName}"
    }


    fun getQuantityAvailable(position: Int) = MutableLiveData<String>().apply {

        this.value =  if (buyCommodities.value.isNullOrEmpty() || position >= buyCommodities.value?.size ?: 0) ""
        else {
            "${quantityAvailablePrefix.value} : ${buyCommodities.value?.get(position)?.quantityAvailable} ${buyCommodities.value?.get(position)?.quantityType}"
        }
        return this
    }

    fun getDispatchDate(position : Int) = MutableLiveData<String>().apply {

        this.value =  if (buyCommodities.value.isNullOrEmpty() || position >= buyCommodities.value?.size ?: 0) ""
        else {
            val convertedDate : String? = DateUtils.convertDate(buyCommodities.value?.get(position)?.dispatchDate,
                NiroAppConstants.POST_DATE_FORMAT,
                NiroAppConstants.DISPLAY_DATE_FORMAT)
            "${dispatchDatePrefix.value} : $convertedDate"
        }
        return this
    }

    fun getCommodityName(position: Int) = MutableLiveData<String>().apply {

        this.value =  if (buyCommodities.value.isNullOrEmpty() || position >= buyCommodities.value?.size ?: 0) ""
        else {
            val convertedDate : String? = DateUtils.convertDate(buyCommodities.value?.get(position)?.dispatchDate,
                NiroAppConstants.POST_DATE_FORMAT,
                NiroAppConstants.DISPLAY_DATE_FORMAT)
            "${buyCommodities.value?.get(position)?.commodity ?: ""}"
        }
        return this
    }



    fun getMandiLocation(position: Int) : MutableLiveData<String> {
        return if(buyCommodities.value.isNullOrEmpty() || position >= buyCommodities.value?.size ?: 0) MutableLiveData("")
        else {
            val mandiLocation = buyCommodities.value?.get(position)?.userDetails?.selectedMandi

            return MutableLiveData("${mandiLocation?.market ?: ""}, ${mandiLocation?.state ?: "" }")
        }
    }


    override fun getViewType(): Int = R.layout.card_buy_commodity

    override fun updateList() {
        adapter.updateList(buyCommodities.value)
    }

    override fun onResultsFiltered(filteredList: List<BuyCommodity>) {
        buyCommodities.value?.clear()
        buyCommodities.value = filteredList.toMutableList()
        adapter.updateFilterList(filteredList.toMutableList())
    }

    fun initializePrefixes(pricePrefix : String,quantityPrefix : String, datePrefix : String) {
        amountPrefix.value = pricePrefix
        quantityAvailablePrefix.value = quantityPrefix
        dispatchDatePrefix.value = datePrefix
    }

}
