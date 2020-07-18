package com.translabtechnologies.visitormanagementsystem.vmshost.database

import android.util.Base64
import com.niro.niroapp.database.DatabaseKeys
import java.security.NoSuchAlgorithmException
import javax.crypto.Cipher
import javax.crypto.NoSuchPaddingException
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


class AESCrypt() {
    private val iv: String = DatabaseKeys.AES_IV //  (CHANGE IT!) 16 char

    private var ivspec: IvParameterSpec? = null
    private var keyspec: SecretKeySpec? = null
    private var cipher: Cipher? = null
    private val SecretKey : String = DatabaseKeys.AES_SecretKey //  (CHANGE IT!) 32 char

    private val TAG = "AESCrypt"
    private val AESALGORITHM = "AES/CBC/PKCS7Padding"

    init {
        ivspec = IvParameterSpec(iv.toByteArray())
        keyspec = SecretKeySpec(SecretKey.toByteArray(), AESALGORITHM)
        try {
            cipher = Cipher.getInstance(AESALGORITHM)
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: NoSuchPaddingException) {
            e.printStackTrace()
        }
    }


    fun encrypt(value: String?): String? {
        var encryptedMsg: String? = ""
        if (value == null || value.isEmpty()) return ""
        var encrypted: ByteArray? = null
        try {
            cipher?.init(Cipher.ENCRYPT_MODE, keyspec, ivspec)
            encrypted = cipher?.doFinal(value.toByteArray(charset("utf-8")))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        encryptedMsg = Base64.encodeToString(encrypted, Base64.DEFAULT)
        return encryptedMsg
    }

    fun decrypt(encryptedMsg: String?): String? {
        var messageAfterDecrypt: String? = ""
        if (encryptedMsg == null || encryptedMsg.isEmpty()) return ""
        var decrypted: ByteArray? = null
        try {
            cipher?.init(Cipher.DECRYPT_MODE, keyspec, ivspec)
            decrypted = cipher?.doFinal(Base64.decode(encryptedMsg.toByteArray(charset("utf-8")), Base64.DEFAULT))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (decrypted == null) return ""
        messageAfterDecrypt = String(decrypted)
        return messageAfterDecrypt
    }
}