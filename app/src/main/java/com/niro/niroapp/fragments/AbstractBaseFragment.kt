package com.niro.niroapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.niro.niroapp.activities.MainActivity
import com.niro.niroapp.utils.NiroAppUtils
import com.niro.niroapp.utils.OnBackPressedListener

abstract class AbstractBaseFragment : Fragment(), OnBackPressedListener {

    private var mPreviousScreenId : Int = -1
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


    }

    fun registerBackPressedCallback(previousScreenId : Int = -1) {
        mPreviousScreenId = previousScreenId
        NiroAppUtils.setBackPressedCallback(requireActivity(), viewLifecycleOwner, this)
    }

    override fun onBackPressed() {
        if(mPreviousScreenId == -1) findNavController().popBackStack()
        else findNavController().popBackStack(mPreviousScreenId,false)
    }


    fun setPageTitle(title : String, icon : Int) {
        if(activity is MainActivity)(activity as? MainActivity)?.setToolbarTitleAndImage(title,icon)
    }
}