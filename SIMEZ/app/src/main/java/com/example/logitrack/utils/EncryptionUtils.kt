package com.example.logitrack.utils

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object EncryptionUtils {
    private const val ALGORITHM = "AES/CBC/PKCS5Padding"
    
    // IMPORTANTE: En una app real, esta clave NO debería estar hardcodeada.
    // Debería guardarse en el Android Keystore.
    // La clave debe tener 16, 24 o 32 caracteres para AES.
    private const val KEY = "1234567890123456" // 16 bytes = AES-128
    private const val IV = "1234567890123456"  // 16 bytes para el vector

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
