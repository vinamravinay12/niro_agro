package com.niro.niroapp.users.adapters.viewholders

import com.niro.niroapp.adapters.viewholders.GenericViewHolder
import com.niro.niroapp.databinding.CardContactBinding
import com.niro.niroapp.models.responsemodels.Contact

class ContactViewHolder(private val viewBinding : CardContactBinding,private val variables : HashMap<Int,Any?>) : GenericViewHolder<Contact>(viewBinding) {


    override fun bind(item: Contact, position: Int) {

        viewBinding.contact = item

        setVariables(variablesMap = variables)

        viewBinding.cvContact.setOnClickListener { viewBinding.itemClickListener?.onItemClick(item) }


    }
}