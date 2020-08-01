package com.niro.niroapp.adapters.viewholders

import com.niro.niroapp.R
import com.niro.niroapp.databinding.CardOrderDetailBinding
import com.niro.niroapp.models.responsemodels.UserOrder
import com.squareup.picasso.Picasso

class OrderViewHolder(private val viewBinding : CardOrderDetailBinding, private val variables : HashMap<Int,Any?>)  : GenericViewHolder<UserOrder>(viewBinding){

    override fun bind(item: UserOrder, position: Int) {

       viewBinding.position = position

        setVariables(variablesMap = variables)
        Picasso.get().load(item.orderCommodity?.image).centerCrop().placeholder(R.drawable.rice).into(viewBinding.ivCommodityImage)

        viewBinding.cvOrderDetail.setOnClickListener { viewBinding.itemCLickListener?.onItemClick(position) }

    }
}