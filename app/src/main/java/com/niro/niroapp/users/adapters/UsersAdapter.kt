package com.niro.niroapp.users.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.niro.niroapp.adapters.GenericRecyclerAdapter
import com.niro.niroapp.adapters.viewholders.GenericViewHolder
import com.niro.niroapp.databinding.CardContactBinding
import com.niro.niroapp.databinding.CardUserBinding
import com.niro.niroapp.models.responsemodels.Contact
import com.niro.niroapp.models.responsemodels.UserContact
import com.niro.niroapp.users.adapters.viewholders.ContactViewHolder
import com.niro.niroapp.users.adapters.viewholders.UserViewHolder
import com.niro.niroapp.utils.FilterResultsListener

class UsersAdapter(private val layoutRes : Int,
                   private var dataList: MutableList<UserContact>?,
                   private val filterResultListener: FilterResultsListener<UserContact>) : GenericRecyclerAdapter<UserContact>(dataList,filterResultListener) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder<UserContact> {
        val inflater = LayoutInflater.from(parent.context)
        val bindingUserCard = DataBindingUtil.inflate<CardUserBinding>(inflater,layoutRes,parent,false)
        return UserViewHolder(bindingUserCard,getVariablesMap())
    }


}