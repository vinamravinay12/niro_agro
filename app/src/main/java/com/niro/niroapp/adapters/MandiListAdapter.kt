package com.niro.niroapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.niro.niroapp.adapters.viewholders.CommodityItemViewHolder
import com.niro.niroapp.adapters.viewholders.GenericViewHolder
import com.niro.niroapp.adapters.viewholders.MandiLocationViewHolder
import com.niro.niroapp.databinding.CardCommodityItemBinding
import com.niro.niroapp.databinding.CardMandiDetailsBinding
import com.niro.niroapp.models.responsemodels.MandiLocation
import com.niro.niroapp.utils.FilterResultsListener

class MandiListAdapter(private val layoutRes : Int,
                       private var dataList: MutableList<MandiLocation>?,
                       private val filterResultListener: FilterResultsListener<MandiLocation>) :
    GenericRecyclerAdapter<MandiLocation>(dataList = dataList,filterResultListener = filterResultListener) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GenericViewHolder<MandiLocation> {
        val inflater = LayoutInflater.from(parent.context)
        val bindingMandiLocation = DataBindingUtil.inflate<CardMandiDetailsBinding>(inflater,layoutRes,parent,false)
        return MandiLocationViewHolder(bindingMandiLocation,getVariablesMap())
    }

    fun updateSelectedItem(selectedLocation: MandiLocation?) {

        if(selectedLocation == null) return

        val index = dataList?.indexOfFirst { mandiLocation -> mandiLocation.market.equals(selectedLocation.market,true) }

        if(index != null) notifyItemChanged(index)
    }


}