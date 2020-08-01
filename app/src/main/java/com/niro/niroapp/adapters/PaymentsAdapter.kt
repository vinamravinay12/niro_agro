package com.niro.niroapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.niro.niroapp.adapters.viewholders.GenericViewHolder
import com.niro.niroapp.adapters.viewholders.OrderViewHolder
import com.niro.niroapp.adapters.viewholders.PaymentViewHolder
import com.niro.niroapp.databinding.CardOrderDetailBinding
import com.niro.niroapp.databinding.CardPaymentDetailBinding
import com.niro.niroapp.models.responsemodels.UserOrder
import com.niro.niroapp.models.responsemodels.UserPayment
import com.niro.niroapp.utils.FilterResultsListener

class PaymentsAdapter (private val layoutRes : Int,
                       private var dataList: MutableList<UserPayment>?,
                       private val filterResultListener: FilterResultsListener<UserPayment>
) : GenericRecyclerAdapter<UserPayment>(dataList,filterResultListener) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder<UserPayment> {

        val inflater = LayoutInflater.from(parent.context)
        val bindingCardPaymentDetail = DataBindingUtil.inflate<CardPaymentDetailBinding>(inflater,layoutRes,parent,false)
        return PaymentViewHolder(bindingCardPaymentDetail,getVariablesMap())
    }

}
