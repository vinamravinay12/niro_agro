package com.niro.niroapp.viewmodels

import androidx.lifecycle.ViewModel

abstract class ListViewModel : ViewModel() {

    abstract fun getViewType(): Int
    abstract fun updateList()

}