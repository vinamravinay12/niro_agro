package com.niro.niroapp.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import com.niro.niroapp.R
import com.niro.niroapp.activities.LoginActivity
import com.niro.niroapp.databinding.DialogLogoutBinding
import com.niro.niroapp.utils.NiroAppUtils

class LogoutDialog : DialogFragment() {

    private lateinit var viewDataBinding : DialogLogoutBinding


    override fun onCreateDialog(savedInstanceState: Bundle?) : Dialog {
        super.onCreate(savedInstanceState)
        val builder = AlertDialog.Builder(context)
        viewDataBinding  = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_logout,null,false)

        builder.setView(viewDataBinding.root)

        viewDataBinding.btnCancel.setOnClickListener { dismiss() }
        viewDataBinding.btnLogout.setOnClickListener { context?.let { context ->  NiroAppUtils.logout(context)
            startActivity(Intent(activity, LoginActivity::class.java))
            activity?.finish()
        } }
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

       return dialog
    }
}