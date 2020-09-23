package com.niro.niroapp.users.fragments

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
import com.niro.niroapp.R
import com.niro.niroapp.databinding.ContactDetailOrdersFragmentBinding
import com.niro.niroapp.models.APIError
import com.niro.niroapp.models.APILoader
import com.niro.niroapp.models.Success
import com.niro.niroapp.models.responsemodels.User
import com.niro.niroapp.models.responsemodels.UserOrder
import com.niro.niroapp.users.viewmodels.ContactDetailOrdersViewModel
import com.niro.niroapp.users.viewmodels.factories.ContactDetailOrderViewModelFactory
import com.niro.niroapp.utils.NiroAppConstants
import com.niro.niroapp.utils.NiroAppUtils

class ContactDetailOrdersFragment : Fragment() {

    private var ordersViewModel: ContactDetailOrdersViewModel? = null
    private lateinit var bindingOrdersFragment: ContactDetailOrdersFragmentBinding
    private var mCurrentUser: User? = null
    private var mSelectedContactId: String? = null
    private var mProgressDialog: ProgressDialog? = null


    companion object {
        fun newInstance(currentUser: User?, selectedContactId: String?) = ContactDetailOrdersFragment().apply {
            arguments = Bundle().apply {
                putParcelable(NiroAppConstants.ARG_CURRENT_USER, currentUser)
                putString(NiroAppConstants.ARG_USER_CONTACT, selectedContactId)

            }

        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            mCurrentUser = it.getParcelable(NiroAppConstants.ARG_CURRENT_USER) as? User
            mSelectedContactId = it.getString(NiroAppConstants.ARG_USER_CONTACT)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        bindingOrdersFragment = DataBindingUtil.inflate(
            inflater,
            R.layout.contact_detail_orders_fragment,
            container,
            false
        )
        bindingOrdersFragment.lifecycleOwner = viewLifecycleOwner
        return bindingOrdersFragment.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        ordersViewModel = activity?.let {
            ContactDetailOrderViewModelFactory(mCurrentUser).getViewModel(
                mCurrentUser,
                it
            )
        }

        ordersViewModel?.getSelectedContactId()?.value = mSelectedContactId

        ordersViewModel?.getAmountPrefix()?.value = getString(R.string.rupee_symbol)
        initializeSelectedContactAndUserId()
        initializeListeners()
        initializeHeaders()
        initializeOrdersRecyclerView()
    }

    private fun initializeSelectedContactAndUserId() {
        ordersViewModel?.getCurrentUserData()
    }


    override fun onResume() {
        super.onResume()
        if (!ordersViewModel?.getUserOrdersList()?.value.isNullOrEmpty()) {
            bindingOrdersFragment.refreshOrdersList.isRefreshing = true
        } else {
            showNoUsers(true)
        }
        fetchOrders()
    }

    private fun initializeOrdersRecyclerView() {
        bindingOrdersFragment.rvOrdersList.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL, false
        )
        val adapter = ordersViewModel?.getAdapter()
        adapter?.setVariablesMap(getVariables())
        bindingOrdersFragment.rvOrdersList.setHasFixedSize(true)
        bindingOrdersFragment.rvOrdersList.adapter = adapter

    }

    private fun getVariables(): HashMap<Int, Any?> {
        return hashMapOf(BR.ordersVM to ordersViewModel)
    }

    private fun initializeHeaders() {
        val totalOrders = ordersViewModel?.getUserOrdersList()?.value?.size ?: 0
        val totalOrderAmount = ordersViewModel?.getTotalOrderAmount()

        bindingOrdersFragment.layoutSummaryDetailHeader.tvTransactionAmount.text =
            String.format(getString(R.string.total_amount), totalOrderAmount)
        bindingOrdersFragment.layoutSummaryDetailHeader.tvTotalAmount.text =
            String.format(getString(R.string.total_orders), totalOrders)

    }

    private fun initializeListeners() {
        bindingOrdersFragment.refreshOrdersList.setOnRefreshListener { fetchOrders() }
    }

    private fun showContactsList() {
        findNavController().navigate(
            R.id.action_navigation_orders_to_navigation_contacts_list,
            bundleOf(
                NiroAppConstants.ARG_CURRENT_USER to mCurrentUser,
                NiroAppConstants.ARG_NEXT_NAVIGATION_ID to R.id.action_navigation_contacts_list_to_navigation_create_order
            )
        )
    }


    private fun fetchOrders() {
        ordersViewModel?.getOrdersForContact(context)
            ?.observe(viewLifecycleOwner, Observer { response ->

                when (response) {

                    is APILoader -> {
                        handleProgress(getString(R.string.fetching_orders), true)
                    }

                    is APIError -> {
                        handleProgress("", false)
                        NiroAppUtils.showSnackbar(response.errorMessage, bindingOrdersFragment.root)
                    }

                    is Success<*> -> {
                        handleProgress("", false)
                        handleSuccessResponse(response.data as? List<UserOrder>)
                    }
                }
            })
    }

    private fun handleSuccessResponse(list: List<UserOrder>?) {
        if (list.isNullOrEmpty()) {
            showNoUsers(true)
            return
        }

        showNoUsers(false)
        ordersViewModel?.getUserOrdersList()?.value = list.toMutableList()
        initializeHeaders()
        ordersViewModel?.updateList()
    }

    private fun showNoUsers(toShow: Boolean) {
        bindingOrdersFragment.noUsersLayout.tvNoItemMessage.text = String.format(
            getString(R.string.no_orders_created),
            NiroAppUtils.getCurrentUserType(mCurrentUser?.userType)
        )
        if (toShow) {
            bindingOrdersFragment.noUsersLayout.noItemParent.visibility = View.VISIBLE
            bindingOrdersFragment.rvOrdersList.visibility = View.GONE
        } else {
            bindingOrdersFragment.noUsersLayout.noItemParent.visibility = View.GONE
            bindingOrdersFragment.rvOrdersList.visibility = View.VISIBLE
        }
    }

    private fun handleProgress(progressMessage: String, toShow: Boolean) {

        if (bindingOrdersFragment.refreshOrdersList.isRefreshing && !toShow) {
            bindingOrdersFragment.refreshOrdersList.isRefreshing = false
        } else if (!bindingOrdersFragment.refreshOrdersList.isRefreshing && toShow) {
            mProgressDialog = context?.let { NiroAppUtils.showLoaderProgress(progressMessage, it) }
        } else if (!toShow && mProgressDialog?.isShowing == true) {
             mProgressDialog?.dismiss()
        }
    }


}