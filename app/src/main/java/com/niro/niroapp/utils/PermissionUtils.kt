package com.niro.niroapp.utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

object PermissionUtils {


    fun isReadContactsPermissionGranted(context: Context?): Boolean {
        return context?.let { ContextCompat.checkSelfPermission(it, NiroAppConstants.CODE_READ_CONTACTS) } == PackageManager.PERMISSION_GRANTED
    }


    fun isCameraPermissionGranted(context: Context?): Boolean {
        return context?.let { ContextCompat.checkSelfPermission(it, NiroAppConstants.CODE_CAMERA_PERMISSION) } == PackageManager.PERMISSION_GRANTED
    }


    fun isStoragePermissionGranted(context: Context?): Boolean {
        return context?.let { ContextCompat.checkSelfPermission(it, NiroAppConstants.CODE_STORAGE_PERMISSION) } == PackageManager.PERMISSION_GRANTED
    }




    fun askForPermissions(fragmentActivity: FragmentActivity?, permissionsArray: Array<String?>?) {
        fragmentActivity?.let {
            if (permissionsArray != null) {
                ActivityCompat.requestPermissions(it, permissionsArray, NiroAppConstants.KEY_MULITPLE_PERMISSIONS)
            }
        }
    }
}