package com.niro.niroapp.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import carbon.dialog.ProgressDialog
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.google.firebase.auth.FirebaseAuth
import com.niro.niroapp.R
import com.niro.niroapp.activities.MainActivity
import com.niro.niroapp.database.DatabaseKeys
import com.niro.niroapp.database.SharedPreferenceManager
import com.niro.niroapp.databinding.FragmentUserTypeBinding
import com.niro.niroapp.firebase.FirebaseTokenGeneratedDelegate
import com.niro.niroapp.firebase.FirebaseTokenGenerator
import com.niro.niroapp.models.APIError
import com.niro.niroapp.models.APILoader
import com.niro.niroapp.models.Success
import com.niro.niroapp.models.responsemodels.LoginResponse
import com.niro.niroapp.models.responsemodels.UserType
import com.niro.niroapp.utils.NiroAppConstants
import com.niro.niroapp.utils.NiroAppUtils
import com.niro.niroapp.viewmodels.SignupViewModel
import com.niro.niroapp.viewmodels.factories.SignUpViewModelFactory


class UserTypeFragment : AbstractBaseFragment(),FirebaseTokenGeneratedDelegate {

    private lateinit var bindingUserTypeFragment: FragmentUserTypeBinding
    private var signupViewModel : SignupViewModel? = null
    private var progressDialog : ProgressDialog? = null
    private lateinit var mAuth : FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

         bindingUserTypeFragment = DataBindingUtil.inflate(inflater,R.layout.fragment_user_type, container, false)
        bindingUserTypeFragment.lifecycleOwner = viewLifecycleOwner

        mAuth = FirebaseAuth.getInstance()

        return bindingUserTypeFragment.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        signupViewModel = activity?.let { SignUpViewModelFactory("").getViewModel(null,it) }
        selectFarmerType(true)
        initializeListeners()

    }




    private fun initializeListeners() {

        super.registerBackPressedCallback(R.id.mandiListFragment)
        bindingUserTypeFragment.flUserFarmer.setOnClickListener { selectFarmerType(true) }
        bindingUserTypeFragment.flUserCommissionAgent.setOnClickListener { selectAgentType(true) }
        bindingUserTypeFragment.flUserLoader.setOnClickListener { selectLoaderType(true) }

        bindingUserTypeFragment.btnSignup.setOnClickListener { signUp() }
    }

    private fun signUp() {

        signupViewModel?.signUpUser(context)?.observe(viewLifecycleOwner, Observer { response ->

            when(response) {
                is APILoader -> progressDialog = context?.let { NiroAppUtils.showLoaderProgress(getString(R.string.signing_up), it)}

                is APIError -> {
                    if(progressDialog != null && progressDialog?.isShowing == true) progressDialog?.dismiss()
                    NiroAppUtils.showSnackbar(response.errorMessage,root = bindingUserTypeFragment.root)
                }

                is Success<*> -> {
                    if(progressDialog != null && progressDialog?.isShowing == true) signInWithCustomToken(response.data as? LoginResponse)
                }
            }

        })
    }


    private fun signInWithCustomToken(loginResponse: LoginResponse?) {

        loginResponse?.token?.let { activity?.let { activity ->
            mAuth.signInWithCustomToken(it).addOnCompleteListener(
                activity) { task ->
                if (task.isSuccessful) {
                    FirebaseTokenGenerator(this).generateIdToken(activity)
                    saveUserData(loginResponse)
                } else {
                    if(progressDialog != null && progressDialog?.isShowing == true) progressDialog?.dismiss()
                    NiroAppUtils.showSnackbar(getString(R.string.signup_failed),bindingUserTypeFragment.root)
                }
            }

        } }

    }


    fun logSignUpEvent (valToSum : Double) {
        val appEventsLogger = AppEventsLogger.newLogger(requireContext())
        appEventsLogger.logEvent("sign_up",valToSum)
    }

    private fun saveUserData(loginResponse: LoginResponse?) {

        context?.let {
            logSignUpEvent(1.0)
            val sharedPreferenceManager = SharedPreferenceManager(it, NiroAppConstants.LOGIN_SP)
            sharedPreferenceManager.storeComplexObjectPreference(NiroAppConstants.USER_DATA,loginResponse?.data)

        }
    }


    private fun launchMainActivity() {
        startActivity(Intent(activity, MainActivity::class.java))
        activity?.finish()
    }



    private fun selectLoaderType(toSelect : Boolean) {

        bindingUserTypeFragment.flUserLoader.isSelected = toSelect
        bindingUserTypeFragment.ivLoaderSelected.visibility = if(toSelect) View.VISIBLE else View.INVISIBLE

        if(toSelect) {
            selectAgentType(false)
            selectFarmerType(false)
        }

        if(toSelect) signupViewModel?.getUserType()?.value = UserType.LOADER
    }


    private fun selectAgentType(toSelect: Boolean) {

        bindingUserTypeFragment.flUserCommissionAgent.isSelected = toSelect
        bindingUserTypeFragment.ivAgentSelected.visibility = if(toSelect) View.VISIBLE else View.INVISIBLE

        if(toSelect) {
            selectFarmerType(false)
            selectLoaderType(false)
        }

        if(toSelect) signupViewModel?.getUserType()?.value = UserType.COMMISSION_AGENT
    }




    private fun selectFarmerType(toSelect: Boolean) {
        bindingUserTypeFragment.flUserFarmer.isSelected = toSelect
        bindingUserTypeFragment.ivFarmerSelected.visibility = if(toSelect) View.VISIBLE else View.INVISIBLE

        if(toSelect) {
            selectAgentType(false)
            selectLoaderType(false)
        }

        if(toSelect) signupViewModel?.getUserType()?.value = UserType.FARMER
    }

    override fun onTokenGenerated(isSuccess: Boolean) {
        if(progressDialog != null && progressDialog?.isShowing == true) progressDialog?.dismiss()
        if(isSuccess) {
            val sharedPreferenceManager = context?.let { SharedPreferenceManager(it,NiroAppConstants.LOGIN_SP) }
            sharedPreferenceManager?.storeBooleanPreference(DatabaseKeys.KEY_LOGGED_IN,true)
            launchMainActivity()
        }

        else NiroAppUtils.showSnackbar(getString(R.string.signup_failed),bindingUserTypeFragment.root)
    }

}