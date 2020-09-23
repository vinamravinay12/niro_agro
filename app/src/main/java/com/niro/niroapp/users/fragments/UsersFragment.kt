package com.niro.niroapp.users.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import carbon.dialog.ProgressDialog
import com.google.android.material.tabs.TabLayout
import com.niro.niroapp.R
import com.niro.niroapp.activities.MainActivity
import com.niro.niroapp.adapters.RecyclerScrollDirectionListener
import com.niro.niroapp.adapters.RecyclerScrollListener
import com.niro.niroapp.databinding.UsersFragmentBinding
import com.niro.niroapp.fragments.AbstractBaseFragment
import com.niro.niroapp.models.APIError
import com.niro.niroapp.models.APILoader
import com.niro.niroapp.models.Success
import com.niro.niroapp.models.responsemodels.User
import com.niro.niroapp.models.responsemodels.UserContact
import com.niro.niroapp.models.responsemodels.UserType
import com.niro.niroapp.users.viewmodels.UsersViewModel
import com.niro.niroapp.users.viewmodels.factories.UsersViewModelFactory
import com.niro.niroapp.utils.*
import kotlinx.android.synthetic.main.users_fragment.*


class UsersFragment : AbstractBaseFragment(), ItemClickListener, RecyclerScrollDirectionListener,
    CallUserListener, OnTabSelectedDelegate{

    private var mCurrentUser: User? = null
    private var mProgressDialog: ProgressDialog? = null
    private lateinit var bindingUsersFragment: UsersFragmentBinding
    private var userViewModel: UsersViewModel? = null

    companion object {
        fun newInstance() = UsersFragment()
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
        bindingUsersFragment =
            DataBindingUtil.inflate(inflater, R.layout.users_fragment, container, false)
        bindingUsersFragment.btnAddUser.text =
            if (mCurrentUser?.userType?.equals(UserType.COMMISSION_AGENT) == true) getString(R.string.add_loader) else getString(
                R.string.add_buyer
            )
        bindingUsersFragment.lifecycleOwner = viewLifecycleOwner
        return bindingUsersFragment.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        userViewModel =
            activity?.let { UsersViewModelFactory(mCurrentUser).getViewModel(mCurrentUser, it) }

        super.setPageTitle(NiroAppUtils.getUserTypeStringBasedOnCurrentUserType(requireContext(),mCurrentUser?.userType) ?: "",R.drawable.ic_user_type)

        setUserType()
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


    override fun onResume() {
        super.onResume()

        if (!userViewModel?.getContactsList()?.value.isNullOrEmpty()) {
            bindingUsersFragment.refreshUsers.isRefreshing = true
        } else {
            showNoUsers(true)
        }

        setCurrentTab()

    }


    private fun setCurrentTab() {
        when (userViewModel?.getSelectedContactType()?.value) {
            ContactType.ALL_BUYERS.type -> {
                bindingUsersFragment.buyersTabLayout.getTabAt(1)?.select()
                fetchAllBuyers()
            }
            else -> {
                bindingUsersFragment.buyersTabLayout.getTabAt(0)?.select()
                fetchUsers()
            }
        }
    }






    private fun initializeRecyclerView() {

        bindingUsersFragment.rvUsersList.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val adapter = userViewModel?.getAdapter()
        adapter?.setVariablesMap(getVariables())
        bindingUsersFragment.rvUsersList.setHasFixedSize(true)
        bindingUsersFragment.rvUsersList.adapter = adapter
      //  bindingUsersFragment.rvUsersList.addOnScrollListener(RecyclerScrollListener(this))

    }

    private fun getVariables(): HashMap<Int, Any?> {
        return hashMapOf(BR.itemClickListener to this, BR.callUserListener to this)

    }

    private fun setupTabLayout() {
        val tabLayout = bindingUsersFragment.buyersTabLayout

        val myBuyersTab = tabLayout.newTab().setText(getString(R.string.tab_text_my_buyers))
        val allBuyersTab = tabLayout.newTab().setText(getString(R.string.tab_text_all_buyers))

        myBuyersTab.tag = ContactType.MY_BUYERS.name
        allBuyersTab.tag = ContactType.ALL_BUYERS.name

        tabLayout.addTab(myBuyersTab)
        tabLayout.addTab(allBuyersTab)

        setTabItemMargin(tabLayout)
        tabLayout.addOnTabSelectedListener(TabSelectedListener(this))

        if (mCurrentUser?.userType != UserType.COMMISSION_AGENT.name) tabLayout.visibility =
            View.VISIBLE else View.GONE

        btnAddUser.text = if (mCurrentUser?.userType != UserType.COMMISSION_AGENT.name) getString(R.string.add_buyer) else getString(R.string.add_loader)
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
                    if(response.errorCode != 422) NiroAppUtils.showSnackbar(response.errorMessage, bindingUsersFragment.root)
                }

                is Success<*> -> {
                    handleProgress("", false)
                    handleSuccessResponse(response.data as? List<UserContact>)
                }
            }
        })
    }

    private fun handleSuccessResponse(list: List<UserContact>?) {
        if (list.isNullOrEmpty()) {
            showNoUsers(true)
            return
        }

        showNoUsers(false)
        userViewModel?.getContactsList()?.value = list.toMutableList()
        initializeHeader()
        userViewModel?.updateList()
    }

    private fun showNoUsers(toShow: Boolean) {
        bindingUsersFragment.noUsersLayout.tvNoItemMessage.text = String.format(
            getString(R.string.no_users_added),
            NiroAppUtils.getUserTypeStringBasedOnCurrentUserType(context, mCurrentUser?.userType)
        )
        if (toShow) {
            bindingUsersFragment.noUsersLayout.noItemParent.visibility = View.VISIBLE
            bindingUsersFragment.rvUsersList.visibility = View.GONE
        } else {
            bindingUsersFragment.noUsersLayout.noItemParent.visibility = View.GONE
            bindingUsersFragment.rvUsersList.visibility = View.VISIBLE
        }
    }

    private fun handleProgress(progressMessage: String, toShow: Boolean) {

        if (bindingUsersFragment.refreshUsers.isRefreshing && !toShow) {
            bindingUsersFragment.refreshUsers.isRefreshing = false
        } else if (!bindingUsersFragment.refreshUsers.isRefreshing && toShow) {
            mProgressDialog = context?.let { NiroAppUtils.showLoaderProgress(progressMessage, it) }
        } else if (!toShow && mProgressDialog?.isShowing == true) {
             mProgressDialog?.dismiss()
        }
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
                    if(response.errorCode != 422) NiroAppUtils.showSnackbar(response.errorMessage, bindingUsersFragment.root)
                }

                is Success<*> -> {
                    handleProgress("", false)
                    handleSuccessResponse(response.data as? List<UserContact>)
                }
            }
        })
    }

    private fun initializeHeader() {
        val userTypeString = context?.let {
            NiroAppUtils.getUserTypeBasedOnCurrentType(
                userType = mCurrentUser?.userType,
                context = it
            )
        }

        bindingUsersFragment.tvTotalUsers.text = String.format(
            getString(R.string.txt_total_users),
            userViewModel?.getContactsList()?.value?.size ?: 0, userTypeString
        )

    }


    private fun applyFilters(checkedId: Int) {

        when (checkedId) {
            R.id.rbLocation -> userViewModel?.filterByLocation()
            R.id.rbCommodity -> userViewModel?.filterByCommodity()
            R.id.rbRatings -> userViewModel?.filterByRatings()
        }
    }

    private fun initializeListeners() {

        super.registerBackPressedCallback(R.id.navigation_home)

        bindingUsersFragment.etSearchUsers.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) FragmentUtils.hideKeyboard(
                view,
                context
            )
        }
        bindingUsersFragment.btnAddUser.setOnClickListener { launchCreateUserFragment() }

        bindingUsersFragment.refreshUsers.setOnClickListener {
            FragmentUtils.hideKeyboard(
                bindingUsersFragment.root,
                context
            )
        }
        bindingUsersFragment.refreshUsers.setOnRefreshListener { if (userViewModel?.getSelectedContactType()?.value == ContactType.ALL_BUYERS.type) fetchAllBuyers() else fetchUsers() }
        bindingUsersFragment.etSearchUsers.doAfterTextChanged { filterUsers() }

        bindingUsersFragment.rbFiltersGroup.setOnCheckedChangeListener { group, checkedId ->
            applyFilters(checkedId)
        }
    }

    private fun filterUsers() {
        userViewModel?.getAdapter()?.filter?.filter(bindingUsersFragment.etSearchUsers.text.toString())
    }

    private fun launchCreateUserFragment() {
        findNavController().navigate(R.id.dialog_select_users_action)
    }

    override fun onItemClick(item: Any?) {

        launchUserDetailScreen(item as? UserContact)
    }

    private fun launchUserDetailScreen(userContact: UserContact?) {

        if(userViewModel?.getSelectedContactType()?.value == ContactType.ALL_BUYERS.type) { return }

        findNavController().navigate(
            R.id.action_navigation_loaders_to_navigation_user_contact_details, bundleOf(
                NiroAppConstants.ARG_CURRENT_USER to mCurrentUser,
                NiroAppConstants.ARG_USER_CONTACT to userContact
            )
        )

    }


    override fun onScrolledDown(isDown: Boolean) {
        bindingUsersFragment.btnAddUser.visibility = if (isDown) View.GONE else View.VISIBLE
    }

    override fun callUser(number: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$number")
        startActivity(intent)
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


}

enum class ContactType(val type: String) {
    MY_BUYERS("My_Buyers"), ALL_BUYERS("All_Buyers"), MY_LOADERS("My_Loaders")
}


