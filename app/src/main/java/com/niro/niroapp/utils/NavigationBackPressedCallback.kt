package com.niro.niroapp.utils

import androidx.activity.OnBackPressedCallback

interface OnBackPressedListener {
    fun onBackPressed()
}
class NavigationBackPressedCallback(private val onBackPressedListener: OnBackPressedListener) : OnBackPressedCallback(true){

    override fun handleOnBackPressed() {
        onBackPressedListener.onBackPressed()
    }
}