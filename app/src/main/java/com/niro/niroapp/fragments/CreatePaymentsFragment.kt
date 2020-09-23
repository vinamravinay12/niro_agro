package com.niro.niroapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import carbon.dialog.ProgressDialog
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.niro.niroapp.R
import com.niro.niroapp.activities.MainActivity
import com.niro.niroapp.databinding.CreatePaymentsFragmentBinding
import com.niro.niroapp.models.APIError
import com.niro.niroapp.models.APILoader
import com.niro.niroapp.models.APIResponse
import com.niro.niroapp.models.Success
import com.niro.niroapp.users.fragments.setReadOnly
import com.niro.niroapp.utils.DateChangeListener
import com.niro.niroapp.utils.NiroAppConstants
import com.niro.niroapp.utils.NiroAppUtils
import com.niro.niroapp.utils.OnBackPressedListener
import com.niro.niroapp.viewmodels.CreatePaymentsViewModel
import com.niro.niroapp.viewmodels.PaymentMode
import com.niro.niroapp.viewmodels.factories.CreatePaymentsViewModelFactory

class CreatePaymentsFragment : Fragment(),DateChangeListener,OnBackPressedListener {

    private var viewModel: CreatePaymentsViewModel? = null
    private var mSelectedUserContactId: String? = null
    private var mSelectedContactType : String? = null
    private var mCurrentUserId : String? = null
    private lateinit var bindingCreatePaymentsFragment: CreatePaymentsFragmentBinding
    private var mProgressDialog: ProgressDialog? = null
    private lateinit var firebaseAnalytics: FirebaseAnalytics


    companion object {
        fun newInstance() = CreateOrderFragment()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mSelectedUserContactId = it.getString(NiroAppConstants.ARG_USER_CONTACT_ID)
            mSelectedContactType = it.getString(NiroAppConstants.ARG_USER_CONTACT_TYPE)
            mCurrentUserId = it.getString(NiroAppConstants.ARG_CURRENT_USER_ID)
        }

        firebaseAnalytics = Firebase.analytics
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingCreatePaymentsFragment =
            DataBindingUtil.inflate(inflater, R.layout.create_payments_fragment, container, false)
        bindingCreatePaymentsFragment.lifecycleOwner = viewLifecycleOwner
        requireActivity().viewModelStore.clear()
        firebaseAnalytics.setCurrentScreen(requireActivity(),getString(R.string.title_create_payment),null)
        return bindingCreatePaymentsFragment.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = activity?.let { CreatePaymentsViewModelFactory(mCurrentUserId,mSelectedUserContactId,mSelectedContactType).getViewModel(mCurrentUserId, it) }
        bindingCreatePaymentsFragment.paymentVM = viewModel

        setPageTitle()
        updateData()
        makeFieldsReadOnly()
        initializeClickListeners()
    }

    private fun setPageTitle() {
        if(activity is MainActivity) (activity as? MainActivity)?.setToolbarTitleAndImage(getString(R.string.title_new_payment), R.drawable.ic_payments)
    }

    private fun updateData() {
        viewModel?.getSelectedContactId()?.value = mSelectedUserContactId
        viewModel?.getSelectedContactType()?.value = mSelectedContactType
        viewModel?.getCurrentUserId()?.value = mCurrentUserId

    }

    private fun makeFieldsReadOnly() {
        bindingCreatePaymentsFragment.etPaymentDate.setReadOnly(true)
    }


    private fun initializeClickListeners() {

        NiroAppUtils.setBackPressedCallback(requireActivity(),viewLifecycleOwner,this)
        bindingCreatePaymentsFragment.root.setOnTouchListener { v, event -> NiroAppUtils.hideKeyBoard(v,context) }
        bindingCreatePaymentsFragment.etPaymentDate.setOnClickListener { openDatePicker() }

        bindingCreatePaymentsFragment.rbBank.setOnCheckedChangeListener { buttonView, isChecked ->
            NiroAppUtils.hideKeyBoard(bindingCreatePaymentsFragment.root,context)
            if(isChecked) {
                bindingCreatePaymentsFragment.ivBankSelected.visibility = View.VISIBLE
                viewModel?.getPaymentMode()?.value = PaymentMode.ONLINE
                bindingCreatePaymentsFragment.rbCash.isChecked = false
            }
            else bindingCreatePaymentsFragment.ivBankSelected.visibility = View.INVISIBLE
        }

        bindingCreatePaymentsFragment.rbCash.setOnCheckedChangeListener { buttonView, isChecked ->
            NiroAppUtils.hideKeyBoard(bindingCreatePaymentsFragment.root,context)
            if(isChecked) {
                bindingCreatePaymentsFragment.ivCashSelected.visibility = View.VISIBLE
                viewModel?.getPaymentMode()?.value = PaymentMode.CASH
                bindingCreatePaymentsFragment.rbBank.isChecked = false
            }
            else bindingCreatePaymentsFragment.ivCashSelected.visibility = View.INVISIBLE
        }

        bindingCreatePaymentsFragment.btnCreateOrder.setOnClickListener { addPayment() }

    }


    private fun addPayment() {
        NiroAppUtils.hideKeyBoard(bindingCreatePaymentsFragment.root,context)
        viewModel?.addPayment(context)?.observe(viewLifecycleOwner, Observer {
            handleAddPaymentsResponse(it)
        })
    }




    private fun openDatePicker() {
        NiroAppUtils.hideKeyBoard(bindingCreatePaymentsFragment.root,context)
        activity?.let { DatePickerDialog(it, this, viewModel?.getPaymentDate()?.value).show(it.supportFragmentManager,NiroAppConstants.TAG_DIALOG_DATE) }
    }

    override fun onDateChanged(date: String?) {
        viewModel?.getPaymentDate()?.value = date
        bindingCreatePaymentsFragment.etPaymentDate.setText(viewModel?.getPaymentDateDisplayValue()?.value ?: "")
    }


    private fun handleAddPaymentsResponse(apiResponse: APIResponse?) {

        when (apiResponse) {
            is APILoader -> mProgressDialog = context?.let {
                NiroAppUtils.showLoaderProgress(getString(R.string.adding_payment), it)
            }

            is APIError -> {
                if(mProgressDialog != null && mProgressDialog?.isShowing == true) mProgressDialog?.dismiss()
                NiroAppUtils.showSnackbar(apiResponse.errorMessage, bindingCreatePaymentsFragment.root)
            }

            is Success<*> -> {
                if(mProgressDialog != null && mProgressDialog?.isShowing == true) mProgressDialog?.dismiss()
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

    override fun onBackPressed() {
        findNavController().popBackStack(R.id.navigation_contacts_list,false)
    }


}