package com.niro.niroapp.database

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.GsonBuilder

class SharedPreferenceManager constructor(private val context: Context, private  val name: String) {
    private var mPrefs: SharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE)
    private var aesCrypt: AESCrypt = AESCrypt()
    private var mContext: Context = context
    private var fileName: String = name


    private fun encryptString(value: String): String? {
        val encryptedMsg: String? = aesCrypt.encrypt(value)
        return encryptedMsg
    }

    private fun decryptString(encryptedMsg: String?): String? {
        return aesCrypt.decrypt(encryptedMsg)
    }

    fun storeStringPreference(preferenceName: String?, value: String) {
        val encryptedMsg = encryptString(value)
        val prefsEditor = mPrefs.edit()
        prefsEditor.putString(preferenceName, encryptedMsg)
        prefsEditor.commit()
    }


    fun storeObjectPreference(preferenceName: String?, value: Any?) {
        val gson = Gson()
        val json = gson.toJson(value)
        storeStringPreference(preferenceName, json)
    }

    fun storeComplexObjectPreference(preferenceName: String?, value: Any?) {
        val gson = GsonBuilder().enableComplexMapKeySerialization().create()
        val json = gson.toJson(value)
        storeStringPreference(preferenceName, json)
    }

    fun storeBooleanPreference(preferenceName: String?, value: Boolean) {
        val encryptedMsg = encryptString(value.toString())
        val prefsEditor = mPrefs.edit()
        prefsEditor.putString(preferenceName, encryptedMsg)
        prefsEditor.apply()
    }

    fun getBooleanPreference(preferenceName: String?, defaultValue: Boolean): Boolean {
        val defaultEncryptedValue = encryptString(defaultValue.toString())


        val prefValue = mPrefs.getString(preferenceName, defaultEncryptedValue)
        val value = decryptString(prefValue)
        return if (value != null && !value.isEmpty()) java.lang.Boolean.parseBoolean(value) else java.lang.Boolean.parseBoolean(value)
    }

    fun getStringPreference(preferenceName: String?): String? {
        val prefValue = mPrefs.getString(preferenceName, "")
        return decryptString(prefValue)
    }


    fun getStringPreference(preferenceName: String?, defaultValue: String): String? {
        val defaultEncryptedValue = encryptString(defaultValue)
        val prefValue = mPrefs.getString(preferenceName, defaultEncryptedValue)
        return decryptString(prefValue)
    }


    fun storeIntegerPreference(preferenceName: String?, value: Int) {
        val encryptedMsg = encryptString(value.toString())
        val prefsEditor = mPrefs.edit()
        prefsEditor.putString(preferenceName, encryptedMsg)
        prefsEditor.commit()
    }

    fun getLongPreference(preferenceName: String?, defaultValue: Long): Long {
        val defaultEncryptedValue = encryptString(defaultValue.toString())
        val prefValue = mPrefs.getString(preferenceName, defaultEncryptedValue)
        val value = decryptString(prefValue)
        return if (value != null && !value.isEmpty()) value.toLong() else prefValue!!.toLong()
    }


    fun storeLongPreference(preferenceName: String?, value: Long) {
        val encryptedMsg = encryptString(value.toString())
        val prefsEditor = mPrefs.edit()
        prefsEditor.putString(preferenceName, encryptedMsg)
        prefsEditor.commit()
    }

    fun getIntegerPreference(preferenceName: String?, defaultValue: Int): Int {
        val defaultEncryptedValue = encryptString(defaultValue.toString())
        val prefValue = mPrefs.getString(preferenceName, defaultEncryptedValue)
        val value = decryptString(prefValue)
        return if (value != null && value.isNotEmpty()) value.toInt() else prefValue?.toInt() ?: 0
    }


    fun clearSharedPreference(preferenceName: String?) {
        mPrefs!!.edit().remove(preferenceName).commit()
    }

    fun clearAll() {
        val editor = mPrefs.edit()
        editor.clear()
        editor.apply()
        editor.commit()
    }

    fun removeKey(key: String?) {
        val editor = mPrefs.edit()
        editor.remove(key)
        editor.commit()
    }


    fun getAllKeys(): Map<String?, *>? {
        return mPrefs.all
    }

}