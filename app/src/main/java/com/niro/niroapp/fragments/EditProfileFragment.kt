package com.niro.niroapp.fragments

import androidx.lifecycle.ViewModelProviders
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
import com.niro.niroapp.R
import com.niro.niroapp.databinding.EditProfileFragmentBinding
import com.niro.niroapp.models.APIError
import com.niro.niroapp.models.APILoader
import com.niro.niroapp.models.APIResponse
import com.niro.niroapp.models.Success
import com.niro.niroapp.models.responsemodels.User
import com.niro.niroapp.users.fragments.setReadOnly
import com.niro.niroapp.utils.FragmentUtils.goBackToPreviousScreen
import com.niro.niroapp.utils.NIroAppConstants
import com.niro.niroapp.utils.NiroAppUtils
import com.niro.niroapp.viewmodels.EditProfileViewModel
import com.niro.niroapp.viewmodels.factories.EditProfileViewModelFactory

class EditProfileFragment : Fragment() {

    private lateinit var bindingEditProfileFragment: EditProfileFragmentBinding
    private var viewModel: EditProfileViewModel? = null
    private var mCurrentUser : User? = null
    private var mProgressDialog : ProgressDialog? = null

    companion object {
        fun newInstance() = EditProfileFragment()
    }


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
        bindingEditProfileFragment = DataBindingUtil.inflate(inflater,R.layout.edit_profile_fragment, container, false)
        bindingEditProfileFragment.lifecycleOwner = viewLifecycleOwner
        return bindingEditProfileFragment.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = activity?.let { EditProfileViewModelFactory(mCurrentUser).getViewModel(mCurrentUser, it) }
        bindingEditProfileFragment.editProfileVM = viewModel

        viewModel?.resetAllFields()
        viewModel?.fillUserDetails()

        makeFieldsReadOnly()
        initializeFocusChangeListeners()
        initializeClickListeners()


    }

    private fun makeFieldsReadOnly() {
        bindingEditProfileFragment.etEnterLocation.setReadOnly(true)
    }

    private fun initializeClickListeners() {
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
                NiroAppUtils.showLoaderProgress(getString(R.string.creating_contact), it)
            }

            is APIError -> {
                mProgressDialog?.dismiss()
                NiroAppUtils.showSnackbar(response.errorMessage,bindingEditProfileFragment.root)
            }

            is Success<*> -> {
                mProgressDialog?.dismiss()
                NiroAppUtils.showSnackbar(getString(R.string.contact_created),bindingEditProfileFragment.root)
                viewModel?.resetAllFields()
                findNavController().popBackStack()
            }
        }

    }


    private fun showError(error : Int?) {
        if(error == null) return
        if(error > 0) NiroAppUtils.showSnackbar(getString(error),bindingEditProfileFragment.root)
    }


    private fun launchMandiLocationFragment() {
        findNavController().navigate(R.id.navigation_mandi_location, bundleOf(NIroAppConstants.PREVIOUS_SCREEN_ID to R.id.navigation_profile_edit,
            NIroAppConstants.ARG_SELECTED_MANDI to viewModel?.getMandiLocation()?.value)
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