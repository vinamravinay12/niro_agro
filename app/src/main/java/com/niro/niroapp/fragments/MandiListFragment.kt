package com.niro.niroapp.fragments

import androidx.lifecycle.ViewModelProviders
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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import carbon.dialog.ProgressDialog
import com.niro.niroapp.R
import com.niro.niroapp.activities.LoginActivity
import com.niro.niroapp.databinding.MandiListFragmentBinding
import com.niro.niroapp.models.APIError
import com.niro.niroapp.models.APILoader
import com.niro.niroapp.models.Success
import com.niro.niroapp.models.responsemodels.MandiLocation
import com.niro.niroapp.utils.CheckChangeListener
import com.niro.niroapp.utils.FragmentUtils
import com.niro.niroapp.utils.NIroAppConstants
import com.niro.niroapp.utils.NiroAppUtils
import com.niro.niroapp.viewmodels.MandiListViewModel
import com.niro.niroapp.viewmodels.SignupViewModel
import com.niro.niroapp.viewmodels.factories.MandiListViewModelFactory
import com.niro.niroapp.viewmodels.factories.SignUpViewModelFactory
import kotlin.math.sign

class MandiListFragment : Fragment(), CheckChangeListener {


    private lateinit var bindingMandiListFragment: MandiListFragmentBinding
    private var mandiListViewModel: MandiListViewModel? = null
    private var signUpViewModel: SignupViewModel? = null
    private var previousScreenId : Int = -1
    private var mSelectedMandiLocation : MandiLocation? = null


    companion object {
        fun newInstance() = MandiListFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            previousScreenId = it.getInt(NIroAppConstants.PREVIOUS_SCREEN_ID,-1)
            mSelectedMandiLocation = it.getParcelable(NIroAppConstants.ARG_SELECTED_MANDI)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingMandiListFragment =
            DataBindingUtil.inflate(inflater, R.layout.mandi_list_fragment, container, false)
        bindingMandiListFragment.lifecycleOwner = viewLifecycleOwner

        return bindingMandiListFragment.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mandiListViewModel = activity?.let { MandiListViewModelFactory().getViewModel(null,it) }

        if(activity is LoginActivity) {
            signUpViewModel = activity?.let { SignUpViewModelFactory(null).getViewModel(null, it) }
        }

        mandiListViewModel?.getSelectedMandiLocation()?.value = mSelectedMandiLocation
        initializeMandiListRecyclerView()
        initializeListeners()
        fetchMandiLocations()

    }

    override fun onResume() {
        super.onResume()
        FragmentUtils.hideKeyboard(bindingMandiListFragment.root,context)

    }

    private fun fetchMandiLocations() {
        var progressDialog: ProgressDialog? = null
        mandiListViewModel?.
        getAllMandiLocations(context)?.
        observe(viewLifecycleOwner, Observer { response ->


                when (response) {
                    is APILoader -> progressDialog = context?.let {
                        NiroAppUtils.showLoaderProgress(getString(R.string.fetching_mandi_list), it)
                    }

                    is APIError -> {
                        progressDialog?.dismiss()
                        NiroAppUtils.showSnackbar(
                            response.errorMessage,
                            bindingMandiListFragment.root
                        )
                    }

                    is Success<*> -> {
                        progressDialog?.dismiss()
                        mandiListViewModel?.setMandiList((response.data as? List<MandiLocation>)?.toMutableList())
                        if(signUpViewModel?.getSelectedMandiLocation()?.value != null) mandiListViewModel?.setSelectedMandiLocation(mandiListViewModel?.getSelectedMandiLocation()?.value,true)
                        mandiListViewModel?.updateList()
                    }
                }
            })
    }

    private fun initializeListeners() {

        bindingMandiListFragment.btnNext.setOnClickListener {
            if (mandiListViewModel?.getSelectedMandiLocation()?.value == null) {
                NiroAppUtils.showSnackbar(
                    message = getString(R.string.select_your_mandi),
                    root = bindingMandiListFragment.root
                )
            } else {
                launchSelectUserTypeFragment()
            }
        }

        bindingMandiListFragment.etSearchMandi.doAfterTextChanged { searchMandi() }
        bindingMandiListFragment.etSearchMandi.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus) FragmentUtils.hideKeyboard(bindingMandiListFragment.root,context)
        }
    }

    private fun searchMandi() {
        val searchTerm = bindingMandiListFragment.etSearchMandi.text.toString()
        mandiListViewModel?.getAdapter()?.filter?.filter(searchTerm)
    }

    private fun launchSelectUserTypeFragment() {

        if(previousScreenId != -1) {
            findNavController().navigate(previousScreenId, bundleOf(NIroAppConstants.ARG_SELECTED_MANDI to mandiListViewModel?.getSelectedMandiLocation()?.value))
            return
        }

        signUpViewModel?.getSelectedMandiLocation()?.value = mandiListViewModel?.getSelectedMandiLocation()?.value
        FragmentUtils.launchFragment(
            activity?.supportFragmentManager,
            view = R.id.fl_login_parent,
            fragment = UserTypeFragment(),
            tag = NIroAppConstants.TAG_USER_TYPE
        )
    }

    private fun getVariablesMap(): HashMap<Int, Any?> {
        return hashMapOf(BR.checkChangeListener to this)
    }

    private fun initializeMandiListRecyclerView() {
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        bindingMandiListFragment.rvMandiList.layoutManager = layoutManager
        bindingMandiListFragment.rvMandiList.setHasFixedSize(true)
        mandiListViewModel?.getAdapter()?.setVariablesMap(getVariablesMap())
        bindingMandiListFragment.rvMandiList.adapter = mandiListViewModel?.getAdapter()
    }

    override fun onCheckChanged(item: Any?) {
        FragmentUtils.hideKeyboard(bindingMandiListFragment.root,context)
        mandiListViewModel?.getSelectedMandiLocation()?.value?.isSelected = false
        mandiListViewModel?.getSelectedMandiLocation()?.value = item as? MandiLocation
        mandiListViewModel?.updateList()

    }

}