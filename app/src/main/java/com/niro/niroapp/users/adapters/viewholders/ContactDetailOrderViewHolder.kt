package com.niro.niroapp.users.adapters.viewholders

import com.bumptech.glide.Glide
import com.niro.niroapp.R
import com.niro.niroapp.adapters.viewholders.GenericViewHolder
import com.niro.niroapp.databinding.UserDetailCardOrderBinding
import com.niro.niroapp.models.responsemodels.UserOrder

class ContactDetailOrderViewHolder (private val viewBinding : UserDetailCardOrderBinding, private val variables : HashMap<Int,Any?>) : GenericViewHolder<UserOrder>(viewBinding) {

    override fun bind(item: UserOrder, position: Int) {

        viewBinding.position = position
        setVariables(variables)
        if(!item.orderCommodity.isNullOrEmpty()) {
            Glide.with(viewBinding.ivCommodityImage).load(item.orderCommodity[0].image).centerCrop().thumbnail(0.2f).placeholder(
                R.drawable.rice).into(viewBinding.ivCommodityImage)
        }


    }
}