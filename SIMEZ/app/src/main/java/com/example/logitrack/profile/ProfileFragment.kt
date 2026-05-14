package com.example.logitrack.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.logitrack.R
import com.example.logitrack.login.LoginActivity
import com.example.logitrack.network.DniSocketClient
import com.example.logitrack.network.RetrofitClient
import com.example.logitrack.utils.Constants
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var uriFrontal: Uri? = null
    private var uriPosterior: Uri? = null

    private val pickFrontal = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            uriFrontal = it
            view?.findViewById<ImageView>(R.id.ivFrontalIcon)?.setImageResource(R.drawable.ic_upload)
            view?.findViewById<TextView>(R.id.tvFrontalText)?.text = "Foto frontal carregada"
        }
    }

    private val pickPosterior = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            uriPosterior = it
            view?.findViewById<ImageView>(R.id.ivPosteriorIcon)?.setImageResource(R.drawable.ic_upload)
            view?.findViewById<TextView>(R.id.tvPosteriorText)?.text = "Foto posterior carregada"
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvNom        = view.findViewById<TextView>(R.id.tvNom)
        val tvCorreu     = view.findViewById<TextView>(R.id.tvCorreu)
        val btnLogout    = view.findViewById<MaterialButton>(R.id.btnLogout)
        val btnFrontal   = view.findViewById<LinearLayout>(R.id.btnUploadFrontal)
        val btnPosterior = view.findViewById<LinearLayout>(R.id.btnUploadPosterior)
        val btnEnviar    = view.findViewById<MaterialButton>(R.id.btnEnviarDni)
        val etDniNumber  = view.findViewById<EditText>(R.id.etDniNumber)
        val btnJoc       = view.findViewById<LinearLayout>(R.id.btnAnarAlJoc)

        val prefs  = requireContext().getSharedPreferences(Constants.PREFS_NAME, 0)
        val userId = prefs.getInt(Constants.PREF_USER_ID, -1)

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getUsuari(userId)
                if (response.isSuccessful) {
                    val usuari = response.body()!!
                    tvNom.text    = "${usuari.nom} ${usuari.cognoms}"
                    tvCorreu.text = usuari.correu
                }
            } catch (e: Exception) {
                // Toast.makeText(requireContext(), "Error carregant perfil", Toast.LENGTH_SHORT).show()
            }
        }

        btnFrontal.setOnClickListener   { pickFrontal.launch("image/*") }
        btnPosterior.setOnClickListener { pickPosterior.launch("image/*") }

        btnEnviar.setOnClickListener {
            val dniText = etDniNumber.text.toString().trim()

            if (dniText.isEmpty()) {
                Toast.makeText(requireContext(), "Escriu el número de DNI", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (uriFrontal == null || uriPosterior == null) {
                Toast.makeText(requireContext(), "Selecciona les dues fotos del DNI", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                btnEnviar.isEnabled = false
                btnEnviar.text = "Enviant..."

                val success = DniSocketClient.enviarDni(requireContext(), userId, dniText, uriFrontal, uriPosterior)

                if (success) {
                    Toast.makeText(requireContext(), "DNI enviat i xifrat amb AES", Toast.LENGTH_LONG).show()
                    etDniNumber.text.clear()
                    uriFrontal = null
                    uriPosterior = null
                    view.findViewById<TextView>(R.id.tvFrontalText).text = "Pujar part frontal"
                    view.findViewById<TextView>(R.id.tvPosteriorText).text = "Pujar part posterior"
                } else {
                    Toast.makeText(requireContext(), "Error en el servidor de seguretat", Toast.LENGTH_LONG).show()
                }

                btnEnviar.isEnabled = true
                btnEnviar.text = "Enviar DNI xifrat"
            }
        }

        btnJoc.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.frameContainer, com.example.logitrack.joc.JocFragment())
                .addToBackStack(null)
                .commit()
        }

        btnLogout.setOnClickListener {
            prefs.edit().clear().apply()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}