package com.niro.niroapp.users.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.niro.niroapp.R
import com.niro.niroapp.utils.NiroAppConstants

class CreateNewUserOptionDialog : DialogFragment() {

    private lateinit var btnCreateFromContact : CardView
    private lateinit var btnEnterManually : CardView
    private lateinit var mNavigationController : NavController
    private var mPreviousScreenId : Int? = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {bundle ->
         mPreviousScreenId =   bundle.getInt(NiroAppConstants.PREVIOUS_SCREEN_ID, -1)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(context)

        val view = LayoutInflater.from(context).inflate(R.layout.dialog_select_contacts_or_manual_enter,null, false)

        btnCreateFromContact = view.findViewById(R.id.layoutSelectContact)
        btnEnterManually = view.findViewById(R.id.layoutEnterManually)

        mNavigationController = findNavController()
        builder.setView(view)
        val dialog = builder.create()
       // dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        return dialog
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initializeListeners()
    }

    private fun initializeListeners() {
        btnCreateFromContact.setOnClickListener { launchContactsFragment() }
        btnEnterManually.setOnClickListener { launchCreateUserManuallyFragment() }
    }

    private fun launchContactsFragment() {
        mNavigationController.navigate(R.id.action_dialog_select_users_to_navigation_contacts, bundleOf(NiroAppConstants.PREVIOUS_SCREEN_ID to mPreviousScreenId))
        dismiss()
    }

    private fun launchCreateUserManuallyFragment() {
        mNavigationController.navigate(R.id.action_dialog_select_users_to_navigation_create_users_manually,bundleOf(NiroAppConstants.PREVIOUS_SCREEN_ID to mPreviousScreenId))
        dismiss()
    }
}