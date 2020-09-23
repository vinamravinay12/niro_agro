package com.niro.niroapp.adapters.viewholders

import com.niro.niroapp.R
import com.niro.niroapp.databinding.CardOrderDetailBinding
import com.niro.niroapp.databinding.CardPaymentDetailBinding
import com.niro.niroapp.models.responsemodels.UserOrder
import com.niro.niroapp.models.responsemodels.UserPayment
import com.niro.niroapp.viewmodels.PaymentMode
import com.squareup.picasso.Picasso

class PaymentViewHolder(private val viewBinding : CardPaymentDetailBinding, private val variables : HashMap<Int,Any?>)
    : GenericViewHolder<UserPayment>(viewBinding){

    override fun bind(item: UserPayment, position: Int) {

        viewBinding.position = position
        setVariables(variables)
        viewBinding.tvPaymentMode.text = if(item.paymentMode == PaymentMode.ONLINE.name)viewBinding.root.context.getText(R.string.online_paid)
        else viewBinding.root.context.getText(R.string.paid_by_cash)
    }
}