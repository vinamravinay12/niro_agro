package com.niro.niroapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.niro.niroapp.R
import com.niro.niroapp.activities.MainActivity
import com.niro.niroapp.databinding.ProfileDetailsFragmentBinding
import com.niro.niroapp.models.responsemodels.CommodityItem
import com.niro.niroapp.models.responsemodels.User
import com.niro.niroapp.utils.NiroAppConstants
import com.niro.niroapp.viewmodels.ProfileDetailsViewModel
import com.niro.niroapp.viewmodels.factories.ProfileDetailsViewModelFactory

class ProfileDetailsFragment : AbstractBaseFragment() {

    private  var viewModel: ProfileDetailsViewModel? = null
    private lateinit var bindingProfileDetails : ProfileDetailsFragmentBinding
    private var mCurrentUser : User? = null

    companion object {
        fun newInstance() = ProfileDetailsFragment()
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
        bindingProfileDetails = DataBindingUtil.inflate(inflater,R.layout.profile_details_fragment, container, false)

        bindingProfileDetails.lifecycleOwner = viewLifecycleOwner

        return bindingProfileDetails.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = activity?.let {
            ProfileDetailsViewModelFactory(mCurrentUser).getViewModel(mCurrentUser, it)
        }

        onUserDetailsUpdated()
       

        bindingProfileDetails.layoutProfileDetails.profileDetailsVM = viewModel
        bindingProfileDetails.layoutCommodityDetails.profileDetailsVM = viewModel

        viewModel?.initializePrefix(name = getString(R.string.text_detail_name),business = getString(R.string.text_detail_business_name),phone = getString(R.string.text_detail_number),
        location = getString(R.string.text_detail_mandi_address))

        super.setPageTitle(getString(R.string.title_my_profile),R.drawable.ic_nav_profile)

        initializeListeners()

    }


    private fun initializeListeners() {

        super.registerBackPressedCallback(previousScreenId = R.id.navigation_home)
        bindingProfileDetails.btnEditCommodities.setOnClickListener { openEditCommoditiesScreen() }
        bindingProfileDetails.btnEditProfile.setOnClickListener { openEditProfileScreen() }
    }

    private fun openEditProfileScreen() {
        findNavController().navigate(R.id.action_navigation_profile_details_to_navigation_profile_edit,bundleOf(NiroAppConstants.ARG_CURRENT_USER to mCurrentUser))
    }

    private fun openEditCommoditiesScreen() {
        findNavController().navigate(R.id.action_navigation_profile_details_to_navigation_commodities_fragment, bundleOf(NiroAppConstants.ARG_COMMODITIES_EDIT to true,
        NiroAppConstants.ARG_SELECTED_COMMODITIES to mCurrentUser?.selectedCommodities, NiroAppConstants.ARG_CURRENT_USER_ID to mCurrentUser?.id))
    }


    private fun onUserDetailsUpdated() {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<User>(NiroAppConstants.ARG_CURRENT_USER)?.observe(
            viewLifecycleOwner, Observer {
                mCurrentUser = it
                viewModel?.getCurrentUser()?.value = it
                (activity as? MainActivity)?.updateUser(it)
                updateProfileDetails()

            }
        )
    }

    private fun updateProfileDetails() {
        bindingProfileDetails.layoutProfileDetails.tvUserName.text = viewModel?.getUserName()?.value
        bindingProfileDetails.layoutProfileDetails.tvBusinessName.text = viewModel?.getBusinessName()?.value
        bindingProfileDetails.layoutProfileDetails.tvMandiAddress.text = viewModel?.getMandiLocation()?.value
    }


}