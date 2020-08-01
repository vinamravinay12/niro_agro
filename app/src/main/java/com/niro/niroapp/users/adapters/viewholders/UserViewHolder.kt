package com.niro.niroapp.users.adapters.viewholders

import com.niro.niroapp.adapters.viewholders.GenericViewHolder
import com.niro.niroapp.databinding.CardUserBinding
import com.niro.niroapp.models.responsemodels.UserContact


class UserViewHolder(private val viewBinding : CardUserBinding, private val variables : HashMap<Int,Any?>) : GenericViewHolder<UserContact>(viewBinding) {

    override fun bind(item: UserContact, position: Int) {

        viewBinding.userContact = item
        setVariables(variables)

        viewBinding.cbUserDetail.setOnClickListener { viewBinding.itemClickListener?.onItemClick(item) }
    }
}