package com.niro.niroapp.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import com.niro.niroapp.R

class SuccessDialog() :  DialogFragment() {

    private lateinit var viewDataBinding : ViewDataBinding


    val handler: Handler = Handler()
    val runnable = Runnable {
        if (dialog?.isShowing == true) {
            dismiss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?) : Dialog{
        super.onCreate(savedInstanceState)
        val builder = AlertDialog.Builder(context)
        viewDataBinding  = DataBindingUtil.inflate(LayoutInflater.from(context),
            R.layout.dialog_loan_required_success,null,false)

        builder.setView(viewDataBinding.root)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        handler.postDelayed(runnable, 3000);
        return dialog
    }






}