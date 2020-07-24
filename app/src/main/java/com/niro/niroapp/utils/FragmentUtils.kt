package com.waitty.kitchen.fragment

import android.content.Context
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction



object FragmentUtils {

    fun  launchFragment(fragmentManager: FragmentManager?, view : Int, fragment: Fragment?, tag: String?) {
        if (fragmentManager == null || fragmentManager.fragments == null) {
            return
        }
        val fragmentTransaction = if (fragmentManager.fragments.size == 0) addFragment(fragmentManager,view, fragment, tag) else replaceFragment(fragmentManager, view, fragment, tag)
        fragmentTransaction?.commit()
    }

    private fun addFragment(fragmentManager: FragmentManager, view : Int, fragment: Fragment?, tag: String?): FragmentTransaction? {
        return fragment?.let { fragmentManager.beginTransaction().add(view, it, tag) }
    }



    private fun replaceFragment(fragmentManager: FragmentManager, view : Int, fragment: Fragment?, tag: String?): FragmentTransaction? {
        return fragment?.let { fragmentManager.beginTransaction().replace(view, it, tag).addToBackStack(null) }
    }

    fun goBackToPreviousScreen(activity: FragmentActivity?, previousFragmentTag: String?, currentFragmentTag: String?) {
        if (activity == null) return
        activity.supportFragmentManager.popBackStack()
    }


    fun setBindingVariables(variablesMap : HashMap<Int,Any>, viewBinding : ViewDataBinding) {
        for(key in variablesMap.keys) {
            viewBinding.setVariable(key,variablesMap[key])
        }
    }

    fun closeErrorScreen( hideView : View, showView : View) {

        hideView.visibility = View.GONE
        showView.visibility = View.VISIBLE

    }

//    fun showProgress(parentView: View,toShow: Boolean,progressMessage: String?, apiErrorViewModel: ApiErrorViewModel, isSwipeRefreshed : Boolean = false) {
//        parentView.visibility = if(toShow) View.GONE else View.VISIBLE
//        apiErrorViewModel.getProgressVisibility().value = toShow && !isSwipeRefreshed
//        apiErrorViewModel.getProgressMessage().value = progressMessage
//    }

//    fun showError(parentView: View, apiErrorViewModel: ApiErrorViewModel, errorCode : Int?, errorMessage: String?, isSwipeRefreshed: Boolean = false) {
//        apiErrorViewModel.getErrorMessage().value = errorMessage
//        errorCode?.let { errorCode -> apiErrorViewModel.setErrorType(errorCode) }
//        apiErrorViewModel.showError()
//        showProgress(parentView = parentView,toShow = false, progressMessage = null, apiErrorViewModel = apiErrorViewModel, isSwipeRefreshed = isSwipeRefreshed)
//        parentView.visibility = if(!TextUtils.isEmpty(errorMessage)) View.GONE else View.VISIBLE
//    }
//
//    fun showProgress(toShow: Boolean,progressMessage: String?, apiErrorViewModel: ApiErrorViewModel, isSwipeRefreshed : Boolean = false) {
//
//        apiErrorViewModel.getProgressVisibility().value = toShow && !isSwipeRefreshed
//        apiErrorViewModel.getProgressMessage().value = progressMessage
//    }
//
//    fun showError(apiErrorViewModel: ApiErrorViewModel,errorCode : Int?, errorMessage: String?, isSwipeRefreshed: Boolean = false) {
//        apiErrorViewModel.getErrorMessage().value = errorMessage
//        errorCode?.let { errorCode -> apiErrorViewModel.setErrorType(errorCode) }
//        apiErrorViewModel.showError()
//        showProgress(toShow = false, progressMessage = null, apiErrorViewModel = apiErrorViewModel, isSwipeRefreshed = isSwipeRefreshed)
//
//    }

    fun hideKeyboard(view: View,context: Context?) : Boolean {
        val inputMethodManager :  InputMethodManager? = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        inputMethodManager?.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        return true
    }



    fun showNoListView(toShow: Boolean, noListView : View, hideView : View) {
        noListView.visibility = getVisibility(toShow)
        hideView.visibility = getVisibility(!toShow)
    }

    private fun getVisibility(toShow : Boolean)  : Int {
        return if(toShow) View.VISIBLE else View.GONE
    }

//    fun hideBottomNavigationView(activity: FragmentActivity?, toHide : Boolean) {
//        (activity as? HomeActivity)?.hideBottomNavigationMenu(toHide)
//    }

//    fun setTitle(activity : FragmentActivity?,title : Int) {
//        (activity as? HomeActivity)?.setTitle(title)
//    }
}