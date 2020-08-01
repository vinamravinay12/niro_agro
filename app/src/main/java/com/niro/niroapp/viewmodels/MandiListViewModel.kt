package com.niro.niroapp.viewmodels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.niro.niroapp.R
import com.niro.niroapp.adapters.CommoditiesListAdapter
import com.niro.niroapp.adapters.MandiListAdapter
import com.niro.niroapp.models.APIResponse
import com.niro.niroapp.models.responsemodels.MandiLocation
import com.niro.niroapp.network.ApiClient
import com.niro.niroapp.utils.FilterResultsListener
import com.niro.niroapp.viewmodels.repositories.MandiLocationRepository

class MandiListViewModel : ListViewModel(), FilterResultsListener<MandiLocation> {

    private var mandiList = MutableLiveData<MutableList<MandiLocation>>()
    private var mandiListAdapter : MandiListAdapter? = null
    private var selectedMandiLocation = MutableLiveData<MandiLocation>()

    init {
        mandiListAdapter = MandiListAdapter(getViewType(),mandiList.value,this)
    }

    fun getAdapter() = mandiListAdapter

    fun getSelectedMandiLocation() = selectedMandiLocation

    fun setMandiList(mandiList : MutableList<MandiLocation>?) {
        this.mandiList.value = mandiList
    }

    fun getAllMandiLocations(context: Context?) : MutableLiveData<APIResponse> {
        val mandiLocationRepository = MandiLocationRepository<List<MandiLocation>>(context)
        return mandiLocationRepository.getResponse()
    }


    override fun getViewType(): Int {
        return R.layout.card_mandi_details
    }

    override fun updateList() {
        mandiListAdapter?.updateList(mandiList.value)
    }

    override fun onResultsFiltered(filteredList: List<MandiLocation>) {
        mandiList.value?.clear()
        mandiList.value = filteredList.toMutableList()
        mandiListAdapter?.updateFilterList(filteredList.toMutableList())
    }

    fun setSelectedMandiLocation(item: MandiLocation?,toSelect : Boolean) {
        if(item == null) return
        selectedMandiLocation.value = item
        val mandiLocation = mandiList.value?.find { mandiLocation -> mandiLocation.market.equals(item.market,true)  }
        mandiLocation?.isSelected = toSelect
    }

}