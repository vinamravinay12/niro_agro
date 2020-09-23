package com.niro.niroapp.users.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.niro.niroapp.adapters.GenericRecyclerAdapter
import com.niro.niroapp.adapters.viewholders.GenericViewHolder
import com.niro.niroapp.databinding.UserDetailCardOrderBinding
import com.niro.niroapp.models.responsemodels.UserOrder
import com.niro.niroapp.users.adapters.viewholders.ContactDetailOrderViewHolder
import com.niro.niroapp.utils.FilterResultsListener

class ContactDetailOrdersAdapter(private val layoutRes : Int,
                                 private var dataList: MutableList<UserOrder>?,
                                 private val filterResultListener: FilterResultsListener<UserOrder>
) : GenericRecyclerAdapter<UserOrder>(dataList,filterResultListener) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder<UserOrder> {
        val inflater = LayoutInflater.from(parent.context)
        val bindingUserDetailOrderCard = DataBindingUtil.inflate<UserDetailCardOrderBinding>(inflater,layoutRes,parent,false)
        return ContactDetailOrderViewHolder(bindingUserDetailOrderCard,getVariablesMap())
    }


}