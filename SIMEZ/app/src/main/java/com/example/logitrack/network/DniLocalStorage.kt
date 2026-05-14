package com.example.logitrack.network

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.DataOutputStream
import java.net.Socket
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import com.example.logitrack.utils.Constants

object DniLocalStorage {

    private val HOST = Constants.SOCKET_HOST
    private val PORT = Constants.SOCKET_PORT


    // Han de coincidir EXACTAMENT amb el servidor C#
    private val AES_KEY = android.util.Base64.decode("cTu3X9kLmNpQrSvWyZ1234567890ABCD", android.util.Base64.DEFAULT)
    private val AES_IV  = android.util.Base64.decode("AAAAAAAAAAAAAAAAAAAAAA==", android.util.Base64.DEFAULT)

    suspend fun enviarDni(
        context: Context,
        userId: Int,
        dniText: String,
        uriFrontal: Uri?,
        uriPosterior: Uri?
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            // 1. Enviar el número de DNI xifrat (side = 2 per diferenciar-lo de les fotos)
            enviarTextXifrat(userId, dniText)

            // 2. Enviar les fotos
            uriFrontal?.let { enviarImatge(context, userId, it, side = 0) }
            uriPosterior?.let { enviarImatge(context, userId, it, side = 1) }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun enviarTextXifrat(userId: Int, text: String) {
        val encrypted = encrypt(text.toByteArray())
        
        Socket(HOST, PORT).use { socket ->
            val out = DataOutputStream(socket.getOutputStream())
            out.writeInt(userId)
            out.writeByte(2) // 2 = Número de DNI (text)
            out.writeInt(encrypted.size)
            out.write(encrypted)
            out.flush()
            
            val response = socket.getInputStream().read()
            if (response != 1) throw Exception("Error enviant text xifrat")
        }
    }

    private fun enviarImatge(context: Context, userId: Int, uri: Uri, side: Int) {
        // 1. Llegir bytes de la imatge
        val bytes = context.contentResolver.openInputStream(uri)!!.readBytes()

        // 2. Xifrar amb AES
        val encrypted = encrypt(bytes)

        // 3. Enviar per socket
        Socket(HOST, PORT).use { socket ->
            val out = DataOutputStream(socket.getOutputStream())

            // userId (4 bytes)
            out.writeInt(userId)
            // side (1 byte): 0=frontal, 1=posterior
            out.writeByte(side)
            // longitud (4 bytes)
            out.writeInt(encrypted.size)
            // dades xifrades
            out.write(encrypted)
            out.flush()

            // Esperar resposta OK (1 byte = 1)
            val response = socket.getInputStream().read()
            if (response != 1) throw Exception("Servidor no ha confirmat la recepció")
        }
    }

    private fun encrypt(data: ByteArray): ByteArray {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(AES_KEY, "AES"), IvParameterSpec(AES_IV))
        return cipher.doFinal(data)
    }
}