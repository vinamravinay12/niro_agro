package com.niro.niroapp.users.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.niro.niroapp.adapters.GenericRecyclerAdapter
import com.niro.niroapp.adapters.viewholders.GenericViewHolder
import com.niro.niroapp.databinding.UserDetailCardPaymentBinding
import com.niro.niroapp.models.responsemodels.UserPayment
import com.niro.niroapp.users.adapters.viewholders.ContactDetailPaymentsViewHolder
import com.niro.niroapp.utils.FilterResultsListener

class ContactDetailPaymentsAdapter(private val layoutRes : Int,
                                   private var dataList: MutableList<UserPayment>?,
                                   private val filterResultListener: FilterResultsListener<UserPayment>
) : GenericRecyclerAdapter<UserPayment>(dataList,filterResultListener) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder<UserPayment> {
        val inflater = LayoutInflater.from(parent.context)
        val bindingUserDetailPaymentsCard = DataBindingUtil.inflate<UserDetailCardPaymentBinding>(inflater,layoutRes,parent,false)
        return ContactDetailPaymentsViewHolder(bindingUserDetailPaymentsCard,getVariablesMap())
    }


}