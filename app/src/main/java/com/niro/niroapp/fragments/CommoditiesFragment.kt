
package com.niro.niroapp.fragments

import android.os.Build
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
import com.niro.niroapp.databinding.CommoditiesFragmentBinding
import com.niro.niroapp.models.APIError
import com.niro.niroapp.models.APILoader
import com.niro.niroapp.models.Success
import com.niro.niroapp.models.responsemodels.Category
import com.niro.niroapp.models.responsemodels.CommodityItem
import com.niro.niroapp.models.responsemodels.User
import com.niro.niroapp.utils.*
import com.niro.niroapp.viewmodels.CommoditiesViewModel
import com.niro.niroapp.viewmodels.SignupViewModel
import com.niro.niroapp.viewmodels.factories.CommodityViewModelFactory
import com.niro.niroapp.viewmodels.factories.SignUpViewModelFactory

class CommoditiesFragment : AbstractBaseFragment(), CheckChangeListener,ItemClickListener {

    companion object {
        fun newInstance() = CommoditiesFragment()
    }

    private lateinit var bindingCommoditiesFragment: CommoditiesFragmentBinding
    private var commoditiesViewModel: CommoditiesViewModel? = null
    private var signUpViewModel: SignupViewModel? = null
    private var previousFragmentId = -1
    private var mSelectedCommodities = ArrayList<CommodityItem>()
    private var allowMultiSelect = true
    private var isEdit = false
    private var mCurrentUserId: String? = null
    private var mProgressDialog: ProgressDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            previousFragmentId = it.getInt(NiroAppConstants.PREVIOUS_SCREEN_ID, -1)
            if (it.getParcelableArrayList<CommodityItem>(NiroAppConstants.ARG_SELECTED_COMMODITIES) != null) {
                mSelectedCommodities.addAll(it.getParcelableArrayList<CommodityItem>(NiroAppConstants.ARG_SELECTED_COMMODITIES) as? ArrayList<CommodityItem>
                    ?: ArrayList())

            }
            allowMultiSelect = it.getBoolean(NiroAppConstants.ARG_ALLOW_MULTISELECT, true)
            isEdit = it.getBoolean(NiroAppConstants.ARG_COMMODITIES_EDIT, false)
            mCurrentUserId = it.getString(NiroAppConstants.ARG_CURRENT_USER_ID)

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        bindingCommoditiesFragment =
            DataBindingUtil.inflate(inflater, R.layout.commodities_fragment, container, false)
        bindingCommoditiesFragment.lifecycleOwner = viewLifecycleOwner
        return bindingCommoditiesFragment.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        commoditiesViewModel = activity?.let { CommodityViewModelFactory().getViewModel(null, it) }

        if (activity is LoginActivity) {
            signUpViewModel = activity?.let { SignUpViewModelFactory("").getViewModel(null, it) }
        }

        commoditiesViewModel?.getSelectedCommoditiesList()?.value = mSelectedCommodities

        initializeCommoditiesRecyclerView()
        initializeListeners()
        fetchCommoditiesList()


    }


    override fun onResume() {
        super.onResume()
        FragmentUtils.hideKeyboard(bindingCommoditiesFragment.root, context)

    }


    private fun fetchCommoditiesList() {
        commoditiesViewModel?.getAllCommodities(context)
            ?.observe(viewLifecycleOwner, Observer { response ->


                when (response) {
                    is APILoader -> mProgressDialog = context?.let {
                        NiroAppUtils.showLoaderProgress(
                            message = getString(R.string.txt_fetching_commodities),
                            context = it
                        )
                    }

                    is APIError -> {
                        if(mProgressDialog != null && mProgressDialog?.isShowing == true) mProgressDialog?.dismiss()
                        NiroAppUtils.showSnackbar(
                            message = response.errorMessage,
                            root = bindingCommoditiesFragment.root
                        )
                    }

                    is Success<*> -> {
                        if(mProgressDialog != null && mProgressDialog?.isShowing == true) mProgressDialog?.dismiss()
                        updateCategoriesList(response.data as? List<Category>)
                    }

                }
            })
    }

    private fun updateCategoriesList(categoryList: List<Category>?) {
        categoryList?.toMutableList()
            ?.let { commoditiesViewModel?.setCategories(categoriesList = it) }
        commoditiesViewModel?.setSelectedCommodities(commoditiesViewModel?.getSelectedCommoditiesList()?.value)
        commoditiesViewModel?.updateList()
    }


    private fun initializeListeners() {

        super.registerBackPressedCallback(if(previousFragmentId == -1) R.id.enterBusinessFragment else previousFragmentId)

        bindingCommoditiesFragment.btnNext.setOnClickListener {
            if (commoditiesViewModel?.getSelectedCommoditiesList()?.value.isNullOrEmpty()) {
                NiroAppUtils.showSnackbar(
                    message = getString(R.string.select_atleast_one_commodity),
                    root = bindingCommoditiesFragment.root
                )
            } else {
                launchSelectMandiListFragment()
            }
        }

        bindingCommoditiesFragment.etSearchCommodity.doAfterTextChanged { searchCommodities() }
        bindingCommoditiesFragment.etSearchCommodity.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) FragmentUtils.hideKeyboard(bindingCommoditiesFragment.root, context)
        }

    }

    private fun searchCommodities() {

        val searchTerm = bindingCommoditiesFragment.etSearchCommodity.text.toString()
        commoditiesViewModel?.getAdapter()?.filter?.filter(searchTerm)
    }

    private fun launchSelectMandiListFragment() {

        if (previousFragmentId != -1) {
            findNavController().previousBackStackEntry?.savedStateHandle?.set(
                NiroAppConstants.ARG_SELECTED_COMMODITIES,
                commoditiesViewModel?.getSelectedCommoditiesList()?.value
            )
            findNavController().popBackStack()
            return
        } else if (isEdit && !mCurrentUserId.isNullOrEmpty()) {
            updateCurrentUserCommodities()
            return
        }

        signUpViewModel?.getSelectedCommodities()?.value =
            commoditiesViewModel?.getSelectedCommoditiesList()?.value

        findNavController().navigate(R.id.action_commoditiesFragment_to_mandiListFragment)
    }

    private fun updateCurrentUserCommodities() {

        commoditiesViewModel?.updateCommodities(mCurrentUserId, context)
            ?.observe(viewLifecycleOwner,
                Observer { response ->


                    when (response) {
                        is APILoader -> mProgressDialog = context?.let {
                            NiroAppUtils.showLoaderProgress(
                                message = getString(R.string.updating_commodities),
                                context = it
                            )
                        }

                        is APIError -> {
                            if(mProgressDialog != null && mProgressDialog?.isShowing == true) mProgressDialog?.dismiss()
                            NiroAppUtils.showSnackbar(
                                message = response.errorMessage,
                                root = bindingCommoditiesFragment.root
                            )
                        }

                        is Success<*> -> {
                            if(mProgressDialog != null && mProgressDialog?.isShowing == true)  mProgressDialog?.dismiss()
                            context?.let {
                                NiroAppUtils.updateCurrentUser(
                                    response.data as? User,
                                    it
                                )
                            }
                            findNavController().popBackStack()
                        }

                    }
                })
    }

    private fun initializeCommoditiesRecyclerView() {

        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        bindingCommoditiesFragment.rvCommodities.layoutManager = layoutManager

        val commodityListAdapter = commoditiesViewModel?.getAdapter()
        commodityListAdapter?.setVariablesMap(getVariablesMap())
        bindingCommoditiesFragment.rvCommodities.adapter = commodityListAdapter

    }

    private fun getVariablesMap(): HashMap<Int, Any?> {
        return hashMapOf(BR.itemClickListener to this,BR.checkChangeListener to this)
    }


    override fun onCheckChanged(item: Any?) {

        if (allowMultiSelect) selectValues(item)
        else selectCurrentValue(item)

    }

    private fun selectCurrentValue(item: Any?) {
        FragmentUtils.hideKeyboard(bindingCommoditiesFragment.root, context)
        val commodityItem = item as? CommodityItem
        if (commoditiesViewModel?.getSelectedCommoditiesList()?.value?.isEmpty() != true) {

            commoditiesViewModel?.getSelectedCommoditiesList()?.value?.get(0)?.isSelected = false
            commoditiesViewModel?.getSelectedCommoditiesList()?.value?.get(0)?.let {
                commoditiesViewModel?.getUnSelectedCommodities()?.value?.add(it)
            }
            commoditiesViewModel?.getSelectedCommoditiesList()?.value?.clear()
        }

        if (commodityItem != null) {
            commodityItem.isSelected = true
            commoditiesViewModel?.getSelectedCommoditiesList()?.value?.add(commodityItem)
            commoditiesViewModel?.updateUnSelectedItems()
            commoditiesViewModel?.updateSelectedItems()
        }
    }

    private fun selectValues(item: Any?) {
        FragmentUtils.hideKeyboard(bindingCommoditiesFragment.root, context)
        val commodityItem = item as? CommodityItem

        val itemIndex = commoditiesViewModel?.getSelectedCommoditiesList()?.value?.indexOfFirst { it.id == commodityItem?.id } ?: -1
        if(itemIndex >= 0){
           commoditiesViewModel?.getSelectedCommoditiesList()?.value?.get(itemIndex)?.isSelected = false
            commoditiesViewModel?.updateSelectedItems()
            removeUnSelectedItems(commodityItem)
        } else commodityItem?.let {
            commoditiesViewModel?.getSelectedCommoditiesList()?.value?.add(it)
            commoditiesViewModel?.setSelectedCommodities(commoditiesViewModel?.getSelectedCommoditiesList()?.value)
            commoditiesViewModel?.updateSelectedItems()

        }

    }


    private fun removeUnSelectedItems(commodityItem: CommodityItem?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            commoditiesViewModel?.getSelectedCommoditiesList()?.value?.removeIf { it.id == commodityItem?.id  && !it.isSelected }
        } else {
            searchForSelectedCommodityAndRemove(commodityItem)
        }
    }

    private fun searchForSelectedCommodityAndRemove(commodityItem: CommodityItem?) {
        if (commoditiesViewModel?.getSelectedCommoditiesList()?.value.isNullOrEmpty()) return

        val iterator = commoditiesViewModel?.getSelectedCommoditiesList()?.value?.iterator()
        if (iterator != null) {
            while (iterator.hasNext()) {
                val item = iterator.next()
                if (item.id == commodityItem?.id && !item.isSelected) {
                    commoditiesViewModel?.getSelectedCommoditiesList()?.value?.remove(item)
                    break
                }
            }
        }
    }

    override fun onItemClick(item: Any?) {
        if (allowMultiSelect) selectValues(item)
        else selectCurrentValue(item)
    }

}