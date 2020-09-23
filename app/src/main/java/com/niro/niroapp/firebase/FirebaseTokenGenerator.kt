package com.niro.niroapp.firebase

import android.content.Context
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.niro.niroapp.database.SharedPreferenceManager
import com.niro.niroapp.utils.NiroAppConstants



interface  FirebaseTokenGeneratedDelegate {
    fun onTokenGenerated(isSuccess : Boolean)
}

class FirebaseTokenGenerator(private val tokenGeneratorListener : FirebaseTokenGeneratedDelegate?)   {


    fun generateIdToken(context : Context) {
        val firebaseUser : FirebaseUser? = FirebaseAuth.getInstance().currentUser
        firebaseUser?.getIdToken(true)?.addOnCompleteListener(OnCompleteListener { task ->
            if(task.isSuccessful) {
                val token = task.result?.token

                if(token.isNullOrEmpty()) return@OnCompleteListener

                SharedPreferenceManager(context,NiroAppConstants.LOGIN_SP).storeStringPreference(NiroAppConstants.USER_TOKEN,token)
                tokenGeneratorListener?.onTokenGenerated(true)
            }
            else {
               tokenGeneratorListener?.onTokenGenerated(false)
            }

        })


    }




}