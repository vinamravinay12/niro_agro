package com.niro.niroapp.loans.fragments


import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import carbon.dialog.ProgressDialog
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.niro.niroapp.R
import com.niro.niroapp.databinding.LoanRequirementFragmentBinding
import com.niro.niroapp.fragments.AbstractBaseFragment
import com.niro.niroapp.fragments.SuccessDialog
import com.niro.niroapp.loans.viewmodels.LoanRequirementViewModel
import com.niro.niroapp.loans.viewmodels.factories.LoanRequirementViewModelFactory
import com.niro.niroapp.models.APIError
import com.niro.niroapp.models.APILoader
import com.niro.niroapp.models.Success
import com.niro.niroapp.models.responsemodels.User
import com.niro.niroapp.utils.NiroAppConstants
import com.niro.niroapp.utils.NiroAppUtils
import com.niro.niroapp.utils.OnBackPressedListener


class LoanRequirementFragment : AbstractBaseFragment() {

    private var viewModel: LoanRequirementViewModel? = null
    private lateinit var bindingLoanRequirementFragment: LoanRequirementFragmentBinding
    private var mProgressDialog : ProgressDialog?= null
    private var mCurrentUser : User? = null
    private var mDialog : Dialog? = null

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    companion object {
        fun newInstance() =
            LoanRequirementFragment()
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
        bindingLoanRequirementFragment = DataBindingUtil.inflate(inflater,
            R.layout.loan_requirement_fragment, container, false)
        bindingLoanRequirementFragment.lifecycleOwner = viewLifecycleOwner

        firebaseAnalytics.setCurrentScreen(requireActivity(),getString(R.string.title_loans),null)
        return bindingLoanRequirementFragment.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = activity?.let { LoanRequirementViewModelFactory(mCurrentUser).getViewModel(mCurrentUser, it) }

        bindingLoanRequirementFragment.loanRequestVM = viewModel

        super.setPageTitle(getString(R.string.title_loans),R.drawable.ic_loans_24)
        initializeListeners()

    }

    private fun initializeListeners() {
        super.registerBackPressedCallback(R.id.navigation_home)
        bindingLoanRequirementFragment.btnSubmitDetails.setOnClickListener { submitLoanRequirement() }
    }

    private fun submitLoanRequirement() {
        viewModel?.createLoanRequirement(context)?.observe(viewLifecycleOwner, Observer { response ->

            when (response) {
                is APILoader -> mProgressDialog = context?.let {
                    NiroAppUtils.showLoaderProgress(getString(R.string.submitting_loan_details), it)
                }

                is APIError -> {
                    if(mProgressDialog != null && mProgressDialog?.isShowing == true) mProgressDialog?.dismiss()
                    NiroAppUtils.showSnackbar(response.errorMessage, bindingLoanRequirementFragment.root)
                }

                is Success<*> -> {
                    if(mProgressDialog != null && mProgressDialog?.isShowing == true) mProgressDialog?.dismiss()
                    showSuccessDialog()
                    viewModel?.resetAllFields()

                }
            }
        })
    }

    private fun showSuccessDialog() {
        activity?.supportFragmentManager?.let { SuccessDialog().show(it,NiroAppConstants.TAG_DIALOG_SUCCESS) }
    }



}