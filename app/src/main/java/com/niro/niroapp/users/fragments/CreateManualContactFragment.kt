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
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.niro.niroapp.R
import com.niro.niroapp.activities.MainActivity
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
import com.niro.niroapp.utils.NiroAppConstants
import com.niro.niroapp.utils.NavigationBackPressedCallback
import com.niro.niroapp.utils.NiroAppUtils
import com.niro.niroapp.utils.OnBackPressedListener

class CreateManualContactFragment : Fragment(), OnBackPressedListener {

    private var viewModel: EnterUserViewModel? = null
    private lateinit var bindingEnterUserFragment: EnterUserFragmentBinding
    private var mSelectedContact: Contact? = null
    private var mProgressDialog : ProgressDialog? = null
    private var mSelectedCommodities = ArrayList<CommodityItem>()
    private var mSelectedMandiLocation : MandiLocation? = null
    private var mPreviousScreenId : Int = -1
    private lateinit var firebaseAnalytics: FirebaseAnalytics


    companion object {
        fun newInstance() = CreateManualContactFragment()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mSelectedContact = it.getParcelable(NiroAppConstants.ARG_CONTACT)
            if(it.getParcelableArrayList<CommodityItem>(NiroAppConstants.ARG_SELECTED_COMMODITIES) != null) {
                mSelectedCommodities =
                    it.getParcelableArrayList<CommodityItem>(NiroAppConstants.ARG_SELECTED_COMMODITIES) as ArrayList<CommodityItem>
            }

            mPreviousScreenId = it.getInt(NiroAppConstants.PREVIOUS_SCREEN_ID)
            mSelectedMandiLocation = it.getParcelable(NiroAppConstants.ARG_SELECTED_MANDI)

        }

        firebaseAnalytics = Firebase.analytics

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        bindingEnterUserFragment =
            DataBindingUtil.inflate(inflater, R.layout.enter_user_fragment, container, false)
        bindingEnterUserFragment.lifecycleOwner = viewLifecycleOwner

        firebaseAnalytics.setCurrentScreen(requireActivity(),getString(R.string.title_create_contact),null)

        return bindingEnterUserFragment.root
    }




    private fun updateMandiLocationDisplayValue() {
        bindingEnterUserFragment.etEnterLocation.setText(viewModel?.getMandiDisplayName()?.value ?: "")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        if(mSelectedContact == null) mSelectedContact = Contact("","","")
        viewModel = activity?.let {
            EnterUserViewModelFactory(mSelectedContact).getViewModel(mSelectedContact, owner = it)
        }

        bindingEnterUserFragment.addContactVM = viewModel
        viewModel?.getSelectedCommodity()?.value = mSelectedCommodities
        viewModel?.getMandiLocation()?.value = mSelectedMandiLocation

        onSelectedMandiFetched()
        onSelectedCommoditiesFetched()

        setPageTitle()

        makeFieldsReadOnly()
        initializeCurrentUserDetails()
        initializeFocusChangeListeners()
        initializeClickListeners()
        viewModel?.fillContactDetails(mSelectedContact)

    }


    private fun setPageTitle(){
        when(activity) {
            is MainActivity -> (activity as MainActivity).setToolbarTitleAndImage(getString(R.string.title_create_contact), R.drawable.ic_user_type)
            else -> ""
        }
    }


    private  fun onSelectedMandiFetched() {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<MandiLocation>(NiroAppConstants.ARG_SELECTED_MANDI)?.observe(
            viewLifecycleOwner, Observer {
                viewModel?.getMandiLocation()?.value = it
                updateValuesInViews()
            })
    }

    private fun onSelectedCommoditiesFetched() {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<ArrayList<CommodityItem>>(NiroAppConstants.ARG_SELECTED_COMMODITIES)?.observe(
            viewLifecycleOwner, Observer {
                viewModel?.getSelectedCommodity()?.value = it
                updateValuesInViews()
            }
        )
    }

    private fun updateValuesInViews() {
        bindingEnterUserFragment.etEnterName.setText(viewModel?.getContactName()?.value ?: "")
        bindingEnterUserFragment.etEnterLocation.setText(viewModel?.getMandiDisplayName()?.value ?: "")
        bindingEnterUserFragment.etEnterCommodity.setText(viewModel?.getSelectedCommodityDisplayName()?.value ?: "")
        bindingEnterUserFragment.etEnterNumber.setText(viewModel?.getPhoneNumber()?.value ?: "")
        bindingEnterUserFragment.etEnterBusiness.setText(viewModel?.getBusinessName()?.value ?: "")
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

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,NavigationBackPressedCallback(this))

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
                if(mProgressDialog != null && mProgressDialog?.isShowing == true) mProgressDialog?.dismiss()
                NiroAppUtils.showSnackbar(response.errorMessage,bindingEnterUserFragment.root)
            }

            is Success<*> -> {
                if(mProgressDialog != null && mProgressDialog?.isShowing == true) mProgressDialog?.dismiss()
                NiroAppUtils.showSnackbar(getString(R.string.contact_created),bindingEnterUserFragment.root)
                viewModel?.resetAllFields()
                onContactCreated(response.data as? String)

            }
        }

    }

    private fun onContactCreated(data : String?) {
        if(mPreviousScreenId != -1) {
            findNavController().getBackStackEntry(mPreviousScreenId).savedStateHandle.set(NiroAppConstants.ARG_USER_CONTACT_ID, data)
            findNavController().popBackStack()
            return
        }

        goBackToContactsScreen()
    }

    private fun goBackToContactsScreen() {
        findNavController().popBackStack(R.id.navigation_loaders,false)
    }


    private fun launchSelectCommodityFragment() {
        findNavController().navigate(R.id.navigation_commodities_fragment, bundleOf(NiroAppConstants.PREVIOUS_SCREEN_ID to R.id.navigation_create_users_manually,
            NiroAppConstants.ARG_ALLOW_MULTISELECT to false))
    }

    private fun launchMandiLocationFragment() {
        findNavController().navigate(R.id.navigation_mandi_location, bundleOf(NiroAppConstants.PREVIOUS_SCREEN_ID to R.id.navigation_create_users_manually,
        NiroAppConstants.ARG_SELECTED_MANDI to viewModel?.getMandiLocation()?.value))

    }

    private fun handleFindUserResponse(response : APIResponse?) {

        when(response) {
            is APILoader -> mProgressDialog = context?.let {
                NiroAppUtils.showLoaderProgress(getString(R.string.checking_user_existing), it )
            }

            is APIError -> {
                if(mProgressDialog != null && mProgressDialog?.isShowing == true) mProgressDialog?.dismiss()
                NiroAppUtils.showSnackbar(message = response.errorMessage,root = bindingEnterUserFragment.root)
            }

            is Success<*> -> {
                if(mProgressDialog != null && mProgressDialog?.isShowing == true) mProgressDialog?.dismiss()
                viewModel?.fillUserDetails(response.data as? User)
            }
        }

    }


    private fun showError(error : Int?) {
        if(error == null) return
        if(error > 0) NiroAppUtils.showSnackbar(getString(error),bindingEnterUserFragment.root)
    }

    override fun onBackPressed() {
        viewModel?.resetAllFields()
        findNavController().popBackStack(R.id.navigation_loaders,false)
    }


}

fun EditText.setReadOnly(value: Boolean, inputType: Int = InputType.TYPE_NULL) {
    isFocusable = !value
    isFocusableInTouchMode = !value
    this.inputType = inputType
}