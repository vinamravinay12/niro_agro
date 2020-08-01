package com.niro.niroapp.databindings

import android.view.View
import android.widget.CompoundButton
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.niro.niroapp.R
import com.niro.niroapp.utils.CheckChangeListener
import com.niro.niroapp.utils.ItemClickListener

object BindingUtils {

    @BindingAdapter("isSelected")
    @JvmStatic fun setSelected(view : View, isSelected : Boolean) {
        view.isSelected = isSelected
    }

    @BindingAdapter("loadImage")
    @JvmStatic fun loadImage(imageView : ImageView, imageUrl : String) {
        Glide.with(imageView.context).load(imageUrl).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().placeholder(
            R.drawable.rice).into(imageView)
    }

    @BindingAdapter(value = ["itemChecked", "checkedItem"],requireAll = true)
    @JvmStatic fun setCheckChange(checkBox: CompoundButton, checkListener : CheckChangeListener, item : Any? ) {
        checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked) checkListener.onCheckChanged(item)
        }
    }

    @BindingAdapter(value = ["onClick","selectedItem"],requireAll = true)
    @JvmStatic fun onClick(view : View, itemClickListener: ItemClickListener,item : Any?) {
        view.isSelected = true
        itemClickListener.onItemClick(item)
    }



}