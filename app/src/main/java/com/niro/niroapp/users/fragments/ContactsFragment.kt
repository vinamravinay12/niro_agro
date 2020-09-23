package com.niro.niroapp.users.fragments


import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.niro.niroapp.R
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.Observer
import carbon.dialog.ProgressDialog
import com.niro.niroapp.activities.MainActivity
import com.niro.niroapp.databinding.ContactsFragmentBinding
import com.niro.niroapp.models.APIError
import com.niro.niroapp.models.APILoader
import com.niro.niroapp.models.Success
import com.niro.niroapp.models.responsemodels.Contact
import com.niro.niroapp.users.viewmodels.ContactsViewModel
import com.niro.niroapp.users.viewmodels.factories.ContactsViewModelFactory
import com.niro.niroapp.utils.*

class ContactsFragment : Fragment(), ItemClickListener, OnBackPressedListener {

    private lateinit var bindingContactsFragment: ContactsFragmentBinding
    private var contactsViewModel: ContactsViewModel? = null
    private var mPreviousScreenId : Int? = -1

    companion object {
        fun newInstance() = ContactsFragment()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            mPreviousScreenId = it.getInt(NiroAppConstants.PREVIOUS_SCREEN_ID, -1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        bindingContactsFragment =
            DataBindingUtil.inflate(inflater, R.layout.contacts_fragment, container, false)
        bindingContactsFragment.lifecycleOwner = viewLifecycleOwner

        return bindingContactsFragment.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        contactsViewModel = activity?.let { ContactsViewModelFactory().getViewModel(null, it) }

        setPageTitle()
        initializeContactsRecyclerView()
        initializeListeners()

    }

    private fun setPageTitle() {
        if(activity is MainActivity) (activity as? MainActivity)?.setToolbarTitleAndImage(getString(R.string.title_select_from_contacts), R.drawable.ic_contacts)

    }

    override fun onResume() {
        super.onResume()
        fetchContacts()
    }

    private fun initializeListeners() {

        NiroAppUtils.setBackPressedCallback(requireActivity(),viewLifecycleOwner,this)

        bindingContactsFragment.cvManualEnter.setOnClickListener {
            findNavController().navigate(R.id.navigation_create_users_manually, bundleOf(NiroAppConstants.PREVIOUS_SCREEN_ID to mPreviousScreenId))
        }

        bindingContactsFragment.etSearchContacts.doAfterTextChanged { filterResult() }
    }

    private fun filterResult() {
        val searchTerm = bindingContactsFragment.etSearchContacts.text.toString()
        contactsViewModel?.getAdapter()?.filter?.filter(searchTerm)
    }

    private fun initializeContactsRecyclerView() {

        bindingContactsFragment.rvContacts.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val adapter = contactsViewModel?.getAdapter()
        adapter?.setVariablesMap(hashMapOf(BR.itemClickListener to this))
        bindingContactsFragment.rvContacts.setHasFixedSize(true)
        bindingContactsFragment.rvContacts.adapter = adapter

    }

    private fun fetchContacts() {

        if (PermissionUtils.isReadContactsPermissionGranted(context)) {
            fetchContactsFromRepository()

        } else PermissionUtils.askForPermissions(activity, arrayOf(NiroAppConstants.CODE_READ_CONTACTS))



    }

    private fun fetchContactsFromRepository() {
        var progressDialog : ProgressDialog? = null

        context?.let { contactsViewModel?.fetchContacts(it) }
        context?.let {
            contactsViewModel?.getResponse()?.observe(viewLifecycleOwner, Observer { response ->
                when(response) {
                    is APILoader ->  {
                        showParentLayout(false)
                        progressDialog =  NiroAppUtils.showLoaderProgress(getString(R.string.fetch_contacts),it)
                    }

                    is APIError -> {
                        progressDialog?.dismiss()
                        showParentLayout(false)
                        NiroAppUtils.showSnackbar(response.errorMessage,bindingContactsFragment.root)
                    }

                    is Success<*> -> {
                        progressDialog?.dismiss()
                        val contactsList = response.data as? ArrayList<Contact>

                        if(contactsList.isNullOrEmpty() ) showParentLayout(false) else showParentLayout(true)

                        contactsViewModel?.getContactsData()?.value = contactsList?.toMutableList()

                        contactsViewModel?.updateList()



                    }
                }
            })
        }
    }

    private fun showParentLayout(toShow: Boolean) {
        bindingContactsFragment.rvContacts.visibility = if(toShow) View.VISIBLE else View.INVISIBLE

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) fetchContactsFromRepository()
    }

    override fun onItemClick(item: Any?) {
        val selectedContact = item as? Contact
        findNavController().navigate(R.id.navigation_create_users_manually, bundleOf(NiroAppConstants.ARG_CONTACT to selectedContact, NiroAppConstants.PREVIOUS_SCREEN_ID to mPreviousScreenId))
    }

    override fun onBackPressed() {
        findNavController().popBackStack(R.id.navigation_loaders,false)
    }


}