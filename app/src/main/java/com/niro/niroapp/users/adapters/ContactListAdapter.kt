package com.niro.niroapp.users.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.niro.niroapp.adapters.GenericRecyclerAdapter
import com.niro.niroapp.adapters.viewholders.CommodityItemViewHolder
import com.niro.niroapp.adapters.viewholders.GenericViewHolder
import com.niro.niroapp.databinding.CardCommodityItemBinding
import com.niro.niroapp.databinding.CardContactBinding
import com.niro.niroapp.models.responsemodels.CommodityItem
import com.niro.niroapp.models.responsemodels.Contact
import com.niro.niroapp.users.adapters.viewholders.ContactViewHolder
import com.niro.niroapp.utils.FilterResultsListener


class ContactListAdapter(private val layoutRes : Int,
                         private var dataList: MutableList<Contact>?,
                         private val filterResultListener: FilterResultsListener<Contact>) : GenericRecyclerAdapter<Contact>(dataList,filterResultListener) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder<Contact> {

        val inflater = LayoutInflater.from(parent.context)
        val bindingCardContact = DataBindingUtil.inflate<CardContactBinding>(inflater,layoutRes,parent,false)
        return ContactViewHolder(bindingCardContact,getVariablesMap())

    }
}