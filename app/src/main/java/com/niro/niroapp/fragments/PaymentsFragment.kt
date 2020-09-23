package com.niro.niroapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.databinding.library.baseAdapters.BR
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import carbon.dialog.ProgressDialog
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.niro.niroapp.R
import com.niro.niroapp.activities.MainActivity
import com.niro.niroapp.adapters.RecyclerScrollDirectionListener
import com.niro.niroapp.adapters.RecyclerScrollListener
import com.niro.niroapp.databinding.PaymentsFragmentBinding
import com.niro.niroapp.models.APIError
import com.niro.niroapp.models.APILoader
import com.niro.niroapp.models.Success
import com.niro.niroapp.models.responsemodels.User
import com.niro.niroapp.models.responsemodels.UserPayment
import com.niro.niroapp.utils.NiroAppConstants
import com.niro.niroapp.utils.NiroAppUtils
import com.niro.niroapp.utils.OnBackPressedListener
import com.niro.niroapp.viewmodels.PaymentsViewModel
import com.niro.niroapp.viewmodels.factories.OrdersViewModelFactory

class PaymentsFragment : Fragment(), RecyclerScrollDirectionListener, OnBackPressedListener {

    private var paymentsViewModel: PaymentsViewModel? = null
    private lateinit var bindingPaymentsFragment: PaymentsFragmentBinding
    private var mCurrentUser : User? = null
    private var mProgressDialog : ProgressDialog? = null
    private lateinit var firebaseAnalytics : FirebaseAnalytics


    companion object {
        fun newInstance() = OrdersFragment()

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            mCurrentUser = it.getParcelable(NiroAppConstants.ARG_CURRENT_USER) as? User
        }

        firebaseAnalytics = Firebase.analytics
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        bindingPaymentsFragment = DataBindingUtil.inflate(inflater,R.layout.payments_fragment, container, false)
        bindingPaymentsFragment.lifecycleOwner = viewLifecycleOwner

        firebaseAnalytics.setCurrentScreen(requireActivity(),getString(R.string.title_payments),null)
        return bindingPaymentsFragment.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        paymentsViewModel = activity?.let { OrdersViewModelFactory(mCurrentUser).getViewModel(mCurrentUser, it) }

        setAmountSymbol()
        setPageTitle()
        initializeListeners()
        initializeHeaders()
        initializeOrdersRecyclerView()
    }

    private fun setAmountSymbol() {
        paymentsViewModel?.getAmountPrefix()?.value = getString(R.string.rupee_symbol)
    }

    private fun setPageTitle() {
        if(activity is MainActivity) (activity as? MainActivity)?.setToolbarTitleAndImage(getString(R.string.title_payments),R.drawable.ic_payments)
    }

    override fun onResume() {
        super.onResume()
        if(!paymentsViewModel?.getPaymentsList()?.value.isNullOrEmpty())  {
            bindingPaymentsFragment.refreshPaymentsList.post { Runnable {
                bindingPaymentsFragment.refreshPaymentsList.isRefreshing = true
            } }
            bindingPaymentsFragment.refreshPaymentsList.isRefreshing = true
        } else {
            showNoPayments(true)
        }
        fetchPayments()

    }

    private fun initializeOrdersRecyclerView() {
        bindingPaymentsFragment.rvPaymentsList.layoutManager = LinearLayoutManager(context,
            LinearLayoutManager.VERTICAL,false)
        val adapter = paymentsViewModel?.getAdapter()
        adapter?.setVariablesMap(getVariables())
        bindingPaymentsFragment.rvPaymentsList.setHasFixedSize(true)
        bindingPaymentsFragment.rvPaymentsList.adapter = adapter

      //  bindingPaymentsFragment.rvPaymentsList.addOnScrollListener(RecyclerScrollListener(this))
    }

    private fun getVariables(): HashMap<Int, Any?> {
        return hashMapOf(BR.paymentsVM to paymentsViewModel)
    }

    private fun initializeHeaders() {
        val totalPayments = paymentsViewModel?.getPaymentsList()?.value?.size ?: 0
        val totalOrderAmount = paymentsViewModel?.getTotalPaymentAmount()

        bindingPaymentsFragment.layoutSummaryDetailHeader.tvTransactionAmount.text = String.format(getString(R.string.total_amount),totalOrderAmount)
        bindingPaymentsFragment.layoutSummaryDetailHeader.tvTotalAmount.text = String.format(getString(R.string.total_payments),totalPayments)

    }

    private fun initializeListeners() {

        NiroAppUtils.setBackPressedCallback(requireActivity(),viewLifecycleOwner,this)
        bindingPaymentsFragment.btnAddPayment.setOnClickListener { showContactsList() }
        bindingPaymentsFragment.refreshPaymentsList.setOnRefreshListener { fetchPayments() }
    }




    private fun showContactsList() {
        findNavController().navigate(R.id.action_navigation_payments_to_navigation_contacts_list,
            bundleOf(NiroAppConstants.ARG_CURRENT_USER to mCurrentUser, NiroAppConstants.ARG_NEXT_NAVIGATION_ID to R.id.action_navigation_contacts_list_to_navigation_create_payment)
        )
    }


    private fun fetchPayments() {
        paymentsViewModel?.getPayments(context)?.observe(viewLifecycleOwner, Observer { response ->

            when(response) {

                is APILoader -> {
                    handleProgress(getString(R.string.fetching_payments),true)
                }

                is APIError -> {
                    handleProgress("",false)
                    if(response.errorCode != 422) NiroAppUtils.showSnackbar(response.errorMessage,bindingPaymentsFragment.root)
                }

                is Success<*> -> {
                    handleProgress("",false)
                    handleSuccessResponse(response.data as? List<UserPayment>)
                }
            }
        })
    }

    private fun handleSuccessResponse(list: List<UserPayment>?) {
        if(list.isNullOrEmpty()) {
            showNoPayments(true)
            return
        }

        showNoPayments(false)
        paymentsViewModel?.getPaymentsList()?.value = list.toMutableList()
        initializeHeaders()
        paymentsViewModel?.updateList()
    }

    private fun showNoPayments(toShow: Boolean) {
        bindingPaymentsFragment.noUsersLayout.tvNoItemMessage.text = String.format(getString(R.string.no_payments_added),
            NiroAppUtils.getCurrentUserType(mCurrentUser?.userType))
        if(toShow) {
            bindingPaymentsFragment.noUsersLayout.noItemParent.visibility = View.VISIBLE
             bindingPaymentsFragment.rvPaymentsList.visibility = View.GONE
        }

        else {
            bindingPaymentsFragment.noUsersLayout.noItemParent.visibility = View.GONE
            bindingPaymentsFragment.rvPaymentsList.visibility = View.VISIBLE
        }
    }

    private fun handleProgress(progressMessage : String,toShow : Boolean) {

        if(bindingPaymentsFragment.refreshPaymentsList.isRefreshing && !toShow) {
            bindingPaymentsFragment.refreshPaymentsList.isRefreshing = false
        }
        else if(!bindingPaymentsFragment.refreshPaymentsList.isRefreshing && toShow) {
            mProgressDialog = context?.let { NiroAppUtils.showLoaderProgress(progressMessage, it) }

        }

        else if(!toShow && mProgressDialog?.isShowing == true){
            mProgressDialog?.dismiss()
        }
    }

    override fun onScrolledDown(isDown: Boolean) {
        bindingPaymentsFragment.btnAddPayment.visibility = if(isDown) View.GONE else View.VISIBLE
    }

    override fun onBackPressed() {
        findNavController().popBackStack(R.id.navigation_home,false)
    }

}