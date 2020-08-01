package com.niro.niroapp.firebase

import android.content.Context
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.niro.niroapp.database.SharedPreferenceManager
import com.niro.niroapp.utils.NIroAppConstants



interface  FirebaseTokenGeneratorListener {
    fun onTokenGenerated(isSuccess : Boolean)
}

class FirebaseTokenGenerator(private val tokenGeneratorListener : FirebaseTokenGeneratorListener?)   {


    fun generateIdToken(context : Context) {
        val firebaseUser : FirebaseUser? = FirebaseAuth.getInstance().currentUser
        firebaseUser?.getIdToken(true)?.addOnCompleteListener(OnCompleteListener { task ->
            if(task.isSuccessful) {
                val token = task.result?.token

                if(token.isNullOrEmpty()) return@OnCompleteListener

                SharedPreferenceManager(context,NIroAppConstants.LOGIN_SP).storeStringPreference(NIroAppConstants.USER_TOKEN,token)
                tokenGeneratorListener?.onTokenGenerated(true)
            }
            else {
               tokenGeneratorListener?.onTokenGenerated(false)
            }

        })


    }




}