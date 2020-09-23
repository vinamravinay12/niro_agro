package com.niro.niroapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.niro.niroapp.R
import com.niro.niroapp.databinding.LayoutEnterNameBinding
import com.niro.niroapp.viewmodels.SignupViewModel
import com.niro.niroapp.viewmodels.factories.SignUpViewModelFactory

const val ARG_MOBILE_NUMBER = "ArgMobileNumber"


class EnterNameFragment : AbstractBaseFragment() {

    private var mobileNumber: String? = null
    private lateinit var bindingEnterNameFragment: LayoutEnterNameBinding
    private var signUpViewModel: SignupViewModel? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mobileNumber = it.getString(ARG_MOBILE_NUMBER)

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        bindingEnterNameFragment =
            DataBindingUtil.inflate(inflater, R.layout.layout_enter_name, container, false)
        signUpViewModel = activity?.let {
            SignUpViewModelFactory(phoneNumber = mobileNumber).getViewModel(mobileNumber, it)
        }

        bindingEnterNameFragment.signUpVM = signUpViewModel
        bindingEnterNameFragment.lifecycleOwner = viewLifecycleOwner
        return bindingEnterNameFragment.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeListeners()
    }

    private fun initializeListeners() {

        super.registerBackPressedCallback(R.id.loginFragment)

        bindingEnterNameFragment.btnNext.setOnClickListener { showEnterBusinessNameScreen() }
    }

    private fun showEnterBusinessNameScreen() {
       findNavController().navigate(R.id.action_enterNameFragment_to_enterBusinessFragment)
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            EnterNameFragment()
    }
}