package com.niro.niroapp.viewmodels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.niro.niroapp.R
import com.niro.niroapp.adapters.DailyMandiRateItemAdapter
import com.niro.niroapp.models.postdatamodels.GetMandiRatesPostData
import com.niro.niroapp.models.responsemodels.MandiLocation
import com.niro.niroapp.models.responsemodels.MandiRatesRecord
import com.niro.niroapp.models.responsemodels.User
import com.niro.niroapp.users.viewmodels.repositories.GetMandiRatesForDateRepository
import com.niro.niroapp.utils.DateUtils
import com.niro.niroapp.utils.FilterResultsListener
import com.niro.niroapp.utils.NiroAppConstants
import java.util.*

class DailyMandiRateListViewModel : ListViewModel(),FilterResultsListener<MandiRatesRecord> {

    private val mandiRates = MutableLiveData<MutableList<MandiRatesRecord>>()

    private val currentUser = MutableLiveData<User>()

    private val selectedMandi = MutableLiveData<MandiLocation>()

    private var adapter : DailyMandiRateItemAdapter




    init {
        adapter = DailyMandiRateItemAdapter(getViewType(),mandiRates.value,this)
    }

    fun getAdapter() = adapter

    fun getMandiRates() = mandiRates

    fun getCurrentUser() = currentUser

    fun getSelectedMandi() = selectedMandi

    fun setMandiRates(mandiRateList : MutableList<MandiRatesRecord>) {
        mandiRates.value = mandiRateList
    }

    fun getCommodityName(position : Int) = MutableLiveData<String>().apply {
        this.value = if(mandiRates.value?.isNullOrEmpty() == true) ""
        else mandiRates.value?.get(position)?.commodity
    }


    fun getModalPrice(position: Int) = MutableLiveData<String>().apply {
        this.value = if(mandiRates.value?.isNullOrEmpty() == true) ""
        else mandiRates.value?.get(position)?.modal_price
    }


    fun getMandiRatesForDate(date  :String, context: Context?) =
        GetMandiRatesForDateRepository(GetMandiRatesPostData(date= date, market = selectedMandi.value?.market ?: ""),context).getResponse()


    fun getMandiLocationDisplayValue() = MutableLiveData<String>().apply {
        this.value =  if(selectedMandi.value == null) ""
        else "${selectedMandi.value?.market ?: ""}, ${selectedMandi.value?.state ?: "" }"
    }


    fun getDate() = MutableLiveData<String>().apply {
       this.value =  DateUtils.getDateString(Date(),NiroAppConstants.POST_DATE_FORMAT)
    }


    override fun getViewType() = R.layout.card_live_mandi

    override fun updateList() {
        adapter.updateList(mandiRates.value)
    }



    override fun onResultsFiltered(filteredList: List<MandiRatesRecord>) {
        mandiRates.value?.clear()
        mandiRates.value = filteredList.toMutableList()
        adapter.updateFilterList(filteredList.toMutableList())
    }

}