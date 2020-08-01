package com.niro.niroapp.fragments

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.niro.niroapp.R
import com.niro.niroapp.databinding.FragmentSupportBinding
import com.niro.niroapp.utils.NiroAppUtils


class SupportFragment : Fragment() {

    private lateinit var bindingSupportFragment : FragmentSupportBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        bindingSupportFragment = DataBindingUtil.inflate(inflater,R.layout.fragment_support, container, false)
        return bindingSupportFragment.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initializeListeners()
    }

    private fun initializeListeners() {
        bindingSupportFragment.btnCallUs.setOnClickListener { callUs() }
        bindingSupportFragment.btnContactWhatsApp.setOnClickListener { contactUsOnWhatApp() }
    }

    private fun contactUsOnWhatApp() {

        val contact = "+91 6361092057" // use country code with your phone number

        val url = "https://api.whatsapp.com/send?phone=$contact"
        try {
            val packageManager = context?.packageManager
            packageManager?.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES)
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        } catch (e: PackageManager.NameNotFoundException) {
            NiroAppUtils.showSnackbar(getString(R.string.whatsapp_not_installed),bindingSupportFragment.root)

        }
    }

    private fun callUs() {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:6361092057")
        startActivity(intent)
    }

}