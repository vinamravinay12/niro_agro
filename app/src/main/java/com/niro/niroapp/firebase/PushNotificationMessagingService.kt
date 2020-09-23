package com.niro.niroapp.firebase

import android.text.TextUtils
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.niro.niroapp.database.SharedPreferenceManager
import com.niro.niroapp.utils.NiroAppConstants
import java.util.*

class PushNotificationMessagingService : FirebaseMessagingService() {

    override fun onCreate() {
        super.onCreate()
        FirebaseInstanceId.getInstance().instanceId
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.w("TAG", "getInstanceId failed", task.exception)
                        return@OnCompleteListener
                    }
                    storeNewToken(task.result!!.token)
                })
    }


    private fun storeNewToken(token: String) {
        val sharedPreferenceManager = SharedPreferenceManager(context = this,name = NiroAppConstants.NOTIFICATION_SP)
        if(TextUtils.isEmpty(sharedPreferenceManager.getStringPreference(NiroAppConstants.USER_FCMTOKENID))) {
            sharedPreferenceManager.storeStringPreference(NiroAppConstants.USER_FCMTOKENID, token)
        }

        val uniqueId = UUID.randomUUID().toString()
        sharedPreferenceManager.storeStringPreference(NiroAppConstants.USER_DEVICEID,uniqueId)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

    }
    // Notification handle received from server


}