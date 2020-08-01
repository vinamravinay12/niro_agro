package com.niro.niroapp.adapters

import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.niro.niroapp.adapters.viewholders.GenericViewHolder
import com.niro.niroapp.models.responsemodels.Searchable
import com.niro.niroapp.utils.FilterResultsListener


abstract class GenericRecyclerAdapter<T : Searchable>(private var dataList: MutableList<T>?, private val filterResultListener: FilterResultsListener<T>) : RecyclerView.Adapter<GenericViewHolder<T>>(),Filterable {

    private val completeList = ArrayList<T>()

    init {

        if(dataList == null)dataList = ArrayList()
        completeList.clear()
        dataList?.let { completeList.addAll(it) }

    }

    private var variablesMap =  HashMap<Int,Any?>()



    fun getVariablesMap() = variablesMap

    abstract override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : GenericViewHolder<T>

    override fun onBindViewHolder(holder: GenericViewHolder<T>, position: Int) {
        val item = dataList?.get(position)
        item?.let { holder.bind(it,position) }
    }

    fun setVariablesMap(variablesMap : HashMap<Int,Any?> ) {
        this.variablesMap = variablesMap
    }



    override fun getItemCount(): Int {
        return dataList?.count() ?: 0
    }



    open fun updateList(newList : MutableList<T>?) {
        dataList?.clear()
        completeList.clear()
        newList?.let {
            dataList?.addAll(it)
            completeList.addAll(it)
        }

        notifyDataSetChanged()

    }

    fun updateFilterList(newList: MutableList<T>?) {
        dataList?.clear()
        newList?.let {
            dataList?.addAll(it)

        }

        notifyDataSetChanged()
    }


    fun getItem(position: Int) : Searchable? {
        return dataList?.get(position)
    }

    fun clear() {
        dataList?.clear()
        notifyDataSetChanged()
    }


    override fun getFilter(): Filter {

        return ResultsFilter(completeList.toMutableList(),filterResultListener)
    }

}