package com.niro.niroapp.adapters

import android.text.TextUtils
import android.widget.Filter
import com.niro.niroapp.models.responsemodels.*
import com.niro.niroapp.utils.FilterResultsListener

class ResultsFilter<T:Searchable>(
    private val completeList: MutableList<T>?,
    private val filterResultListener: FilterResultsListener<T>
) : Filter() {

    override fun performFiltering(constraint: CharSequence?): FilterResults {
        val filteredList = ArrayList<Searchable>()
        if (TextUtils.isEmpty(constraint)) {
            completeList?.let { filteredList.addAll(it) }
        } else {
            val filterPattern = constraint.toString().toLowerCase().trim()
            completeList?.let { itemList ->
                for (item in itemList) {

                    when (item) {
                        is CommodityItem -> {
                            if ((item.name ?: "").contains(
                                    filterPattern,
                                    ignoreCase = true
                                )
                            ) filteredList.add(item)
                        }

                        is MandiLocation -> {
                            if ((item.market ?: "").contains(filterPattern, ignoreCase = true) ||
                                (item.district ?: "").contains(filterPattern, true) ||
                                (item.state ?: "").contains(filterPattern, true)
                            ) filteredList.add(item)
                        }

                        is Contact -> {
                            if((item.name ?: "").contains(filterPattern,true) || (item.number ?: "").contains(filterPattern,true)) filteredList.add(item)
                        }

                        is UserContact -> {
                            if((item.contactName ?: "").contains(filterPattern,true) ||
                                (item.phoneNumber ?: "").contains(filterPattern,true))filteredList.add(item)
                        }

                        is UserOrder -> {
                            if((item.userContact?.contactName ?: "").contains(filterPattern,true) ||
                                (item.userContact?.phoneNumber ?: "").contains(filterPattern,true))filteredList.add(item)
                        }

                    }
                }
            }
        }

        val filteredResults = FilterResults()
        filteredResults.values = filteredList
        return filteredResults
    }

    override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
        filterResultListener.onResultsFiltered(results?.values as List<T>)
    }
}