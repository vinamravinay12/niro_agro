package com.niro.niroapp.users.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import carbon.dialog.ProgressDialog
import com.niro.niroapp.R
import com.niro.niroapp.databinding.FragmentUsersListBinding
import com.niro.niroapp.databinding.UsersFragmentBinding
import com.niro.niroapp.models.APIError
import com.niro.niroapp.models.APILoader
import com.niro.niroapp.models.Success
import com.niro.niroapp.models.responsemodels.User
import com.niro.niroapp.models.responsemodels.UserContact
import com.niro.niroapp.models.responsemodels.UserType
import com.niro.niroapp.users.viewmodels.UsersViewModel
import com.niro.niroapp.users.viewmodels.factories.UsersViewModelFactory
import com.niro.niroapp.utils.ItemClickListener
import com.niro.niroapp.utils.NIroAppConstants
import com.niro.niroapp.utils.NiroAppUtils


class UsersListFragment : Fragment(), ItemClickListener {

    private var mCurrentUser : User? = null
    private var mProgressDialog : ProgressDialog? = null

    private lateinit var bindingUsersListFragment: FragmentUsersListBinding
    private var userViewModel: UsersViewModel? = null
    private var mNextNavigationViewId : Int = -1


    companion object {
        fun newInstance() = UsersFragment()
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mCurrentUser = it.getParcelable(NIroAppConstants.ARG_CURRENT_USER)
            mNextNavigationViewId = it.getInt(NIroAppConstants.ARG_NEXT_NAVIGATION_ID,-1)
        }
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        bindingUsersListFragment = DataBindingUtil.inflate(inflater,R.layout.fragment_users_list, container, false)
        bindingUsersListFragment.lifecycleOwner = viewLifecycleOwner
        return bindingUsersListFragment.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        userViewModel = activity?.let { UsersViewModelFactory(mCurrentUser).getViewModel(mCurrentUser, it) }
        initializeRecyclerView()

    }

    override fun onResume() {
        super.onResume()
        if(!userViewModel?.getContactsList()?.value.isNullOrEmpty())  {
            bindingUsersListFragment.refreshUsers.isRefreshing = true
        } else {
            showNoUsers(true)
        }
        fetchUsers()


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
                    NiroAppUtils.showSnackbar(response.errorMessage,bindingUsersListFragment.root)
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

    private fun initializeHeader() {
        bindingUsersListFragment.tvSelectUserHeader.text = String.format(bindingUsersListFragment.tvSelectUserHeader.text.toString(),NiroAppUtils.getCurrentUserType(mCurrentUser?.userType))
    }


    private fun showNoUsers(toShow: Boolean) {
        bindingUsersListFragment.noUsersLayout.tvNoItemMessage.text = String.format(getString(R.string.no_users_added),NiroAppUtils.getCurrentUserType(mCurrentUser?.userType))
        if(toShow) {
            bindingUsersListFragment.noUsersLayout.noItemParent.visibility = View.VISIBLE
            bindingUsersListFragment.rvUsersContact.visibility = View.GONE
        }

        else {
            bindingUsersListFragment.noUsersLayout.noItemParent.visibility = View.GONE
            bindingUsersListFragment.rvUsersContact.visibility = View.VISIBLE
        }
    }


    private fun handleProgress(progressMessage : String,toShow : Boolean) {

        if(bindingUsersListFragment.refreshUsers.isRefreshing && !toShow) {
            bindingUsersListFragment.refreshUsers.isRefreshing = false
        }
        else if(!bindingUsersListFragment.refreshUsers.isRefreshing && toShow) {
            mProgressDialog = context?.let { NiroAppUtils.showLoaderProgress(progressMessage, it) }
        }

        else {
            mProgressDialog?.dismiss()
            bindingUsersListFragment.refreshUsers.isRefreshing = false
        }
    }

    private fun initializeRecyclerView() {

        bindingUsersListFragment.rvUsersContact.layoutManager = LinearLayoutManager(context,
            LinearLayoutManager.VERTICAL,false)
        val adapter = userViewModel?.getAdapter()
        adapter?.setVariablesMap(getVariables())
        bindingUsersListFragment.rvUsersContact.setHasFixedSize(true)
        bindingUsersListFragment.rvUsersContact.adapter = adapter

    }

    private fun getVariables(): HashMap<Int, Any?> {
        return  hashMapOf(BR.itemClickListener to this)

    }

    override fun onItemClick(item: Any?) {
        navigateToNextScreen(item as? UserContact)
    }

    private fun navigateToNextScreen(userContact: UserContact?) {
        findNavController().navigate(mNextNavigationViewId, bundleOf(NIroAppConstants.ARG_USER_CONTACT to userContact))

    }


}