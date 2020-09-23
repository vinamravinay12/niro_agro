package com.niro.niroapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.niro.niroapp.adapters.viewholders.DailyMandiRateViewHolder
import com.niro.niroapp.adapters.viewholders.GenericViewHolder
import com.niro.niroapp.adapters.viewholders.MandiLocationViewHolder
import com.niro.niroapp.databinding.CardLiveMandiBinding
import com.niro.niroapp.databinding.CardMandiDetailsBinding
import com.niro.niroapp.models.responsemodels.MandiLocation
import com.niro.niroapp.models.responsemodels.MandiRatesRecord
import com.niro.niroapp.utils.FilterResultsListener

class DailyMandiRateItemAdapter(private val layoutRes : Int,
                                private var dataList: MutableList<MandiRatesRecord>?,
                                private val filterResultListener: FilterResultsListener<MandiRatesRecord>
) :
    GenericRecyclerAdapter<MandiRatesRecord>(dataList = dataList,filterResultListener = filterResultListener) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GenericViewHolder<MandiRatesRecord> {
        val inflater = LayoutInflater.from(parent.context)
        val bindingCardLiveMandi = DataBindingUtil.inflate<CardLiveMandiBinding>(inflater,layoutRes,parent,false)
        return DailyMandiRateViewHolder(bindingCardLiveMandi,getVariablesMap())
    }


}