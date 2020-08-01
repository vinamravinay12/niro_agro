package com.niro.niroapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil

import com.niro.niroapp.adapters.viewholders.CommodityItemViewHolder
import com.niro.niroapp.adapters.viewholders.GenericViewHolder
import com.niro.niroapp.databinding.CardCommodityItemBinding
import com.niro.niroapp.models.responsemodels.CommodityItem

import com.niro.niroapp.utils.FilterResultsListener

class CommoditiesListAdapter(private val layoutRes : Int,
                             private var dataList: MutableList<CommodityItem>?,
                             private val filterResultListener: FilterResultsListener<CommodityItem>)
    : GenericRecyclerAdapter<CommodityItem>(dataList = dataList,filterResultListener = filterResultListener) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder<CommodityItem> {
        val inflater = LayoutInflater.from(parent.context)
        val bindingCommodityItem = DataBindingUtil.inflate<CardCommodityItemBinding>(inflater,layoutRes,parent,false)
        return CommodityItemViewHolder(bindingCommodityItem,getVariablesMap())
    }

}