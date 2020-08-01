package com.niro.niroapp.adapters.viewholders

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.niro.niroapp.models.responsemodels.Searchable

abstract class GenericViewHolder<T:Searchable>(private val viewBinding : ViewDataBinding) : RecyclerView.ViewHolder(viewBinding.root) {

    abstract fun bind(item: T, position : Int)

    fun setVariables(variablesMap : HashMap<Int,Any?>) {
        if(variablesMap.size == 0) return
        for(key in variablesMap.keys) {
            viewBinding.setVariable(key,variablesMap[key])
        }
    }

}