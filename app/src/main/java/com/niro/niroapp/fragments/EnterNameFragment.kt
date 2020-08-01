package com.niro.niroapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.niro.niroapp.R
import com.niro.niroapp.databinding.LayoutEnterNameBinding
import com.niro.niroapp.utils.FragmentUtils
import com.niro.niroapp.utils.NIroAppConstants
import com.niro.niroapp.viewmodels.SignupViewModel
import com.niro.niroapp.viewmodels.factories.SignUpViewModelFactory
import kotlin.math.sign


private const val ARG_MOBILE_NUMBER = "ArgMobileNumber"


class EnterNameFragment : Fragment() {

        private var mobileNumber: String? = null
        private lateinit var bindingEnterNameFragment : LayoutEnterNameBinding
        private var signUpViewModel : SignupViewModel? = null


        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            arguments?.let {
                mobileNumber = it.getString(ARG_MOBILE_NUMBER)

            }
        }

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

            bindingEnterNameFragment =  DataBindingUtil.inflate(inflater,R.layout.layout_enter_name  , container, false)
            signUpViewModel = activity?.let {
                SignUpViewModelFactory(phoneNumber = mobileNumber).getViewModel(mobileNumber,it)
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

        bindingEnterNameFragment.btnNext.setOnClickListener { showEnterBusinessNameScreen() }
    }

    private fun showEnterBusinessNameScreen() {
        FragmentUtils.launchFragment(activity?.supportFragmentManager,fragment = EnterBusinessFragment(),
            view = R.id.fl_login,tag = NIroAppConstants.TAG_ENTER_BUSINESS)
    }

    companion object {

            @JvmStatic
            fun newInstance(mobileNumber: String) =
                EnterNameFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_MOBILE_NUMBER, mobileNumber)
                    }
                }
        }
}