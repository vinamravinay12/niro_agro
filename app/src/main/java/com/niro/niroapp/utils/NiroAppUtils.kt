package com.niro.niroapp.utils

import android.content.Context
import android.os.Environment
import android.telephony.PhoneNumberUtils
import android.view.View
import carbon.dialog.ProgressDialog
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.niro.niroapp.R
import com.niro.niroapp.database.SharedPreferenceManager
import com.niro.niroapp.models.responsemodels.User
import com.niro.niroapp.models.responsemodels.UserType
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


object NiroAppUtils {


    @JvmStatic
    fun getPxFromDP(context: Context, dp: Float): Float {
        return dp * context.resources.displayMetrics.density
    }

    @JvmStatic
    fun showSnackbar(message: String, root: View) {

        Snackbar.make(root,message,Snackbar.LENGTH_LONG).show()
    }

    @JvmStatic
    fun showLoaderProgress(message: String, context: Context) : ProgressDialog {
        val progressDialog = ProgressDialog(context)
        progressDialog.setText(message)
        progressDialog.show()
        return progressDialog
    }

    @JvmStatic
    fun getToken(context: Context): String? {
        return "Bearer " + SharedPreferenceManager(
            context,
            NIroAppConstants.LOGIN_SP
        ).getStringPreference(NIroAppConstants.USER_TOKEN)
    }


    @JvmStatic
    fun getDefaultErrorMessage(context: Context?) : String {

        return context?.getString(R.string.something_went_wrong) ?: NIroAppConstants.SWW
    }

    @JvmStatic
    fun getCurrentUser(context: Context) : User {
        val userJson =  SharedPreferenceManager(context,
            NIroAppConstants.LOGIN_SP).getStringPreference(preferenceName = NIroAppConstants.USER_DATA)

       return Gson().fromJson<User>(userJson, User::class.java)
    }

    fun getCurrentUserType(userType: String?): String? {
       return when(userType) {
            UserType.COMMISSION_AGENT.name -> UserType.COMMISSION_AGENT.userType
           UserType.LOADER.name -> UserType.LOADER.userType
           UserType.FARMER.name -> UserType.FARMER.userType
           else -> ""
       }

    }

    fun getUserTypeBasedOnCurrentType(userType: String?,context: Context) : String? {
        return when(userType) {
            UserType.COMMISSION_AGENT.name -> context.getString(R.string.txt_loader)
            else -> context.getString(R.string.txt_buyers)
        }
    }

    fun deleteCountryCode(phone : String) : String {
        val phoneInstance = PhoneNumberUtil.getInstance()

        if(!PhoneNumberUtils.isGlobalPhoneNumber(phone)) return phone

        val phoneNumber = (phoneInstance.parse(phone,"IN"))
        return phoneNumber.nationalNumber.toString()
    }


    @Throws(IOException::class)
    fun createImageFile(filePrefix: String?, context: Context): File? {
        val timeStamp = SimpleDateFormat(
            "yyyyMMdd_HHmmss",
            Locale.getDefault()
        ).format(Date())
        val imageFileName =
            StringBuilder().append(filePrefix).append("_IMG_").append(timeStamp)
                .toString()
        val storageDir =
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(imageFileName, ".jpg", storageDir)
    }

    fun updateCurrentUser(user: User?,context: Context) {
        val sharedPreferenceManager = SharedPreferenceManager(context,NIroAppConstants.LOGIN_SP)
        sharedPreferenceManager.storeComplexObjectPreference(NIroAppConstants.USER_DATA,user)
    }

    fun logout(context: Context) {
        SharedPreferenceManager(context,NIroAppConstants.LOGIN_SP).clearAll()
    }


}