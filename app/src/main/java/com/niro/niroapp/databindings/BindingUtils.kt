package com.niro.niroapp.databindings

import android.view.View
import androidx.databinding.BindingAdapter

object BindingUtils {

    @BindingAdapter("isSelected")
    @JvmStatic fun setSelected(view : View, isSelected : Boolean) {
        view.isSelected = isSelected
    }
}