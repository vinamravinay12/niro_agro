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
import carbon.dialog.ProgressDialog
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.niro.niroapp.R
import com.niro.niroapp.databinding.EditProfileFragmentBinding
import com.niro.niroapp.models.APIError
import com.niro.niroapp.models.APILoader
import com.niro.niroapp.models.APIResponse
import com.niro.niroapp.models.Success
import com.niro.niroapp.models.responsemodels.MandiLocation
import com.niro.niroapp.models.responsemodels.User
import com.niro.niroapp.users.fragments.setReadOnly
import com.niro.niroapp.utils.NiroAppConstants
import com.niro.niroapp.utils.NiroAppUtils
import com.niro.niroapp.viewmodels.EditProfileViewModel
import com.niro.niroapp.viewmodels.factories.EditProfileViewModelFactory

class EditProfileFragment : AbstractBaseFragment() {

    private lateinit var bindingEditProfileFragment: EditProfileFragmentBinding
    private var viewModel: EditProfileViewModel? = null
    private var mCurrentUser : User? = null
    private var mProgressDialog : ProgressDialog? = null
    private lateinit var firebaseAnalytics : FirebaseAnalytics


    companion object {
        fun newInstance() = EditProfileFragment()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mCurrentUser = it.getParcelable(NiroAppConstants.ARG_CURRENT_USER) as? User
        }

        firebaseAnalytics = Firebase.analytics
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingEditProfileFragment = DataBindingUtil.inflate(inflater,R.layout.edit_profile_fragment, container, false)
        bindingEditProfileFragment.lifecycleOwner = viewLifecycleOwner
        requireActivity().viewModelStore.clear()

        return bindingEditProfileFragment.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = activity?.let { EditProfileViewModelFactory(mCurrentUser).getViewModel(mCurrentUser, it) }
        bindingEditProfileFragment.editProfileVM = viewModel

        viewModel?.resetAllFields()
        viewModel?.fillUserDetails()

        firebaseAnalytics.setCurrentScreen(requireActivity(),getString(R.string.menu_edit_profile),null)

        super.setPageTitle(getString(R.string.title_my_profile),R.drawable.ic_nav_profile)
        onSelectedMandiFetched()

        makeFieldsReadOnly()
        initializeFocusChangeListeners()
        initializeClickListeners()


    }

    private  fun onSelectedMandiFetched() {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<MandiLocation>(NiroAppConstants.ARG_SELECTED_MANDI)?.observe(
            viewLifecycleOwner, Observer {
                viewModel?.getMandiLocation()?.value = it
                updateValuesInViews()
            })
    }

    private fun updateValuesInViews() {
        bindingEditProfileFragment.etEnterName.setText(viewModel?.getUserName()?.value ?: "")
        bindingEditProfileFragment.etEnterLocation.setText(viewModel?.getMandiDisplayName()?.value ?: "")
        bindingEditProfileFragment.etEnterBusiness.setText(viewModel?.getBusinessName()?.value ?: "")
    }

    private fun makeFieldsReadOnly() {
        bindingEditProfileFragment.etEnterLocation.setReadOnly(true)
    }

    private fun initializeClickListeners() {
        super.registerBackPressedCallback(R.id.navigation_home)
        bindingEditProfileFragment.etEnterLocation.setOnClickListener { launchMandiLocationFragment() }
        bindingEditProfileFragment.btnUpdateUser.setOnClickListener { updateProfile() }
    }

    private fun updateProfile() {
        if(viewModel?.validateAllFields() ?: 0 > 0)  {
            showError(viewModel?.validateAllFields())
            return
        }


        viewModel?.updateUserProfile(context)?.observe(viewLifecycleOwner, Observer { handleUpdateUserResponse(it) })
    }

    private fun handleUpdateUserResponse(response: APIResponse?) {

        when(response) {
            is APILoader -> mProgressDialog = context?.let {
                NiroAppUtils.showLoaderProgress(getString(R.string.updating_profile), it)
            }

            is APIError -> {
                if(mProgressDialog != null && mProgressDialog?.isShowing == true) mProgressDialog?.dismiss()
                NiroAppUtils.showSnackbar(response.errorMessage,bindingEditProfileFragment.root)
            }

            is Success<*> -> {
                if(mProgressDialog != null && mProgressDialog?.isShowing == true) mProgressDialog?.dismiss()
                NiroAppUtils.showSnackbar(getString(R.string.details_updated),bindingEditProfileFragment.root)
                NiroAppUtils.updateCurrentUser(
                    response.data as? User,
                    requireContext()
                )
                viewModel?.resetAllFields()
                findNavController().previousBackStackEntry?.savedStateHandle?.set(NiroAppConstants.ARG_CURRENT_USER, response.data as? User)
                findNavController().popBackStack()
            }
        }

    }


    private fun showError(error : Int?) {
        if(error == null) return
        if(error > 0) NiroAppUtils.showSnackbar(getString(error),bindingEditProfileFragment.root)
    }


    private fun launchMandiLocationFragment() {
        findNavController().navigate(R.id.navigation_mandi_location, bundleOf(NiroAppConstants.PREVIOUS_SCREEN_ID to R.id.navigation_profile_edit,
            NiroAppConstants.ARG_SELECTED_MANDI to viewModel?.getMandiLocation()?.value)
        )
    }

    private fun initializeFocusChangeListeners() {
        bindingEditProfileFragment.etEnterName.setOnFocusChangeListener {_,hasFocus ->
            if(!hasFocus && (viewModel?.validateUserName()?.value ?: 0 > 0)) {
                bindingEditProfileFragment.etEnterName.error = getString(viewModel?.validateUserName()?.value!!)
            }
        }

        bindingEditProfileFragment.etEnterBusiness.setOnFocusChangeListener {_,hasFocus ->
            if(!hasFocus && (viewModel?.validateBusinessName()?.value ?: 0 > 0)) {
                bindingEditProfileFragment.etEnterBusiness.error = getString(viewModel?.validateBusinessName()?.value!!)
            }
        }

        bindingEditProfileFragment.etEnterNumber.setOnFocusChangeListener {_,hasFocus ->
            if(!hasFocus && (viewModel?.validatePhoneNumber()?.value ?: 0 > 0)) {
                bindingEditProfileFragment.etEnterNumber.error = getString(viewModel?.validatePhoneNumber()?.value!!)
            }
        }


        bindingEditProfileFragment.etEnterLocation.setOnFocusChangeListener {_,hasFocus ->
            if(!hasFocus && (viewModel?.validateSelectedMandi()?.value ?: 0 > 0)) {
                bindingEditProfileFragment.etEnterLocation.error = getString(viewModel?.validateSelectedMandi()?.value!!)
            }
        }
    }

}