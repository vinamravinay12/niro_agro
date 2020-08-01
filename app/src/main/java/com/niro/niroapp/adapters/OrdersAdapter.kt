package com.niro.niroapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.niro.niroapp.adapters.viewholders.GenericViewHolder
import com.niro.niroapp.adapters.viewholders.OrderViewHolder
import com.niro.niroapp.databinding.CardOrderDetailBinding
import com.niro.niroapp.databinding.CardUserBinding
import com.niro.niroapp.models.responsemodels.UserContact
import com.niro.niroapp.models.responsemodels.UserOrder
import com.niro.niroapp.users.adapters.viewholders.UserViewHolder
import com.niro.niroapp.utils.FilterResultsListener
import com.niro.niroapp.viewmodels.OrdersViewModel

class OrdersAdapter(private val layoutRes : Int,
                    private var dataList: MutableList<UserOrder>?,
                    private val filterResultListener: FilterResultsListener<UserOrder>) : GenericRecyclerAdapter<UserOrder>(dataList,filterResultListener) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder<UserOrder> {

        val inflater = LayoutInflater.from(parent.context)
        val bindingCardOrder = DataBindingUtil.inflate<CardOrderDetailBinding>(inflater,layoutRes,parent,false)
        return OrderViewHolder(bindingCardOrder,getVariablesMap())
    }

}
