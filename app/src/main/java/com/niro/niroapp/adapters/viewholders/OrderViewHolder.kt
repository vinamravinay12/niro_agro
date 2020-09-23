package com.niro.niroapp.adapters.viewholders

import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.niro.niroapp.R
import com.niro.niroapp.databinding.CardOrderDetailBinding
import com.niro.niroapp.models.responsemodels.UserOrder
import com.squareup.picasso.Picasso

class OrderViewHolder(private val viewBinding : CardOrderDetailBinding, private val variables : HashMap<Int,Any?>)  : GenericViewHolder<UserOrder>(viewBinding){

    override fun bind(item: UserOrder, position: Int) {

       viewBinding.position = position

        setVariables(variablesMap = variables)
        if(item.orderCommodity?.size == 1 && !item.orderCommodity[0].image.isNullOrEmpty())  {
            Glide.with(viewBinding.ivCommodityImage).load(item.orderCommodity[0].image).thumbnail(0.2f)
                .diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().placeholder(
                    R.drawable.rice
                ).into(viewBinding.ivCommodityImage)
        }


        viewBinding.cvOrderDetail.setOnClickListener { viewBinding.itemCLickListener?.onItemClick(item) }

    }
}