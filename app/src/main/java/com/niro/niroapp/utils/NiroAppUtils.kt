package com.niro.niroapp.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.telephony.PhoneNumberUtils
import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import carbon.dialog.ProgressDialog
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.niro.niroapp.R
import com.niro.niroapp.database.SharedPreferenceManager
import com.niro.niroapp.models.responsemodels.User
import com.niro.niroapp.models.responsemodels.UserType
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.Duration
import java.util.*


object NiroAppUtils {


    @JvmStatic
    fun getPxFromDP(context: Context, dp: Float): Float {
        return dp * context.resources.displayMetrics.density
    }

    @JvmStatic
    fun showSnackbar(message: String, root: View) {

        Snackbar.make(root, message, Snackbar.LENGTH_SHORT).show()
    }

    @JvmStatic
    fun showLoaderProgress(message: String, context: Context): ProgressDialog {
        val progressDialog = ProgressDialog(context)
        progressDialog.setText(message)
        progressDialog.show()
        return progressDialog
    }

    @JvmStatic
    fun getToken(context: Context): String? {
        return "Bearer " + SharedPreferenceManager(
            context,
            NiroAppConstants.LOGIN_SP
        ).getStringPreference(NiroAppConstants.USER_TOKEN)
    }


    @JvmStatic
    fun getDefaultErrorMessage(context: Context?): String {

        return context?.getString(R.string.something_went_wrong) ?: NiroAppConstants.SWW
    }

    @JvmStatic
    fun getCurrentUser(context: Context): User {
        val userJson = SharedPreferenceManager(
            context,
            NiroAppConstants.LOGIN_SP
        ).getStringPreference(preferenceName = NiroAppConstants.USER_DATA)

        return Gson().fromJson<User>(userJson, User::class.java)
    }

    fun getCurrentUserType(userType: String?): String? {
        return when (userType) {
            UserType.COMMISSION_AGENT.name -> "Loader"
            UserType.LOADER.name -> UserType.LOADER.userType
            UserType.FARMER.name -> UserType.FARMER.userType
            else -> ""
        }

    }

    fun getUserTypeStringBasedOnCurrentUserType(context: Context?, userType: String?): String? {
        return when (userType) {
            UserType.COMMISSION_AGENT.name -> context?.getString(R.string.txt_loader)
            else -> context?.getString(R.string.txt_buyers)
        }
    }


    fun getCurrentUserId(context: Context): String? {
        val userJson = SharedPreferenceManager(
            context,
            NiroAppConstants.LOGIN_SP
        ).getStringPreference(preferenceName = NiroAppConstants.USER_DATA)

        val user = Gson().fromJson<User>(userJson, User::class.java)
        return user.id
    }

    fun getUserTypeBasedOnCurrentType(userType: String?, context: Context): String? {
        return when (userType) {
            UserType.COMMISSION_AGENT.name -> context.getString(R.string.txt_loader)
            else -> context.getString(R.string.txt_buyers)
        }
    }

    fun deleteCountryCode(phone: String): String {
        val phoneInstance = PhoneNumberUtil.getInstance()

        if (!PhoneNumberUtils.isGlobalPhoneNumber(phone)) return phone

        val phoneNumber = (phoneInstance.parse(phone, "IN"))
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

    fun updateCurrentUser(user: User?, context: Context) {
        val sharedPreferenceManager = SharedPreferenceManager(context, NiroAppConstants.LOGIN_SP)
        sharedPreferenceManager.storeComplexObjectPreference(NiroAppConstants.USER_DATA, user)
    }

    fun logout(context: Context) {
        FirebaseAuth.getInstance().signOut()
        SharedPreferenceManager(context, NiroAppConstants.LOGIN_SP).clearAll()
    }

    fun hideKeyBoard(view: View, context: Context?): Boolean {
        return FragmentUtils.hideKeyboard(view, context)
    }


    fun callUser(context: Context, number: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$number")
        context.startActivity(intent)
    }

    fun openFacebookGroup(context: Context) {
        val fbGroupUrl = "https://www.facebook.com/groups/257380321998250"
        try {
            val applicationInfo =
                context.packageManager.getApplicationInfo("com.facebook.katana", 0)

            var versionCode: Long = 0
            if (applicationInfo.enabled) {
                versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    context.packageManager.getPackageInfo("com.facebook.katana", 0).longVersionCode
                } else context.packageManager.getPackageInfo(
                    "com.facebook.katana",
                    0
                ).versionCode.toLong()

                var url = ""
                url =
                    if (versionCode >= 3002850) "fb://facewebmodal/f?href=$fbGroupUrl" else "fb://groups/257380321998250"
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)
                context.startActivity(intent)
            }

        } catch (exception: Exception) {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(fbGroupUrl)))
        }
    }


    fun roundToDecimalPlaces(num: Double): Double {
        val df = DecimalFormat("#.###")
        df.roundingMode = RoundingMode.CEILING

        return df.format(num).toDouble()
    }


    fun setBackPressedCallback(activity:FragmentActivity,viewLifecycleOwner : LifecycleOwner,onBackPressedListener: OnBackPressedListener ) {
        activity.onBackPressedDispatcher.addCallback(viewLifecycleOwner,NavigationBackPressedCallback(onBackPressedListener))
    }


    fun showToast(message:String,context: Context,duration: Int) {
        Toast.makeText(context,message,duration).show()
    }



}