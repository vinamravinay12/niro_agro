package com.niro.niroapp.utils

import android.content.Context
import android.view.View
import carbon.dialog.ProgressDialog
import com.google.android.material.snackbar.Snackbar


object NiroAppUtils {


    @JvmStatic
    fun getPxFromDP(context: Context, dp: Float): Float {
        return dp * context.resources.displayMetrics.density
    }

    @JvmStatic
    fun showSnackbar(message: String, root: View) {

        Snackbar.make(root,message,Snackbar.LENGTH_LONG).show()
    }

    fun showLoaderProgress(message: String, context: Context) : ProgressDialog {
        val progressDialog = ProgressDialog(context)
        progressDialog.setText(message)
        progressDialog.show()
        return progressDialog
    }


}