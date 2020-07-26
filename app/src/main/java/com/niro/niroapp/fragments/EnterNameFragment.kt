package com.niro.niroapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.niro.niroapp.R


private const val ARG_MOBILE_NUMBER = "ArgMobileNumber"


class EnterNameFragment : Fragment() {

        private var mobileNumber: String? = null


        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            arguments?.let {
                mobileNumber = it.getString(ARG_MOBILE_NUMBER)

            }
        }

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            // Inflate the layout for this fragment
            return inflater.inflate(R.layout.layout_enter_name  , container, false)
        }

        companion object {

            @JvmStatic
            fun newInstance(mobileNumber: String) =
                OTPFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_MOBILE_NUMBER, mobileNumber)
                    }
                }
        }
}