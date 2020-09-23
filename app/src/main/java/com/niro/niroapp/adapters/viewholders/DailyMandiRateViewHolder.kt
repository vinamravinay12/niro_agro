package com.niro.niroapp.adapters.viewholders

import com.niro.niroapp.databinding.CardLiveMandiBinding
import com.niro.niroapp.models.responsemodels.MandiRatesRecord

class DailyMandiRateViewHolder(private val viewBinding : CardLiveMandiBinding, private val variables : HashMap<Int,Any?>) :
    GenericViewHolder<MandiRatesRecord>(viewBinding) {

    override fun bind(item: MandiRatesRecord, position: Int) {
        viewBinding.position = position
        viewBinding.mandiRate = item
        setVariables(variables)

    }
}