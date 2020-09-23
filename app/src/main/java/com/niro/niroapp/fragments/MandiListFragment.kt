package com.niro.niroapp.fragments

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
import com.niro.niroapp.R
import com.niro.niroapp.activities.LoginActivity
import com.niro.niroapp.databinding.MandiListFragmentBinding
import com.niro.niroapp.models.APIError
import com.niro.niroapp.models.APILoader
import com.niro.niroapp.models.Success
import com.niro.niroapp.models.responsemodels.MandiLocation
import com.niro.niroapp.utils.*
import com.niro.niroapp.viewmodels.MandiListViewModel
import com.niro.niroapp.viewmodels.SignupViewModel
import com.niro.niroapp.viewmodels.factories.MandiListViewModelFactory
import com.niro.niroapp.viewmodels.factories.SignUpViewModelFactory

class MandiListFragment : AbstractBaseFragment(), CheckChangeListener,ItemClickListener {


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
            previousScreenId = it.getInt(NiroAppConstants.PREVIOUS_SCREEN_ID,-1)
            mSelectedMandiLocation = it.getParcelable(NiroAppConstants.ARG_SELECTED_MANDI)
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
                        if(progressDialog != null && progressDialog?.isShowing == true) progressDialog?.dismiss()
                        NiroAppUtils.showSnackbar(
                            response.errorMessage,
                            bindingMandiListFragment.root
                        )
                    }

                    is Success<*> -> {
                        if(progressDialog != null && progressDialog?.isShowing == true) progressDialog?.dismiss()
                        mandiListViewModel?.setMandiList((response.data as? List<MandiLocation>)?.toMutableList())
                        if(mandiListViewModel?.getSelectedMandiLocation()?.value != null) mandiListViewModel?.setSelectedMandiLocation(mandiListViewModel?.getSelectedMandiLocation()?.value,true)
                        mandiListViewModel?.updateList()
                    }
                }
            })
    }

    private fun initializeListeners() {

        super.registerBackPressedCallback(if(previousScreenId == -1) R.id.commoditiesFragment else previousScreenId)
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
            findNavController().previousBackStackEntry?.savedStateHandle?.set(NiroAppConstants.ARG_SELECTED_MANDI, mandiListViewModel?.getSelectedMandiLocation()?.value)
            findNavController().popBackStack()
            return
        }

        signUpViewModel?.getSelectedMandiLocation()?.value = mandiListViewModel?.getSelectedMandiLocation()?.value
        findNavController().navigate(R.id.action_mandiListFragment_to_userTypeFragment)
    }

    private fun getVariablesMap(): HashMap<Int, Any?> {
        return hashMapOf(BR.itemClickListener to this)
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
        mandiListViewModel?.getUnSelectedLocation()?.value = mandiListViewModel?.getSelectedMandiLocation()?.value
        mandiListViewModel?.getSelectedMandiLocation()?.value = item as? MandiLocation
        mandiListViewModel?.updateList()

    }

    override fun onItemClick(item: Any?) {
        FragmentUtils.hideKeyboard(bindingMandiListFragment.root,context)
        mandiListViewModel?.getSelectedMandiLocation()?.value?.isSelected = false
        mandiListViewModel?.getUnSelectedLocation()?.value = mandiListViewModel?.getSelectedMandiLocation()?.value
        mandiListViewModel?.getSelectedMandiLocation()?.value = item as? MandiLocation
        mandiListViewModel?.updateSelectedLocation()
    }

}