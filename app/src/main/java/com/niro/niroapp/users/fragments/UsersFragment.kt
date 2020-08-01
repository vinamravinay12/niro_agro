package com.niro.niroapp.users.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import carbon.dialog.ProgressDialog
import com.google.android.material.tabs.TabLayout
import com.niro.niroapp.R
import com.niro.niroapp.adapters.RecyclerScrollDirectionListener
import com.niro.niroapp.adapters.RecyclerScrollListener
import com.niro.niroapp.databinding.UsersFragmentBinding
import com.niro.niroapp.models.APIError
import com.niro.niroapp.models.APILoader
import com.niro.niroapp.models.Success
import com.niro.niroapp.models.responsemodels.User
import com.niro.niroapp.models.responsemodels.UserContact
import com.niro.niroapp.models.responsemodels.UserType
import com.niro.niroapp.utils.NIroAppConstants
import com.niro.niroapp.utils.NiroAppUtils
import com.niro.niroapp.users.viewmodels.UsersViewModel
import com.niro.niroapp.users.viewmodels.factories.UsersViewModelFactory
import com.niro.niroapp.utils.ItemClickListener


class UsersFragment : Fragment(), ItemClickListener, RecyclerScrollDirectionListener {

    private var mCurrentUser : User? = null
    private var mProgressDialog : ProgressDialog? = null

    companion object {
        fun newInstance() = UsersFragment()
    }

    private lateinit var bindingUsersFragment : UsersFragmentBinding
    private var userViewModel: UsersViewModel? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mCurrentUser = it.getParcelable(NIroAppConstants.ARG_CURRENT_USER)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingUsersFragment = DataBindingUtil.inflate(inflater,R.layout.users_fragment, container, false)
        bindingUsersFragment.lifecycleOwner = viewLifecycleOwner
        return bindingUsersFragment.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        userViewModel = activity?.let { UsersViewModelFactory(mCurrentUser).getViewModel(mCurrentUser, it) }

        setupTabLayout()
        initializeRecyclerView()
        initializeListeners()

    }

    override fun onResume() {
        super.onResume()
        if(!userViewModel?.getContactsList()?.value.isNullOrEmpty())  {
            bindingUsersFragment.refreshUsers.isRefreshing = true
        } else {
            showNoUsers(true)
        }
        fetchUsers()


    }

    private fun initializeRecyclerView() {

        bindingUsersFragment.rvUsersList.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        val adapter = userViewModel?.getAdapter()
        adapter?.setVariablesMap(getVariables())
        bindingUsersFragment.rvUsersList.setHasFixedSize(true)
        bindingUsersFragment.rvUsersList.adapter = adapter
        bindingUsersFragment.rvUsersList.addOnScrollListener(RecyclerScrollListener(this))
        
    }

    private fun getVariables(): HashMap<Int, Any?> {
       return  hashMapOf(BR.itemClickListener to this)

    }

    private fun setupTabLayout() {
        val tabLayout = bindingUsersFragment.buyersTabLayout

        val myBuyersTab = tabLayout.newTab().setText(getString(R.string.tab_text_my_buyers))
        val allBuyersTab = tabLayout.newTab().setText(getString(R.string.tab_text_all_buyers))

        myBuyersTab.tag = TabLayoutType.MY_BUYERS.name
        allBuyersTab.tag = TabLayoutType.ALL_BUYERS.name

        tabLayout.addTab(myBuyersTab)
        tabLayout.addTab(allBuyersTab)

        setTabItemMargin(tabLayout)
        tabLayout.addOnTabSelectedListener(TabSelectedListener())

        if(mCurrentUser?.userType != UserType.COMMISSION_AGENT.name) tabLayout.visibility = View.VISIBLE else View.GONE
    }

    private fun setTabItemMargin(tabLayout: TabLayout) {
        for (i in 0 until tabLayout.tabCount) {
            val tab = (tabLayout.getChildAt(0) as ViewGroup).getChildAt(i)
            val marginParams : ViewGroup.MarginLayoutParams = tab.layoutParams as ViewGroup.MarginLayoutParams
            marginParams.setMargins(10,0,10,0)
            tab.requestLayout()
        }
    }

    private fun fetchUsers() {

        userViewModel?.getUsersList(context)?.observe(viewLifecycleOwner, Observer { response ->

            when(response) {

                is APILoader -> {
                    if(mCurrentUser?.userType != UserType.COMMISSION_AGENT.name) handleProgress(getString(R.string.fetching_buyers),true)
                    else handleProgress(getString(R.string.fetching_loaders),true)
                }

                is APIError -> {
                    handleProgress("",false)
                    NiroAppUtils.showSnackbar(response.errorMessage,bindingUsersFragment.root)
                }

                is Success<*> -> {
                    handleProgress("",false)
                    handleSuccessResponse(response.data as? List<UserContact>)
                }
            }
        })
    }

    private fun handleSuccessResponse(list: List<UserContact>?) {
        if(list.isNullOrEmpty()) {
            showNoUsers(true)
            return
        }

        showNoUsers(false)
        userViewModel?.getContactsList()?.value = list.toMutableList()
        initializeHeader()
        userViewModel?.updateList()
    }

    private fun showNoUsers(toShow: Boolean) {
        bindingUsersFragment.noUsersLayout.tvNoItemMessage.text = String.format(getString(R.string.no_users_added),NiroAppUtils.getCurrentUserType(mCurrentUser?.userType))
        if(toShow) {
            bindingUsersFragment.noUsersLayout.noItemParent.visibility = View.VISIBLE
            bindingUsersFragment.rvUsersList.visibility = View.GONE
        }

        else {
            bindingUsersFragment.noUsersLayout.noItemParent.visibility = View.GONE
            bindingUsersFragment.rvUsersList.visibility = View.VISIBLE
        }
    }

    private fun handleProgress(progressMessage : String,toShow : Boolean) {

        if(bindingUsersFragment.refreshUsers.isRefreshing && !toShow) {
            bindingUsersFragment.refreshUsers.isRefreshing = false
        }
        else if(!bindingUsersFragment.refreshUsers.isRefreshing && toShow) {
            mProgressDialog = context?.let { NiroAppUtils.showLoaderProgress(progressMessage, it) }
        }

        else {
            mProgressDialog?.dismiss()
            bindingUsersFragment.refreshUsers.isRefreshing = false
        }
    }


    private fun fetchAllBuyers() {

        userViewModel?.getAllUsersList(context)?.observe(viewLifecycleOwner, Observer { response ->

            when(response) {

                is APILoader -> {
                    if(mCurrentUser?.userType != UserType.COMMISSION_AGENT.name) handleProgress(getString(R.string.fetching_buyers),true)
                    else handleProgress(getString(R.string.fetching_loaders),true)
                }

                is APIError -> {
                    handleProgress("",false)
                    NiroAppUtils.showSnackbar(response.errorMessage,bindingUsersFragment.root)
                }

                is Success<*> -> {
                    handleProgress("",false)
                    handleSuccessResponse(response.data as? List<UserContact>)
                }
            }
        })
    }

    private fun initializeHeader() {
        val userTypeString = context?.let { NiroAppUtils.getUserTypeBasedOnCurrentType(userType = mCurrentUser?.userType,context = it) }

        bindingUsersFragment.tvTotalUsers.text = String.format(bindingUsersFragment.tvTotalUsers.text.toString(),
            userViewModel?.getContactsList()?.value?.size ?: 0,userTypeString)

    }



    private fun applyFilters(checkedId: Int) {

        when(checkedId) {
            R.id.rbLocation -> userViewModel?.filterByLocation()
            R.id.rbCommodity -> userViewModel?.filterByCommodity()
            R.id.rbRatings -> userViewModel?.filterByRatings()
        }
    }

    private fun initializeListeners() {
        bindingUsersFragment.btnAddUser.setOnClickListener { launchCreateUserFragment() }
        bindingUsersFragment.refreshUsers.setOnRefreshListener { fetchUsers() }
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


    }


     inner class  TabSelectedListener : TabLayout.OnTabSelectedListener {

         override fun onTabReselected(tab: TabLayout.Tab?) {

         }

         override fun onTabUnselected(tab: TabLayout.Tab?) {

         }

         override fun onTabSelected(tab: TabLayout.Tab?) {
            if(!tab?.text.isNullOrEmpty() && tab?.tag == TabLayoutType.MY_BUYERS.name) fetchUsers()
             else fetchAllBuyers()
         }

     }

    override fun onScrolledDown(isDown: Boolean) {
        bindingUsersFragment.btnAddUser.visibility = if(isDown) View.GONE else View.VISIBLE
    }

}

enum class TabLayoutType {
    MY_BUYERS, ALL_BUYERS
}