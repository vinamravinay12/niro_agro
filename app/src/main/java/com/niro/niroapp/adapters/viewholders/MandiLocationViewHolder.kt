package com.niro.niroapp.adapters.viewholders

import android.view.View
import com.niro.niroapp.databinding.CardMandiDetailsBinding
import com.niro.niroapp.models.responsemodels.MandiLocation

class MandiLocationViewHolder(private val viewBinding : CardMandiDetailsBinding, private val variableMap : HashMap<Int,Any?>) : GenericViewHolder<MandiLocation>(viewBinding) {

    override fun bind(item: MandiLocation, position: Int) {

        viewBinding.mandLocation = item
        viewBinding.position = position
        setVariables(variablesMap = variableMap)

        viewBinding.cvMandiDetails.isSelected = item.isSelected
        viewBinding.rbSelectItem.isChecked = item.isSelected
        viewBinding.ivItemSelected.visibility = if (item.isSelected) View.VISIBLE else View.INVISIBLE


        viewBinding.rbSelectItem.setOnCheckedChangeListener{buttonView, isChecked ->

            if(buttonView.isShown) {

                item.isSelected = isChecked
                buttonView.isChecked = isChecked
                viewBinding.ivItemSelected.visibility =
                    if (isChecked) View.VISIBLE else View.INVISIBLE
                viewBinding.checkChangeListener?.onCheckChanged(item)
            }

        }
    }


}