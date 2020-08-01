package com.niro.niroapp.users.fragments


import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import carbon.dialog.ProgressDialog
import com.niro.niroapp.R
import com.niro.niroapp.databinding.EnterUserFragmentBinding
import com.niro.niroapp.models.APIError
import com.niro.niroapp.models.APILoader
import com.niro.niroapp.models.APIResponse
import com.niro.niroapp.models.Success
import com.niro.niroapp.models.responsemodels.CommodityItem
import com.niro.niroapp.models.responsemodels.Contact
import com.niro.niroapp.models.responsemodels.MandiLocation
import com.niro.niroapp.models.responsemodels.User
import com.niro.niroapp.users.viewmodels.EnterUserViewModel
import com.niro.niroapp.users.viewmodels.factories.EnterUserViewModelFactory
import com.niro.niroapp.utils.NIroAppConstants
import com.niro.niroapp.utils.NiroAppUtils

class EnterUserFragment : Fragment() {

    private var viewModel: EnterUserViewModel? = null
    private lateinit var bindingEnterUserFragment: EnterUserFragmentBinding
    private var mSelectedContact: Contact? = null
    private var mProgressDialog : ProgressDialog? = null
    private var mSelectedCommodities = ArrayList<CommodityItem>()
    private var mSelectedMandiLocation : MandiLocation? = null


    companion object {
        fun newInstance() = EnterUserFragment()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mSelectedContact = it.getParcelable(NIroAppConstants.ARG_CONTACT)
            if(it.getParcelableArrayList<CommodityItem>(NIroAppConstants.ARG_SELECTED_COMMODITIES) != null) {
                mSelectedCommodities =
                    it.getParcelableArrayList<CommodityItem>(NIroAppConstants.ARG_SELECTED_COMMODITIES) as ArrayList<CommodityItem>
            }

            mSelectedMandiLocation = it.getParcelable<MandiLocation>(NIroAppConstants.ARG_SELECTED_MANDI)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        bindingEnterUserFragment =
            DataBindingUtil.inflate(inflater, R.layout.enter_user_fragment, container, false)
        bindingEnterUserFragment.lifecycleOwner = viewLifecycleOwner

        return bindingEnterUserFragment.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if(mSelectedContact == null) mSelectedContact = Contact("","","")
        viewModel = activity?.let {

            EnterUserViewModelFactory(mSelectedContact).getViewModel(mSelectedContact, owner = it)
        }
        bindingEnterUserFragment.addContactVM = viewModel
        viewModel?.resetAllFields()

        viewModel?.getSelectedCommodity()?.value = mSelectedCommodities
        viewModel?.getMandiLocation()?.value = mSelectedMandiLocation

        makeFieldsReadOnly()
        initializeCurrentUserDetails()
        initializeFocusChangeListeners()
        initializeClickListeners()
        viewModel?.fillContactDetails(mSelectedContact)
    }

    private fun makeFieldsReadOnly() {
        bindingEnterUserFragment.etEnterLocation.setReadOnly(true)
        bindingEnterUserFragment.etEnterCommodity.setReadOnly(true)
    }

    private fun initializeCurrentUserDetails() {
        val currentUser = context?.let { NiroAppUtils.getCurrentUser(it) }

        viewModel?.getCurrentUser()?.value = currentUser

    }

    private fun initializeClickListeners() {

        bindingEnterUserFragment.etEnterLocation.setOnClickListener { launchMandiLocationFragment() }

        bindingEnterUserFragment.etEnterCommodity.setOnClickListener { launchSelectCommodityFragment() }

        bindingEnterUserFragment.btnAddContact.setOnClickListener { createContact() }

    }


    private fun initializeFocusChangeListeners() {
        bindingEnterUserFragment.etEnterName.setOnFocusChangeListener {_,hasFocus ->
            if(!hasFocus && (viewModel?.validateUserName()?.value ?: 0 > 0)) {
                bindingEnterUserFragment.etEnterName.error = getString(viewModel?.validateUserName()?.value!!)
            }
        }

        bindingEnterUserFragment.etEnterBusiness.setOnFocusChangeListener {_,hasFocus ->
            if(!hasFocus && (viewModel?.validateBusinessName()?.value ?: 0 > 0)) {
                bindingEnterUserFragment.etEnterBusiness.error = getString(viewModel?.validateBusinessName()?.value!!)
            }
        }

        bindingEnterUserFragment.etEnterNumber.setOnFocusChangeListener {_,hasFocus ->
            if(!hasFocus && (viewModel?.validatePhoneNumber()?.value ?: 0 > 0)) {
                bindingEnterUserFragment.etEnterNumber.error = getString(viewModel?.validatePhoneNumber()?.value!!)
            }
            else if(!hasFocus && viewModel?.validatePhoneNumber()?.value ?: 0 == -1) {
                viewModel?.getUserFromPhoneNumber(context)?.observe(viewLifecycleOwner, Observer { handleFindUserResponse(it) })
            }
        }

        bindingEnterUserFragment.etEnterCommodity.setOnFocusChangeListener {_,hasFocus ->
            if(!hasFocus && (viewModel?.validateSelectedCommodity()?.value ?: 0 > 0)) {
                bindingEnterUserFragment.etEnterCommodity.error = getString(viewModel?.validateSelectedCommodity()?.value!!)
            }
        }


        bindingEnterUserFragment.etEnterLocation.setOnFocusChangeListener {_,hasFocus ->
            if(!hasFocus && (viewModel?.validateSelectedMandi()?.value ?: 0 > 0)) {
                bindingEnterUserFragment.etEnterLocation.error = getString(viewModel?.validateSelectedMandi()?.value!!)
            }
        }
    }

    private fun createContact() {
        if(viewModel?.validateAllFields() ?: 0 > 0)  {
            showError(viewModel?.validateAllFields())
            return
        }


        viewModel?.createContact(context)?.observe(viewLifecycleOwner, Observer { handleCreateContactResponse(it) })
    }

    private fun handleCreateContactResponse(response: APIResponse?) {
        when(response) {
            is APILoader -> mProgressDialog = context?.let {
                NiroAppUtils.showLoaderProgress(getString(R.string.creating_contact), it)
            }

            is APIError -> {
                mProgressDialog?.dismiss()
                NiroAppUtils.showSnackbar(response.errorMessage,bindingEnterUserFragment.root)
            }

            is Success<*> -> {
                mProgressDialog?.dismiss()
                NiroAppUtils.showSnackbar(getString(R.string.contact_created),bindingEnterUserFragment.root)
                viewModel?.resetAllFields()
                goBackToContactsScreen()
            }
        }

    }

    private fun goBackToContactsScreen() {
        findNavController().popBackStack(R.id.navigation_loaders,false)
    }


    private fun launchSelectCommodityFragment() {
        findNavController().navigate(R.id.navigation_commodities_fragment, bundleOf(NIroAppConstants.PREVIOUS_SCREEN_ID to R.id.navigation_create_users_manually,
            NIroAppConstants.ARG_ALLOW_MULTISELECT to false))
    }

    private fun launchMandiLocationFragment() {
        findNavController().navigate(R.id.navigation_mandi_location, bundleOf(NIroAppConstants.PREVIOUS_SCREEN_ID to R.id.navigation_create_users_manually,
        NIroAppConstants.ARG_SELECTED_MANDI to viewModel?.getMandiLocation()?.value))

    }

    private fun handleFindUserResponse(response : APIResponse?) {

        when(response) {
            is APILoader -> mProgressDialog = context?.let {
                NiroAppUtils.showLoaderProgress(getString(R.string.checking_user_existing), it )
            }

            is APIError -> {
                mProgressDialog?.dismiss()
                NiroAppUtils.showSnackbar(message = response.errorMessage,root = bindingEnterUserFragment.root)
            }

            is Success<*> -> {
                mProgressDialog?.dismiss()
                viewModel?.fillUserDetails(response.data as? User)
            }
        }

    }


    private fun showError(error : Int?) {
        if(error == null) return
        if(error > 0) NiroAppUtils.showSnackbar(getString(error),bindingEnterUserFragment.root)
    }

}

fun EditText.setReadOnly(value: Boolean, inputType: Int = InputType.TYPE_NULL) {
    isFocusable = !value
    isFocusableInTouchMode = !value
    this.inputType = inputType
}