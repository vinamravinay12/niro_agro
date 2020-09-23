package com.niro.niroapp.users.adapters.viewholders

import com.niro.niroapp.R
import com.niro.niroapp.adapters.viewholders.GenericViewHolder
import com.niro.niroapp.databinding.UserDetailCardPaymentBinding
import com.niro.niroapp.models.responsemodels.UserPayment
import com.niro.niroapp.viewmodels.PaymentMode

class ContactDetailPaymentsViewHolder(private val viewBinding : UserDetailCardPaymentBinding, private val variables : HashMap<Int,Any?>) : GenericViewHolder<UserPayment>(viewBinding) {

    override fun bind(item: UserPayment, position: Int) {

        viewBinding.position = position
        setVariables(variables)
        viewBinding.tvPaymentMode.text = if(item.paymentMode == PaymentMode.ONLINE.name)viewBinding.root.context.getText(
            R.string.online_paid)
        else viewBinding.root.context.getText(R.string.paid_by_cash)

    }
}