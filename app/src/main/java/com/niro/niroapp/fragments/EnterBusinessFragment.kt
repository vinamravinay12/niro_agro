package com.niro.niroapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.niro.niroapp.R
import com.niro.niroapp.activities.LoginActivity
import com.niro.niroapp.databinding.LayoutEnterBusinessBinding
import com.niro.niroapp.databinding.LayoutEnterNameBinding
import com.niro.niroapp.utils.FragmentUtils
import com.niro.niroapp.utils.NIroAppConstants
import com.niro.niroapp.utils.NiroAppUtils
import com.niro.niroapp.viewmodels.SignupViewModel
import com.niro.niroapp.viewmodels.factories.SignUpViewModelFactory
import kotlinx.android.synthetic.main.layout_enter_business.*

class EnterBusinessFragment : Fragment() {

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
        (activity as? LoginActivity)?.hideChildFrameLayout(false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindingEnterBusinessFragment.btnNext.setOnClickListener {
            if(signUpViewModel?.validateBusinessName() != true) NiroAppUtils.showSnackbar(getString(R.string.business_name_missing),bindingEnterBusinessFragment.root)

            else  launchSelectCommoditiesFragment()

        }
    }

    private fun launchSelectCommoditiesFragment() {
        (activity as? LoginActivity)?.hideChildFrameLayout(true)
        FragmentUtils.launchFragment(activity?.supportFragmentManager,view = R.id.fl_login_parent,
            fragment = CommoditiesFragment.newInstance(),tag = NIroAppConstants.TAG_COMMODITIES)
    }

}