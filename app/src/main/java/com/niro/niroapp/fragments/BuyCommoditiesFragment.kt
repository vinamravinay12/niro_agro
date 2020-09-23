package com.niro.niroapp.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import carbon.dialog.ProgressDialog
import com.niro.niroapp.BR
import com.niro.niroapp.R
import com.niro.niroapp.activities.MainActivity
import com.niro.niroapp.databinding.BuyersListFragmentBinding
import com.niro.niroapp.models.APIError
import com.niro.niroapp.models.APILoader
import com.niro.niroapp.models.Success
import com.niro.niroapp.models.responsemodels.BuyCommodity
import com.niro.niroapp.models.responsemodels.User
import com.niro.niroapp.utils.*
import com.niro.niroapp.viewmodels.BuyCommoditiesViewModel
import com.niro.niroapp.viewmodels.factories.SellerCommodityViewModelFactory


class BuyCommoditiesFragment : Fragment(), CallUserListener,
    OnBackPressedListener {

    private lateinit var bindingBuyersListFragment: BuyersListFragmentBinding
    private var buyCommoditiesViewModel: BuyCommoditiesViewModel? = null
    private var mCurrentUser: User? = null
    private var mProgressDialog: ProgressDialog? = null
    private var mPreviousProgressMessage : String = ""


    companion object {
        fun newInstance() = BuyCommoditiesFragment()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mCurrentUser = it.getParcelable(NiroAppConstants.ARG_CURRENT_USER) as? User
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingBuyersListFragment =
            DataBindingUtil.inflate(inflater, R.layout.buyers_list_fragment, container, false)

        bindingBuyersListFragment.lifecycleOwner = viewLifecycleOwner
        return bindingBuyersListFragment.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        buyCommoditiesViewModel = SellerCommodityViewModelFactory(
            mCurrentUser?.id,
            userType = mCurrentUser?.userType
        ).getViewModel(true, requireActivity())

        setPageTitle()
        initializeSellerCommoditiesRecyclerView()
        initializePrefixValues()

        initializeListeners()

        NiroAppUtils.setBackPressedCallback(requireActivity(), viewLifecycleOwner, this)

    }


    override fun onResume() {
        super.onResume()
        if (!buyCommoditiesViewModel?.getBuyCommoditiesList()?.value.isNullOrEmpty()) {
            bindingBuyersListFragment.refreshBuyCommoditiesList.isRefreshing = true
        } else {
            showNoCommoditiesToBuy(true)
        }
        fetchBuyCommoditiesList()
    }

    private fun initializeListeners() {
        bindingBuyersListFragment.etSearchItems.doAfterTextChanged { filterItems() }
        bindingBuyersListFragment.refreshBuyCommoditiesList.setOnRefreshListener { fetchBuyCommoditiesList() }
    }

    private fun filterItems() {
        buyCommoditiesViewModel?.getAdapter()?.filter?.filter(bindingBuyersListFragment.etSearchItems.text.toString())
    }


    private fun setPageTitle() {
        if (activity is MainActivity) (activity as? MainActivity)?.setToolbarTitleAndImage(
            getString(
                R.string.title_buy_commodities
            ), -1
        )
    }


    private fun initializeSellerCommoditiesRecyclerView() {
        bindingBuyersListFragment.rvBuyersList.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL, false
        )
        val adapter = buyCommoditiesViewModel?.getAdapter()
        adapter?.setVariablesMap(getVariables())
        bindingBuyersListFragment.rvBuyersList.setHasFixedSize(true)
        bindingBuyersListFragment.rvBuyersList.adapter = adapter
    }


    private fun getVariables() =
        hashMapOf(BR.sellerCommoditiesVM to buyCommoditiesViewModel, BR.callUserListener to this)


    private fun initializePrefixValues() {
        buyCommoditiesViewModel?.initializePrefixes(
            pricePrefix = getString(R.string.rupee_symbol),
            quantityPrefix = getString(R.string.prefix_quantity),
            datePrefix = getString(R.string.prefix_dispatch_date)
        )
    }


    private fun fetchBuyCommoditiesList() {
        buyCommoditiesViewModel?.fetchBuyCommoditiesList(context)?.observe(viewLifecycleOwner,
            Observer { response ->

                when (response) {
                    is APILoader -> showProgress(
                        message = getString(R.string.fetching_commdities_to_buy),
                        toShow = true
                    )

                    is APIError -> {
                        showProgress("", false)
                        if (response.errorCode != 422) NiroAppUtils.showSnackbar(
                            response.errorMessage,
                            bindingBuyersListFragment.root
                        )
                    }

                    is Success<*> -> {
                        showProgress("", false)
                        handleSuccessResponse(response.data as? List<BuyCommodity>)
                    }
                }

            })
    }

    private fun handleSuccessResponse(list: List<BuyCommodity>?) {
        if (list.isNullOrEmpty()) {
            showNoCommoditiesToBuy(true)
            return
        }

        showNoCommoditiesToBuy(false)
        buyCommoditiesViewModel?.getBuyCommoditiesList()?.value = list.toMutableList()
        buyCommoditiesViewModel?.updateList()
    }

    private fun showNoCommoditiesToBuy(toShow: Boolean) {

        bindingBuyersListFragment.noCommoditiesLayout.tvNoItemMessage.text = String.format(
            getString(R.string.no_commodities_available_to_buy),
            NiroAppUtils.getCurrentUserType(mCurrentUser?.userType)
        )
        if (toShow) {
            bindingBuyersListFragment.noCommoditiesLayout.noItemParent.visibility = View.VISIBLE
            bindingBuyersListFragment.rvBuyersList.visibility = View.GONE
        } else {
            bindingBuyersListFragment.noCommoditiesLayout.noItemParent.visibility = View.GONE
            bindingBuyersListFragment.rvBuyersList.visibility = View.VISIBLE
        }
    }

    override fun callUser(number: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$number")
        startActivity(intent)
    }

    override fun onBackPressed() {
        findNavController().popBackStack(R.id.navigation_home, false)
    }

    private fun showProgress(message: String, toShow: Boolean) {

        if (bindingBuyersListFragment.refreshBuyCommoditiesList.isRefreshing && !toShow) {
            bindingBuyersListFragment.refreshBuyCommoditiesList.isRefreshing = false
        } else if (!bindingBuyersListFragment.refreshBuyCommoditiesList.isRefreshing && toShow) {
            if(!mPreviousProgressMessage.equals(message,true)) mProgressDialog = context?.let { NiroAppUtils.showLoaderProgress(message, it) }
            mPreviousProgressMessage = message
        } else if (!toShow && mProgressDialog?.isShowing == true) {
            mProgressDialog?.dismiss()

        }

    }


}