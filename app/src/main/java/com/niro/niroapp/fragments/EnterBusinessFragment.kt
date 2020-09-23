package com.niro.niroapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.niro.niroapp.R
import com.niro.niroapp.databinding.LayoutEnterBusinessBinding
import com.niro.niroapp.utils.NiroAppUtils
import com.niro.niroapp.viewmodels.SignupViewModel
import com.niro.niroapp.viewmodels.factories.SignUpViewModelFactory

class EnterBusinessFragment : AbstractBaseFragment() {

    private lateinit var bindingEnterBusinessFragment: LayoutEnterBusinessBinding
    private var signUpViewModel : SignupViewModel? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        bindingEnterBusinessFragment = DataBindingUtil.inflate(inflater,R.layout.layout_enter_business  , container, false)
        signUpViewModel = activity?.let { SignUpViewModelFactory("").getViewModel(null,it) }

        bindingEnterBusinessFragment.signUpVM = signUpViewModel
        bindingEnterBusinessFragment.lifecycleOwner = viewLifecycleOwner


        return bindingEnterBusinessFragment.root
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        super.registerBackPressedCallback(R.id.enterNameFragment)
        bindingEnterBusinessFragment.btnNext.setOnClickListener {
            if(signUpViewModel?.validateBusinessName() != true) NiroAppUtils.showSnackbar(getString(R.string.business_name_missing),bindingEnterBusinessFragment.root)

            else  launchSelectCommoditiesFragment()

        }
    }

    private fun launchSelectCommoditiesFragment() {
       findNavController().navigate(R.id.action_enterBusinessFragment_to_commoditiesFragment)

    }

}