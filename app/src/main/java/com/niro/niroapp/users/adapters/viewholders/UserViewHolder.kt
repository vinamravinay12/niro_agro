package com.niro.niroapp.users.adapters.viewholders

import android.content.Intent
import android.net.Uri
import android.view.View
import com.niro.niroapp.adapters.viewholders.GenericViewHolder
import com.niro.niroapp.databinding.CardUserBinding
import com.niro.niroapp.models.responsemodels.UserContact
import com.niro.niroapp.users.fragments.ContactType


class UserViewHolder(private val viewBinding : CardUserBinding, private val variables : HashMap<Int,Any?>) : GenericViewHolder<UserContact>(viewBinding) {

    override fun bind(item: UserContact, position: Int) {

        viewBinding.userContact = item
        setVariables(variables)

        viewBinding.cbUserDetail.setOnClickListener { viewBinding.itemClickListener?.onItemClick(item) }

       viewBinding.tvCallNow.setOnClickListener { viewBinding.callUserListener?.callUser(item.phoneNumber ?: "")}
    }




}