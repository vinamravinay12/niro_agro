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
import com.google.android.material.tabs.TabLayout
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.niro.niroapp.R
import com.niro.niroapp.activities.MainActivity
import com.niro.niroapp.databinding.FragmentUsersListBinding
import com.niro.niroapp.models.APIError
import com.niro.niroapp.models.APILoader
import com.niro.niroapp.models.Success
import com.niro.niroapp.models.responsemodels.User
import com.niro.niroapp.models.responsemodels.UserContact
import com.niro.niroapp.models.responsemodels.UserType
import com.niro.niroapp.users.viewmodels.UsersViewModel
import com.niro.niroapp.users.viewmodels.factories.UsersViewModelFactory
import com.niro.niroapp.utils.*


class UsersListFragment : Fragment(), ItemClickListener, OnTabSelectedDelegate, OnBackPressedListener {

    private var mCurrentUser: User? = null
    private var mProgressDialog: ProgressDialog? = null

    private lateinit var bindingUsersListFragment: FragmentUsersListBinding
    private var userViewModel: UsersViewModel? = null
    private var mNextNavigationViewId: Int = -1
    private lateinit var firebaseAnalytics : FirebaseAnalytics


    companion object {
        fun newInstance() = UsersFragment()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mCurrentUser = it.getParcelable(NiroAppConstants.ARG_CURRENT_USER) as? User
            mNextNavigationViewId = it.getInt(NiroAppConstants.ARG_NEXT_NAVIGATION_ID, -1)
        }

        firebaseAnalytics = Firebase.analytics
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        bindingUsersListFragment =
            DataBindingUtil.inflate(inflater, R.layout.fragment_users_list, container, false)
        bindingUsersListFragment.lifecycleOwner = viewLifecycleOwner
        requireActivity().viewModelStore.clear()
        return bindingUsersListFragment.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        userViewModel =
            activity?.let { UsersViewModelFactory(mCurrentUser).getViewModel(mCurrentUser, it) }

        setUserType()

        onNewUserCreated()
        setPageTitle()
        setupTabLayout()
        initializeHeader()
        initializeRecyclerView()
        initializeListeners()



    }

    private fun setUserType() {
    userViewModel?.getSelectedContactType()?.value =
        when(mCurrentUser?.userType) {
            UserType.COMMISSION_AGENT.name -> ContactType.MY_LOADERS.type
            else -> ContactType.MY_BUYERS.type
        }

    }

    private fun setPageTitle() {
        if(activity is MainActivity) {
            (activity as? MainActivity)?.setToolbarTitleAndImage(getString(R.string.title_select_contact),R.drawable.ic_user_type)
        }
    }

    private fun onNewUserCreated() {

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>(NiroAppConstants.ARG_USER_CONTACT_ID)?.observe(
            viewLifecycleOwner, Observer {contactId ->
                navigateToNextScreen(contactType = userViewModel?.getSelectedContactType()?.value,contactId = contactId)
            }
        )
    }

    private fun initializeListeners() {

        NiroAppUtils.setBackPressedCallback(requireActivity(),viewLifecycleOwner,this)
        bindingUsersListFragment.refreshUsers.setOnClickListener {
            FragmentUtils.hideKeyboard(
                bindingUsersListFragment.root,
                context
            )
        }
        bindingUsersListFragment.refreshUsers.setOnRefreshListener { if (userViewModel?.getSelectedContactType()?.value == ContactType.ALL_BUYERS.type) fetchAllBuyers() else fetchUsers() }

        bindingUsersListFragment.btnAddUser.setOnClickListener { addNewLoader() }
    }

    private fun addNewLoader() {
        findNavController().navigate(R.id.dialog_select_users_action, bundleOf(NiroAppConstants.PREVIOUS_SCREEN_ID to R.id.navigation_contacts_list))
    }


    override fun onResume() {
        super.onResume()
        if (!userViewModel?.getContactsList()?.value.isNullOrEmpty()) {
            bindingUsersListFragment.refreshUsers.isRefreshing = true
        } else {
            showNoUsers(true)
        }
        setCurrentTab()
    }

    private fun setCurrentTab() {
        when (userViewModel?.getSelectedContactType()?.value) {
            ContactType.ALL_BUYERS.type -> {
                bindingUsersListFragment.buyersTabLayout.getTabAt(1)?.select()
                fetchAllBuyers()
            }
            else -> {
                bindingUsersListFragment.buyersTabLayout.getTabAt(0)?.select()
                fetchUsers()
            }
        }
    }


    private fun setupTabLayout() {
        val tabLayout = bindingUsersListFragment.buyersTabLayout

        val myBuyersTab = tabLayout.newTab().setText(getString(R.string.tab_text_my_buyers))
        val allBuyersTab = tabLayout.newTab().setText(getString(R.string.tab_text_all_buyers))

        myBuyersTab.tag = ContactType.MY_BUYERS.name
        allBuyersTab.tag = ContactType.ALL_BUYERS.name

        tabLayout.addTab(myBuyersTab)
        tabLayout.addTab(allBuyersTab)

        setTabItemMargin(tabLayout)

        tabLayout.addOnTabSelectedListener(TabSelectedListener(this))

        if (mCurrentUser?.userType != UserType.COMMISSION_AGENT.name)  {
            tabLayout.visibility = View.VISIBLE
        } else {
            bindingUsersListFragment.btnAddUser.visibility =  if(userViewModel?.getContactsList()?.value.isNullOrEmpty()) View.VISIBLE else View.GONE
            tabLayout.visibility = View.GONE
        }


    }


    private fun setTabItemMargin(tabLayout: TabLayout) {
        for (i in 0 until tabLayout.tabCount) {
            val tab = (tabLayout.getChildAt(0) as ViewGroup).getChildAt(i)
            val marginParams: ViewGroup.MarginLayoutParams =
                tab.layoutParams as ViewGroup.MarginLayoutParams
            marginParams.setMargins(10, 0, 10, 0)
            tab.requestLayout()
        }
    }


    private fun fetchUsers() {

        userViewModel?.getUsersList(context)?.observe(viewLifecycleOwner, Observer { response ->

            when (response) {

                is APILoader -> {
                    if (mCurrentUser?.userType != UserType.COMMISSION_AGENT.name) handleProgress(
                        getString(R.string.fetching_buyers),
                        true
                    )
                    else handleProgress(getString(R.string.fetching_loaders), true)
                }

                is APIError -> {
                    handleProgress("", false)
                   if(response.errorCode != 422) NiroAppUtils.showSnackbar(response.errorMessage, bindingUsersListFragment.root)
                }

                is Success<*> -> {
                    handleProgress("", false)
                    handleSuccessResponse(response.data as? List<UserContact>)
                }
            }
        })
    }


    private fun fetchAllBuyers() {

        userViewModel?.getAllUsersList(context)?.observe(viewLifecycleOwner, Observer { response ->

            when (response) {

                is APILoader -> {
                    if (mCurrentUser?.userType != UserType.COMMISSION_AGENT.name) handleProgress(
                        getString(R.string.fetching_buyers),
                        true
                    )
                    else handleProgress(getString(R.string.fetching_loaders), true)
                }

                is APIError -> {
                    handleProgress("", false)
                    showNoUsers(true)
                    NiroAppUtils.showSnackbar(response.errorMessage, bindingUsersListFragment.root)
                }

                is Success<*> -> {
                    handleProgress("", false)
                    handleSuccessResponse(response.data as? List<UserContact>)
                }
            }
        })
    }


    private fun handleSuccessResponse(list: List<UserContact>?) {
        bindingUsersListFragment.btnAddUser.visibility =  if(list?.isNullOrEmpty() == true) View.VISIBLE else View.GONE

        if (list.isNullOrEmpty()) {
            showNoUsers(true)
            return
        }

        showNoUsers(false)
        userViewModel?.getContactsList()?.value = list.toMutableList()
        initializeHeader()
        userViewModel?.updateList()
    }

    private fun initializeHeader() {
        bindingUsersListFragment.tvSelectUserHeader.text = String.format(
            getString(R.string.txt_select_user),
            NiroAppUtils.getUserTypeStringBasedOnCurrentUserType(context, mCurrentUser?.userType)
        )
    }


    private fun showNoUsers(toShow: Boolean) {
        bindingUsersListFragment.noUsersLayout.tvNoItemMessage.text = String.format(
            getString(R.string.no_users_added),
            NiroAppUtils.getUserTypeStringBasedOnCurrentUserType(context, mCurrentUser?.userType)
        )
        if (toShow) {
            bindingUsersListFragment.noUsersLayout.noItemParent.visibility = View.VISIBLE
            bindingUsersListFragment.rvUsersContact.visibility = View.GONE
        } else {
            bindingUsersListFragment.noUsersLayout.noItemParent.visibility = View.GONE
            bindingUsersListFragment.rvUsersContact.visibility = View.VISIBLE
        }
    }


    private fun handleProgress(progressMessage: String, toShow: Boolean) {

        if (bindingUsersListFragment.refreshUsers.isRefreshing && !toShow) {
            bindingUsersListFragment.refreshUsers.isRefreshing = false
        } else if (!bindingUsersListFragment.refreshUsers.isRefreshing && toShow) {
            mProgressDialog = context?.let { NiroAppUtils.showLoaderProgress(progressMessage, it) }
        } else if (!toShow && mProgressDialog?.isShowing == true) {
            if(mProgressDialog != null && mProgressDialog?.isShowing == true) mProgressDialog?.dismiss()
        }
    }

    private fun initializeRecyclerView() {

        bindingUsersListFragment.rvUsersContact.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL, false
        )
        val adapter = userViewModel?.getAdapter()
        adapter?.setVariablesMap(getVariables())
        bindingUsersListFragment.rvUsersContact.setHasFixedSize(true)
        bindingUsersListFragment.rvUsersContact.adapter = adapter

    }

    private fun getVariables(): HashMap<Int, Any?> {
        return hashMapOf(BR.itemClickListener to this)

    }

    override fun onItemClick(item: Any?) {
        val contact = item as? UserContact
        contact?.contactType =
            userViewModel?.getSelectedContactType()?.value ?: ContactType.MY_LOADERS.type
        navigateToNextScreen(contact?.contactId,contact?.contactType)

       firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM,
           bundleOf(NiroAppConstants.ARG_USER_CONTACT_TYPE to userViewModel?.getSelectedContactType()?.value,
           FirebaseAnalytics.Param.ITEM_NAME to contact?.contactName,
        NiroAppConstants.ARG_SELECTED_COMMODITIES to contact?.selectedCommodity?.joinToString(separator = ",") { it -> "${it.name}"}))
    }

    private fun navigateToNextScreen(contactId : String?,contactType : String?) {
        findNavController().navigate(
            mNextNavigationViewId,
            bundleOf(
                NiroAppConstants.ARG_USER_CONTACT_ID to contactId,
                NiroAppConstants.ARG_USER_CONTACT_TYPE to contactType,
                NiroAppConstants.ARG_CURRENT_USER_ID to mCurrentUser?.id
            )
        )
       if(mProgressDialog != null && mProgressDialog?.isShowing == true) mProgressDialog?.dismiss()

    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        if (!tab?.text.isNullOrEmpty() && tab?.tag == ContactType.MY_BUYERS.name) {
            userViewModel?.getSelectedContactType()?.value = ContactType.MY_BUYERS.type
            fetchUsers()
        } else {
            userViewModel?.getSelectedContactType()?.value = ContactType.ALL_BUYERS.type
            fetchAllBuyers()
        }
    }

    override fun onBackPressed() {
        findNavController().popBackStack()
    }


    override fun onPause() {
        super.onPause()
        if(mProgressDialog != null && mProgressDialog?.isShowing == true) mProgressDialog?.dismiss()
    }


}