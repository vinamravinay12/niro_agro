package com.niro.niroapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import carbon.dialog.ProgressDialog
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.niro.niroapp.R
import com.niro.niroapp.database.SharedPreferenceManager
import com.niro.niroapp.databinding.LiveMandiListFragmentBinding
import com.niro.niroapp.models.APIError
import com.niro.niroapp.models.APILoader
import com.niro.niroapp.models.Success
import com.niro.niroapp.models.responsemodels.MandiLocation
import com.niro.niroapp.models.responsemodels.MandiRatesRecord
import com.niro.niroapp.models.responsemodels.User
import com.niro.niroapp.utils.DateUtils
import com.niro.niroapp.utils.FragmentUtils
import com.niro.niroapp.utils.NiroAppConstants
import com.niro.niroapp.utils.NiroAppUtils
import com.niro.niroapp.viewmodels.DailyMandiRateListViewModel
import java.util.*
import kotlin.collections.ArrayList

class DailyMandiRateListFragment : AbstractBaseFragment() {


    private var viewModelRate: DailyMandiRateListViewModel? = null
    private lateinit var bindingLiveMandiList: LiveMandiListFragmentBinding
    private var mCurrentUser: User? = null
    private var mProgressDialog: ProgressDialog? = null
    private lateinit var firebaseAnalytics: FirebaseAnalytics


    companion object {
        fun newInstance() = DailyMandiRateListFragment()
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
        bindingLiveMandiList =
            DataBindingUtil.inflate(inflater, R.layout.live_mandi_list_fragment, container, false)
        bindingLiveMandiList.lifecycleOwner = viewLifecycleOwner

        return bindingLiveMandiList.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModelRate =
            ViewModelProvider(requireActivity()).get(DailyMandiRateListViewModel::class.java)

        viewModelRate?.getSelectedMandi()?.value = mCurrentUser?.selectedMandi

        firebaseAnalytics.setCurrentScreen(
            requireActivity(),
            getString(R.string.title__mandi_rates),
            null
        )

        super.setPageTitle(getString(R.string.title__mandi_rates), -1)
        onSelectedMandiFetched()
        initializeHeader()
        initializeRecyclerView()
        initializeListeners()
    }


    override fun onResume() {
        super.onResume()
        if (!viewModelRate?.getMandiRates()?.value.isNullOrEmpty()) {
            bindingLiveMandiList.refreshDailyMandiList.isRefreshing = true
        } else {
            showNoUsers(true)
        }

        fetchMandiRates()
    }

    private fun fetchMandiRates() {

        if (isMandiRatesFetchedForToday()) {
            handleProgress("", false)
            val savedMandiRatesList = getSavedMandiRates()
            if (savedMandiRatesList.isEmpty()) fetchMandiRatesFromServer()
            else handleSuccessResponse(getSavedMandiRates())

        } else {
            fetchMandiRatesFromServer()

        }
    }

    private fun fetchMandiRatesFromServer() {
        viewModelRate?.getMandiRatesForDate(viewModelRate?.getDate()?.value ?: "", context)
            ?.observe(viewLifecycleOwner, Observer { response ->
                when (response) {

                    is APILoader -> {

                        handleProgress(getString(R.string.fetching_mandi_rates_for_today), true)
                    }

                    is APIError -> {
                        handleProgress("", false)
                        NiroAppUtils.showSnackbar(
                            response.errorMessage,
                            bindingLiveMandiList.root
                        )
                    }

                    is Success<*> -> {

                        handleProgress("", false)

                        handleSuccessResponse(response.data as? List<MandiRatesRecord>)
                        saveMandiRates(records = response.data as? List<MandiRatesRecord>)
                    }
                }

            })
    }

    private fun isMandiRatesFetchedForToday(): Boolean {
        return SharedPreferenceManager(
            requireContext(),
            NiroAppConstants.LOGIN_SP
        ).getBooleanPreference(
            DateUtils.getDateString(
                Date(), postDateFormat = NiroAppConstants.POST_DATE_FORMAT
            ), false
        )

    }


    private fun saveMandiRates(records: List<MandiRatesRecord>?) {
        if (records?.isEmpty() == true) return

        val sharedPreferenceManager =
            SharedPreferenceManager(requireContext(), NiroAppConstants.LOGIN_SP)
        sharedPreferenceManager.storeComplexObjectPreference(
            NiroAppConstants.KEY_MANDI_RECORDS,
            records
        )
        sharedPreferenceManager.storeBooleanPreference(
            DateUtils.getDateString(
                Date(), postDateFormat = NiroAppConstants.POST_DATE_FORMAT
            ), true
        )
    }

    private fun getSavedMandiRates(): List<MandiRatesRecord> {
        return try {
            val mandiRatesJson = SharedPreferenceManager(
                requireContext(),
                NiroAppConstants.LOGIN_SP
            ).getStringPreference(NiroAppConstants.KEY_MANDI_RECORDS)

            Gson().fromJson(
                mandiRatesJson,
                object : TypeToken<List<MandiRatesRecord>>() {}.type
            )
        } catch (exception: Exception) {
            return ArrayList()
        }
    }

    private fun handleSuccessResponse(mandiRates: List<MandiRatesRecord>?) {
        if (mandiRates.isNullOrEmpty()) {
            showNoUsers(true)
            return
        }

        showNoUsers(false)
        viewModelRate?.setMandiRates(mandiRates.toMutableList())

        viewModelRate?.updateList()
    }

    private fun initializeHeader() {
        bindingLiveMandiList.tvDate.text = viewModelRate?.getDate()?.value ?: ""

    }


    private fun onSelectedMandiFetched() {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<MandiLocation>(
            NiroAppConstants.ARG_SELECTED_MANDI
        )?.observe(
            viewLifecycleOwner, Observer {
                viewModelRate?.getSelectedMandi()?.value = it
                initializeHeader()

            })
    }

    private fun initializeListeners() {
        super.registerBackPressedCallback(R.id.navigation_home)

        bindingLiveMandiList.etSearchMandi.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) FragmentUtils.hideKeyboard(
                view,
                context
            )
        }
        bindingLiveMandiList.refreshDailyMandiList.setOnRefreshListener { fetchMandiRates() }

        bindingLiveMandiList.etSearchMandi.doAfterTextChanged { filterMandiList() }

    }

    private fun filterMandiList() {
        viewModelRate?.getAdapter()?.filter?.filter(bindingLiveMandiList.etSearchMandi.text.toString())
    }


    private fun openMandiListScreen() {
        findNavController().navigate(
            R.id.navigation_mandi_location, bundleOf(
                NiroAppConstants.PREVIOUS_SCREEN_ID to R.id.navigation_daily_mandi_rates,
                NiroAppConstants.ARG_SELECTED_MANDI to viewModelRate?.getSelectedMandi()?.value
            )
        )
    }

    private fun initializeRecyclerView() {

        bindingLiveMandiList.rvDailyMandiList.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL, false
        )
        val adapter = viewModelRate?.getAdapter()
        bindingLiveMandiList.rvDailyMandiList.setHasFixedSize(true)
        bindingLiveMandiList.rvDailyMandiList.adapter = adapter

    }


    private fun showNoUsers(toShow: Boolean) {
        bindingLiveMandiList.noUsersLayout.tvNoItemMessage.text = String.format(
            getString(R.string.no_rates_available_for_this_location),
            NiroAppUtils.getCurrentUserType(mCurrentUser?.userType)
        )
        if (toShow) {
            bindingLiveMandiList.noUsersLayout.noItemParent.visibility = View.VISIBLE
            bindingLiveMandiList.rvDailyMandiList.visibility = View.GONE
        } else {
            bindingLiveMandiList.noUsersLayout.noItemParent.visibility = View.GONE
            bindingLiveMandiList.rvDailyMandiList.visibility = View.VISIBLE
        }
    }

    private fun handleProgress(progressMessage: String, toShow: Boolean) {

        if (bindingLiveMandiList.refreshDailyMandiList.isRefreshing && !toShow) {
            bindingLiveMandiList.refreshDailyMandiList.isRefreshing = false
        } else if (!bindingLiveMandiList.refreshDailyMandiList.isRefreshing && toShow) {
            mProgressDialog = context?.let { NiroAppUtils.showLoaderProgress(progressMessage, it) }
        } else if (!toShow && mProgressDialog?.isShowing == true) {
            mProgressDialog?.dismiss()
        }
    }

}