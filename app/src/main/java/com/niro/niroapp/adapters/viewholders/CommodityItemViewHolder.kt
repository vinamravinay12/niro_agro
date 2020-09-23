package com.niro.niroapp.adapters.viewholders

import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.niro.niroapp.R
import com.niro.niroapp.databinding.CardCommodityItemBinding
import com.niro.niroapp.models.responsemodels.CommodityItem
import com.squareup.picasso.Picasso

class CommodityItemViewHolder(
    private val viewBinding: CardCommodityItemBinding,
    private val variableMap: HashMap<Int, Any?>
) : GenericViewHolder<CommodityItem>(viewBinding) {

    override fun bind(item: CommodityItem, position: Int) {

        viewBinding.commodity = item
        viewBinding.position = position
        setVariables(variablesMap = variableMap)

        viewBinding.cvCommodityItem.isSelected = item.isSelected
        viewBinding.cbSelectItem.isChecked = item.isSelected
        viewBinding.ivItemSelected.visibility = if (item.isSelected) View.VISIBLE else View.INVISIBLE



        Glide.with(viewBinding.ivCommodity).load(item.image).thumbnail(0.2f)
            .diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().placeholder(
            R.drawable.rice
        ).into(viewBinding.ivCommodity)

        viewBinding.parentLayout.setOnClickListener{

            viewBinding.cbSelectItem.isChecked = true
            viewBinding.ivItemSelected.visibility = View.VISIBLE
            viewBinding.itemClickListener?.onItemClick(item)
        }
//        viewBinding.cbSelectItem.setOnCheckedChangeListener { buttonView, isChecked ->
//
//            if(buttonView.isShown) {
//
//                viewBinding.ivItemSelected.visibility =
//                    if (isChecked) View.VISIBLE else View.INVISIBLE
//                viewBinding.checkChangeListener?.onCheckChanged(item)
//            }
//
//        }
    }

}