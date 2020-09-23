package com.niro.niroapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.niro.niroapp.adapters.viewholders.BuyCommoditiesListViewHolder
import com.niro.niroapp.adapters.viewholders.GenericViewHolder
import com.niro.niroapp.databinding.CardBuyCommodityBinding
import com.niro.niroapp.models.responsemodels.BuyCommodity
import com.niro.niroapp.utils.FilterResultsListener

class BuyCommoditiesListAdapter(private val layoutRes : Int,
                                private var dataList: MutableList<BuyCommodity>?,
                                private val filterResultListener: FilterResultsListener<BuyCommodity>
) : GenericRecyclerAdapter<BuyCommodity>(dataList,filterResultListener) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder<BuyCommodity> {

        val inflater = LayoutInflater.from(parent.context)
        val bindingCardBuyCommodity = DataBindingUtil.inflate<CardBuyCommodityBinding>(inflater,layoutRes,parent,false)
        return BuyCommoditiesListViewHolder(bindingCardBuyCommodity,getVariablesMap())
    }

}