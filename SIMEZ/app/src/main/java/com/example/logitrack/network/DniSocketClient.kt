package com.example.logitrack.network

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.DataOutputStream
import java.io.File
import java.net.Socket

object DniSocketClient {

    private const val HOST = "10.0.2.2"
    private const val PORT = 9001

    suspend fun enviarDni(
        context: Context,
        userId: Int,
        dniText: String,
        uriFrontal: Uri?,
        uriPosterior: Uri?
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            // Crear carpeta local al dispositiu
            val folder = File(context.filesDir, "dni/$userId")
            folder.mkdirs()

            // 1. Enviar i guardar número DNI (side = 2)
            val dniBytes = dniText.toByteArray()
            enviarDades(userId, 2, dniBytes)
            File(folder, "dni_number.enc").writeBytes(dniBytes)

            // 2. Enviar i guardar foto frontal (side = 0)
            uriFrontal?.let {
                val bytes = context.contentResolver.openInputStream(it)!!.readBytes()
                enviarDades(userId, 0, bytes)
                File(folder, "frontal.enc").writeBytes(bytes)
            }

            // 3. Enviar i guardar foto posterior (side = 1)
            uriPosterior?.let {
                val bytes = context.contentResolver.openInputStream(it)!!.readBytes()
                enviarDades(userId, 1, bytes)
                File(folder, "posterior.enc").writeBytes(bytes)
            }

            android.util.Log.d("DNI", "Arxius guardats a: ${folder.absolutePath}")
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun enviarDades(userId: Int, side: Int, data: ByteArray) {
        Socket(HOST, PORT).use { socket ->
            val out = DataOutputStream(socket.getOutputStream())
            out.writeInt(userId)
            out.writeByte(side)
            out.writeInt(data.size)
            out.write(data)
            out.flush()

            val response = socket.getInputStream().read()
            if (response != 1) throw Exception("Servidor no ha confirmat la recepció")
        }
    }
}