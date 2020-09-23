package com.niro.niroapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.niro.niroapp.R
import com.niro.niroapp.databinding.UserDetailFragmentBinding
import com.niro.niroapp.models.responsemodels.User
import com.niro.niroapp.models.responsemodels.UserContact
import com.niro.niroapp.users.fragments.ContactDetailOrdersFragment
import com.niro.niroapp.users.fragments.ContactDetailPaymentsFragment
import com.niro.niroapp.utils.FragmentUtils
import com.niro.niroapp.utils.NiroAppConstants
import com.niro.niroapp.utils.NiroAppUtils
import com.niro.niroapp.viewmodels.UserDetailViewModel

class UserDetailFragment : AbstractBaseFragment() {

    private lateinit var bindingUserDetails : UserDetailFragmentBinding
    private  var viewModel: UserDetailViewModel? = null
    private var mCurrentUser : User? = null
    private var mSelectedContact : UserContact? = null


    companion object {
        fun newInstance() = UserDetailFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mSelectedContact = it.getParcelable(NiroAppConstants.ARG_USER_CONTACT) as? UserContact
            mCurrentUser = it.getParcelable(NiroAppConstants.ARG_CURRENT_USER) as? User
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        bindingUserDetails = DataBindingUtil.inflate(inflater,R.layout.user_detail_fragment, container, false)
        bindingUserDetails.lifecycleOwner = viewLifecycleOwner
        return bindingUserDetails.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = activity?.let { ViewModelProvider(it).get(UserDetailViewModel::class.java) }

        initializeData()
        initializeListeners()

        launchOrderFragment()

    }

    private fun initializeData() {
        viewModel?.getSelectedContact()?.value = mSelectedContact
        viewModel?.getCurrentUserData()?.value = mCurrentUser

        super.setPageTitle(String.format(getString(R.string.title_contact_profile),
            NiroAppUtils.getUserTypeStringBasedOnCurrentUserType(requireContext(),mCurrentUser?.userType)),R.drawable.ic_user_type)

        bindingUserDetails.layoutUserDetails.contactDetailsVM  = viewModel
        bindingUserDetails.contactDetailVM = viewModel

        viewModel?.initializePrefix(name = getString(R.string.text_detail_name),business = getString(R.string.text_detail_business_name),phone = getString(R.string.text_detail_number),
            location = getString(R.string.text_detail_mandi_address))

        bindingUserDetails.tvAmountPending.text = String.format(getString(R.string.text_total_pending_amount),
        if((viewModel?.getDifferenceInPayment() ?: 0.0) < 0.0) getString(R.string.text_to_receive) else getString(R.string.text_pending),
            viewModel?.getTotalPendingAmount()?.value)
    }

    private fun initializeListeners() {

        super.registerBackPressedCallback(-1)
        bindingUserDetails.groupUserDetailTabs.setOnCheckedChangeListener { group, checkedId -> showListFragment(checkedId) }

        bindingUserDetails.btnAddOrder.setOnClickListener { launchAddOrderScreen() }

        bindingUserDetails.btnAddPayment.setOnClickListener { launchAddPaymentScreen() }

        bindingUserDetails.layoutUserDetails.tvCallNow.setOnClickListener { callUser() }

    }

    private fun callUser() {
        NiroAppUtils.callUser(requireContext(),viewModel?.getSelectedContact()?.value?.phoneNumber ?: "")
    }

    private fun launchAddPaymentScreen() {
        findNavController().navigate(R.id.action_navigation_user_contact_details_to_dialog_select_users,
            bundleOf(NiroAppConstants.ARG_CURRENT_USER to mCurrentUser, NiroAppConstants.ARG_NEXT_NAVIGATION_ID to R.id.action_navigation_contacts_list_to_navigation_create_payment)
        )

    }

    private fun launchAddOrderScreen() {
        findNavController().navigate(R.id.action_navigation_user_contact_details_to_dialog_select_users,
            bundleOf(NiroAppConstants.ARG_CURRENT_USER to mCurrentUser, NiroAppConstants.ARG_NEXT_NAVIGATION_ID to R.id.action_navigation_contacts_list_to_navigation_create_order)
        )
    }


    private fun showListFragment(checkedId: Int) {

        when(checkedId) {
            R.id.rbOrders -> launchOrderFragment()

            else -> launchPaymentsFragment()
        }

    }


    private fun launchOrderFragment() {
        FragmentUtils.launchFragment(requireActivity().supportFragmentManager,bindingUserDetails.flDetailsList.id,
        fragment = ContactDetailOrdersFragment.newInstance(currentUser = viewModel?.getCurrentUserData()?.value,selectedContactId =
        viewModel?.getSelectedContact()?.value?.contactId),
        tag = NiroAppConstants.TAG_CONTACT_DETAIL_ORDER)
    }

    private fun launchPaymentsFragment() {
        FragmentUtils.launchFragment(requireActivity().supportFragmentManager,bindingUserDetails.flDetailsList.id,
            fragment = ContactDetailPaymentsFragment.newInstance(currentUser = viewModel?.getCurrentUserData()?.value,selectedContactId =
            viewModel?.getSelectedContact()?.value?.contactId),
            tag = NiroAppConstants.TAG_CONTACT_DETAIL_PAYMENTS)
    }




}