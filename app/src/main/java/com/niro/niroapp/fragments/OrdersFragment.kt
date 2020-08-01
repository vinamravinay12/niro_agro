package com.niro.niroapp.fragments

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import carbon.dialog.ProgressDialog
import com.niro.niroapp.R
import com.niro.niroapp.adapters.RecyclerScrollDirectionListener
import com.niro.niroapp.adapters.RecyclerScrollListener
import com.niro.niroapp.databinding.OrdersFragmentBinding
import com.niro.niroapp.databinding.PaymentsFragmentBinding
import com.niro.niroapp.models.APIError
import com.niro.niroapp.models.APILoader
import com.niro.niroapp.models.Success
import com.niro.niroapp.models.responsemodels.User
import com.niro.niroapp.models.responsemodels.UserContact
import com.niro.niroapp.models.responsemodels.UserOrder
import com.niro.niroapp.models.responsemodels.UserType
import com.niro.niroapp.utils.ItemClickListener
import com.niro.niroapp.utils.NIroAppConstants
import com.niro.niroapp.utils.NiroAppUtils
import com.niro.niroapp.viewmodels.OrdersViewModel
import com.niro.niroapp.viewmodels.factories.OrdersViewModelFactory
import kotlinx.android.synthetic.main.orders_fragment.*

class OrdersFragment : Fragment(), ItemClickListener,RecyclerScrollDirectionListener {

    private var ordersViewModel: OrdersViewModel? = null
    private lateinit var bindingOrdersFragment: OrdersFragmentBinding
    private var mCurrentUser : User? = null
    private var mProgressDialog : ProgressDialog? = null


    companion object {
        fun newInstance() = OrdersFragment()

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            mCurrentUser = it.getParcelable(NIroAppConstants.ARG_USER_CONTACT)
        }
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        bindingOrdersFragment = DataBindingUtil.inflate(inflater,R.layout.payments_fragment, container, false)
        bindingOrdersFragment.lifecycleOwner = viewLifecycleOwner
        return bindingOrdersFragment.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        ordersViewModel = activity?.let { OrdersViewModelFactory(mCurrentUser).getViewModel(mCurrentUser, it) }

        initializeListeners()
        initializeHeaders()
        initializeOrdersRecyclerView()
        fetchOrders()

    }


    override fun onResume() {
        super.onResume()
        if(!ordersViewModel?.getUserOrdersList()?.value.isNullOrEmpty())  {
            bindingOrdersFragment.refreshOrdersList.isRefreshing = true
        } else {
            showNoUsers(true)
        }
        fetchOrders()
    }

    private fun initializeOrdersRecyclerView() {
        bindingOrdersFragment.rvOrdersList.layoutManager = LinearLayoutManager(context,
            LinearLayoutManager.VERTICAL,false)
        val adapter = ordersViewModel?.getAdapter()
        adapter?.setVariablesMap(getVariables())
        bindingOrdersFragment.rvOrdersList.setHasFixedSize(true)
        bindingOrdersFragment.rvOrdersList.adapter = adapter
        bindingOrdersFragment.rvOrdersList.addOnScrollListener(RecyclerScrollListener(this))

    }

    private fun getVariables(): HashMap<Int, Any?> {
            return hashMapOf(BR.ordersVM to ordersViewModel, BR.itemClickListener to this)
    }

    private fun initializeHeaders() {
            val totalOrders = ordersViewModel?.getUserOrdersList()?.value?.size ?: 0
            val totalOrderAmount = ordersViewModel?.getTotalOrderAmount()

            bindingOrdersFragment.layoutSummaryDetailHeader.tvTransactionAmount.text = String.format(getString(R.string.total_amount),totalOrderAmount)
            bindingOrdersFragment.layoutSummaryDetailHeader.tvTotalAmount.text = String.format(getString(R.string.total_orders),totalOrders)

    }

    private fun initializeListeners() {
        bindingOrdersFragment.btnAddOrder.setOnClickListener { showContactsList() }
        bindingOrdersFragment.refreshOrdersList.setOnRefreshListener { fetchOrders() }
    }

    private fun showContactsList() {
        findNavController().navigate(R.id.action_navigation_orders_to_navigation_contacts_list,
            bundleOf(NIroAppConstants.ARG_CURRENT_USER to mCurrentUser, NIroAppConstants.ARG_NEXT_NAVIGATION_ID to R.id.action_navigation_contacts_list_to_navigation_create_order))
    }


    private fun fetchOrders() {
        ordersViewModel?.getOrders(context)?.observe(viewLifecycleOwner, Observer { response ->

            when(response) {

                is APILoader -> {
                    if(mCurrentUser?.userType != UserType.COMMISSION_AGENT.name) handleProgress(getString(R.string.fetching_buyers),true)
                    else handleProgress(getString(R.string.fetching_loaders),true)
                }

                is APIError -> {
                    handleProgress("",false)
                    NiroAppUtils.showSnackbar(response.errorMessage,bindingOrdersFragment.root)
                }

                is Success<*> -> {
                    handleProgress("",false)
                    handleSuccessResponse(response.data as? List<UserOrder>)
                }
            }
        })
    }

    private fun handleSuccessResponse(list: List<UserOrder>?) {
        if(list.isNullOrEmpty()) {
            showNoUsers(true)
            return
        }

        showNoUsers(false)
        ordersViewModel?.getUserOrdersList()?.value = list.toMutableList()
        initializeHeaders()
        ordersViewModel?.updateList()
    }

    private fun showNoUsers(toShow: Boolean) {
        bindingOrdersFragment.noUsersLayout.tvNoItemMessage.text = String.format(getString(R.string.no_orders_created),NiroAppUtils.getCurrentUserType(mCurrentUser?.userType))
        if(toShow) {
            bindingOrdersFragment.noUsersLayout.noItemParent.visibility = View.VISIBLE
            bindingOrdersFragment.rvOrdersList.visibility = View.GONE
        }

        else {
            bindingOrdersFragment.noUsersLayout.noItemParent.visibility = View.GONE
            bindingOrdersFragment.rvOrdersList.visibility = View.VISIBLE
        }
    }

    private fun handleProgress(progressMessage : String,toShow : Boolean) {

        if(bindingOrdersFragment.refreshOrdersList.isRefreshing && !toShow) {
            bindingOrdersFragment.refreshOrdersList.isRefreshing = false
        }
        else if(!bindingOrdersFragment.refreshOrdersList.isRefreshing && toShow) {
            mProgressDialog = context?.let { NiroAppUtils.showLoaderProgress(progressMessage, it) }
        }

        else {
            mProgressDialog?.dismiss()
            bindingOrdersFragment.refreshOrdersList.isRefreshing = false
        }
    }

    override fun onItemClick(item: Any?) {
        showOrderDetailsScreen(item as? UserOrder)
    }

    private fun showOrderDetailsScreen(userOrder: UserOrder?) {

    }

    override fun onScrolledDown(isDown: Boolean) {
        bindingOrdersFragment.btnAddOrder.visibility = if(isDown) View.GONE else View.VISIBLE
    }


}