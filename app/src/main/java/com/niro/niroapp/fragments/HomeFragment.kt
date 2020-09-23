package com.niro.niroapp.fragments

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import carbon.dialog.ProgressDialog
import com.niro.niroapp.R
import com.niro.niroapp.activities.MainActivity
import com.niro.niroapp.databinding.HomeFragmentBinding
import com.niro.niroapp.models.APIError
import com.niro.niroapp.models.APILoader
import com.niro.niroapp.models.Success
import com.niro.niroapp.models.responsemodels.OrderSummary
import com.niro.niroapp.models.responsemodels.User
import com.niro.niroapp.models.responsemodels.UserType
import com.niro.niroapp.utils.NiroAppConstants
import com.niro.niroapp.utils.NiroAppUtils
import com.niro.niroapp.utils.OnBackPressedListener
import com.niro.niroapp.viewmodels.OrdersViewModel
import com.niro.niroapp.viewmodels.factories.OrdersViewModelFactory


private const val ARG_USER = "ArgUser"

class HomeFragment : Fragment(), OnBackPressedListener {

    private var orderViewModel: OrdersViewModel? = null
    private lateinit var bindingHomeFragment: HomeFragmentBinding
    private var currentUser: User? = null
    private var mBackPressedCount = 0


    companion object {
        fun newInstance(user: User): HomeFragment {
            return HomeFragment().apply {
                arguments = bundleOf(ARG_USER to user)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            currentUser = getParcelable(ARG_USER)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingHomeFragment =
            DataBindingUtil.inflate(inflater, R.layout.home_fragment, container, false)
        bindingHomeFragment.lifecycleOwner = viewLifecycleOwner

        currentUser = context?.let { NiroAppUtils.getCurrentUser(it) }
        return bindingHomeFragment.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        orderViewModel =
            activity?.let { OrdersViewModelFactory(currentUser).getViewModel(currentUser, it) }


        setPageTitle()
        setWelcomeTitle(currentUser?.fullName ?: "")
        getOrderSummary()
        initializeListeners()
        showOrHideBuySellButtons()


    }

    private fun showOrHideBuySellButtons() {

        bindingHomeFragment.layoutSellOrBuyButton.visibility =
            if (currentUser?.userType == UserType.LOADER.name) View.GONE else View.VISIBLE

        bindingHomeFragment.btnBuy.visibility = if(currentUser?.userType == UserType.COMMISSION_AGENT.name) View.VISIBLE else View.GONE

        bindingHomeFragment.btnSell.setTextSize(TypedValue.COMPLEX_UNIT_SP,
            if(currentUser?.userType == UserType.COMMISSION_AGENT.name) 16f else 20f)

    }

    private fun setPageTitle() {
        if (activity is MainActivity) (activity as? MainActivity)?.setToolbarTitleAndImage(
            getString(
                R.string.title_home
            ), R.drawable.ic_home_24
        )
    }

    private fun initializeListeners() {

        NiroAppUtils.setBackPressedCallback(requireActivity(), viewLifecycleOwner, this)
        bindingHomeFragment.btnConnectWhatsapp.setOnClickListener { openWhatsApp() }
        bindingHomeFragment.btnJoinFacebook.setOnClickListener { launchFacebookGroup() }

        bindingHomeFragment.btnLiveMandiRates.setOnClickListener { showLiveMandiRatesScreen() }

        bindingHomeFragment.btnBuy.setOnClickListener { launchBuyCommoditiesScreen() }

        bindingHomeFragment.btnSell.setOnClickListener { launchCreateNewSellCommodityScreen() }
    }

    private fun launchCreateNewSellCommodityScreen() {
        findNavController().navigate(
            R.id.navigation_create_sell_commodity,
            bundleOf(NiroAppConstants.ARG_CURRENT_USER to currentUser)
        )
    }

    private fun launchBuyCommoditiesScreen() {
        findNavController().navigate(
            R.id.navigation_buy_commodities,
            bundleOf(NiroAppConstants.ARG_CURRENT_USER to currentUser)
        )
    }

    private fun showLiveMandiRatesScreen() {
        findNavController().navigate(
            R.id.navigation_daily_mandi_rates,
            bundleOf(NiroAppConstants.ARG_CURRENT_USER to currentUser)
        )
    }

    private fun launchFacebookGroup() {
        NiroAppUtils.openFacebookGroup(requireContext())
    }

    private fun openWhatsApp() {
        val contact = "+91 6361092057" // use country code with your phone number

        val url = "https://api.whatsapp.com/send?phone=$contact"
        try {
            val packageManager = context?.packageManager
            packageManager?.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES)
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        } catch (e: PackageManager.NameNotFoundException) {
            NiroAppUtils.showSnackbar(
                getString(R.string.whatsapp_not_installed),
                bindingHomeFragment.root
            )

        }
    }

    private fun setWelcomeTitle(fullName: String) {
        bindingHomeFragment.tvWelcomeTitle.text =
            String.format(bindingHomeFragment.tvWelcomeTitle.text.toString(), fullName)
    }

    private fun getOrderSummary() {
        var progressDialog: ProgressDialog? = null
        currentUser?.id?.let {
            orderViewModel?.getOrderSummary(it, context)
                ?.observe(viewLifecycleOwner, Observer { response ->

                    when (response) {
                        is APILoader -> {
                            progressDialog = context?.let {
                                NiroAppUtils.showLoaderProgress(
                                    getString(R.string.fetching_order_summary), it
                                )
                            }

                            showParentView(false)
                        }

                        is APIError -> {
                           if(progressDialog != null && progressDialog?.isShowing == true) progressDialog?.dismiss()
                            showParentView(true)
                            setOrderSummary(0, 0)
                            NiroAppUtils.showSnackbar(
                                response.errorMessage,
                                bindingHomeFragment.root
                            )
                        }

                        is Success<*> -> {
                            if(progressDialog != null && progressDialog?.isShowing == true) progressDialog?.dismiss()
                            showParentView(true)
                            val orderSummary = response.data as OrderSummary
                            setOrderSummary(
                                orderSummary.totalOrders.toInt(),
                                orderSummary.totalTransaction.toInt()
                            )
                        }
                    }
                })
        }
    }

    private fun setOrderSummary(totalOrders: Int, totalTransaction: Int) {
        bindingHomeFragment.tvTotalOrders.text =
            String.format(bindingHomeFragment.tvTotalOrders.text.toString(), totalOrders)
        bindingHomeFragment.tvTotalTransactions.text =
            String.format(bindingHomeFragment.tvTotalTransactions.text.toString(), totalTransaction)


    }

    private fun showParentView(toShow: Boolean) {
        bindingHomeFragment.parentHome.visibility = if (toShow) View.VISIBLE else View.INVISIBLE
    }

    override fun onBackPressed() {

        if (mBackPressedCount == 0) {
            NiroAppUtils.showToast(
                message = getString(R.string.press_again_to_exit),
                context = requireContext(),
                duration = Toast.LENGTH_SHORT
            )
            mBackPressedCount += 1
        } else {
            mBackPressedCount = 0
            activity?.finish()
        }
    }

}