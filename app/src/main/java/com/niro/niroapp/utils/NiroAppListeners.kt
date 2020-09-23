package com.niro.niroapp.utils

import android.content.Context
import com.niro.niroapp.models.responsemodels.Searchable


interface ItemClickListener {
    fun onItemClick(item : Any?)
}


interface ViewInteractionHandler {
    fun setContext(context: Context)
}


interface KeyItemActionListener {
    fun onKeyEvent(valueEntered: String, data: Any?)
}

interface CheckChangeListener {
    fun onCheckChanged(item : Any?)
}

interface FilterResultsListener<E:Searchable> {
    fun onResultsFiltered(filteredList : List<E>)
}

interface DateChangeListener {
    fun onDateChanged(date : String?)
}

interface CallUserListener {
    fun callUser(number : String)
}