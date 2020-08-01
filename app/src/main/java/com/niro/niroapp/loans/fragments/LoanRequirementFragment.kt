package com.niro.niroapp.loans.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import carbon.dialog.ProgressDialog
import com.niro.niroapp.R
import com.niro.niroapp.databinding.LoanRequirementFragmentBinding
import com.niro.niroapp.loans.viewmodels.LoanRequirementViewModel
import com.niro.niroapp.loans.viewmodels.factories.LoanRequirementViewModelFactory
import com.niro.niroapp.models.APIError
import com.niro.niroapp.models.APILoader
import com.niro.niroapp.models.Success
import com.niro.niroapp.models.responsemodels.User
import com.niro.niroapp.utils.NIroAppConstants
import com.niro.niroapp.utils.NiroAppUtils


class LoanRequirementFragment : Fragment() {

    private var viewModel: LoanRequirementViewModel? = null
    private lateinit var bindingLoanRequirementFragment: LoanRequirementFragmentBinding
    private var mProgressDialog : ProgressDialog?= null
    private var mCurrentUser : User? = null

    companion object {
        fun newInstance() =
            LoanRequirementFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mCurrentUser = it.getParcelable(NIroAppConstants.ARG_USER_CONTACT)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingLoanRequirementFragment = DataBindingUtil.inflate(inflater,
            R.layout.loan_requirement_fragment, container, false)
        bindingLoanRequirementFragment.lifecycleOwner = viewLifecycleOwner
        return bindingLoanRequirementFragment.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = activity?.let { LoanRequirementViewModelFactory(mCurrentUser).getViewModel(mCurrentUser, it) }

        bindingLoanRequirementFragment.loanRequestVM = viewModel

        initializeListeners()

    }

    private fun initializeListeners() {
        bindingLoanRequirementFragment.btnSubmitDetails.setOnClickListener { submitLoanRequirement() }
    }

    private fun submitLoanRequirement() {
        viewModel?.createLoanRequirement(context)?.observe(viewLifecycleOwner, Observer { response ->

            when (response) {
                is APILoader -> mProgressDialog = context?.let {
                    NiroAppUtils.showLoaderProgress(getString(R.string.submitting_loan_details), it)
                }

                is APIError -> {
                    mProgressDialog?.dismiss()
                    NiroAppUtils.showSnackbar(response.errorMessage, bindingLoanRequirementFragment.root)
                }

                is Success<*> -> {
                    mProgressDialog?.dismiss()
                    showSuccessDialog()
                    viewModel?.resetAllFields()

                }
            }
        })
    }

    private fun showSuccessDialog() {
        
    }




}