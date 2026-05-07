package com.example.logitrack.utils

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object EncryptionUtils {
    private const val ALGORITHM = "AES/CBC/PKCS5Padding"

    // IMPORTANT: En producció aquestes claus haurien d'estar al Android Keystore
    private val KEY = Constants.AES_KEY_ENCRYPTION
    private val IV  = Constants.AES_IV_ENCRYPTION

    fun encrypt(value: String): String? {
        return try {
            val secretKey = SecretKeySpec(KEY.toByteArray(), "AES")
            val ivSpec = IvParameterSpec(IV.toByteArray())
            val cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec)

            val encryptedBytes = cipher.doFinal(value.toByteArray())
            Base64.encodeToString(encryptedBytes, Base64.DEFAULT).trim()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun decrypt(encryptedValue: String): String? {
        return try {
            val secretKey = SecretKeySpec(KEY.toByteArray(), "AES")
            val ivSpec = IvParameterSpec(IV.toByteArray())
            val cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec)

            val decodedBytes = Base64.decode(encryptedValue, Base64.DEFAULT)
            String(cipher.doFinal(decodedBytes))
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}