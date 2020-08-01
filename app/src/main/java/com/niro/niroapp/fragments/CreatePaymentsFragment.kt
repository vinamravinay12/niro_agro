package com.niro.niroapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import carbon.dialog.ProgressDialog
import com.niro.niroapp.R
import com.niro.niroapp.databinding.CreatePaymentsFragmentBinding
import com.niro.niroapp.models.APIError
import com.niro.niroapp.models.APILoader
import com.niro.niroapp.models.APIResponse
import com.niro.niroapp.models.Success
import com.niro.niroapp.models.responsemodels.UserContact
import com.niro.niroapp.users.fragments.setReadOnly
import com.niro.niroapp.utils.DateChangeListener
import com.niro.niroapp.utils.NIroAppConstants
import com.niro.niroapp.utils.NiroAppUtils
import com.niro.niroapp.viewmodels.CreatePaymentsViewModel
import com.niro.niroapp.viewmodels.PaymentMode
import com.niro.niroapp.viewmodels.factories.CreateOrderViewModelFactory

class CreatePaymentsFragment : Fragment(),DateChangeListener {

    private var viewModel: CreatePaymentsViewModel? = null
    private var mSelectedUserContact: UserContact? = null
    private lateinit var bindingCreatePaymentsFragment: CreatePaymentsFragmentBinding
    private var mProgressDialog: ProgressDialog? = null


    companion object {
        fun newInstance() = CreateOrderFragment()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mSelectedUserContact = it.getParcelable(NIroAppConstants.ARG_USER_CONTACT)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingCreatePaymentsFragment =
            DataBindingUtil.inflate(inflater, R.layout.create_payments_fragment, container, false)
        bindingCreatePaymentsFragment.lifecycleOwner = viewLifecycleOwner
        return bindingCreatePaymentsFragment.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = activity?.let { CreateOrderViewModelFactory().getViewModel(null, it) }
        bindingCreatePaymentsFragment.paymentVM = viewModel

        makeFieldsReadOnly()
        initializeClickListeners()
    }

    private fun makeFieldsReadOnly() {
        bindingCreatePaymentsFragment.etPaymentDate.setReadOnly(true)
    }


    private fun initializeClickListeners() {

        bindingCreatePaymentsFragment.etPaymentDate.setOnClickListener { openDatePicker() }

        bindingCreatePaymentsFragment.groupPaymentMode.setOnCheckedChangeListener { group, checkedId ->
            handlePaymentModeSelectionListener(checkedId)
        }

        bindingCreatePaymentsFragment.btnCreateOrder.setOnClickListener { addPayment() }

    }

    private fun handlePaymentModeSelectionListener(checkedId: Int) {

        when(checkedId) {
            R.id.rbBank -> viewModel?.getPaymentMode()?.value = PaymentMode.ONLINE
            else -> viewModel?.getPaymentMode()?.value = PaymentMode.CASH
        }

    }

    private fun addPayment() {
        viewModel?.addPayment(context)?.observe(viewLifecycleOwner, Observer {
            handleAddPaymentsResponse(it)
        })
    }




    private fun openDatePicker() {
        context?.let { DatePickerDialog(it, this, viewModel?.getPaymentDate()?.value) }
    }

    override fun onDateChanged(date: String?) {
        viewModel?.getPaymentDate()?.value = date
    }


    private fun handleAddPaymentsResponse(apiResponse: APIResponse?) {

        when (apiResponse) {
            is APILoader -> mProgressDialog = context?.let {
                NiroAppUtils.showLoaderProgress(getString(R.string.adding_payment), it)
            }

            is APIError -> {
                mProgressDialog?.dismiss()
                NiroAppUtils.showSnackbar(apiResponse.errorMessage, bindingCreatePaymentsFragment.root)
            }

            is Success<*> -> {
                mProgressDialog?.dismiss()
                NiroAppUtils.showSnackbar(
                    getString(R.string.payment_added),
                    bindingCreatePaymentsFragment.root
                )
                viewModel?.resetAllFields()
                goBackToPaymentsScreen()
            }
        }

    }

    private fun goBackToPaymentsScreen() {
        findNavController().popBackStack(R.id.navigation_payments, false)
    }

}