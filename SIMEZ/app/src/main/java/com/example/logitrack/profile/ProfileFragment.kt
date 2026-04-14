package com.example.logitrack.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.logitrack.R
import com.example.logitrack.login.LoginActivity
import com.example.logitrack.network.RetrofitClient
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvNom = view.findViewById<TextView>(R.id.tvNom)
        val tvCorreu = view.findViewById<TextView>(R.id.tvCorreu)
        val tvRol = view.findViewById<TextView>(R.id.tvRol)
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)

        val prefs = requireContext().getSharedPreferences("logitrack", 0)
        val userId = prefs.getInt("userId", -1)

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getUsuari(userId)
                if (response.isSuccessful) {
                    val usuari = response.body()!!
                    tvNom.text = "Nom: ${usuari.nom} ${usuari.cognoms}"
                    tvCorreu.text = "Correu: ${usuari.correu}"
                    tvRol.text = if (usuari.rolId == 2) "Rol: Agent Comercial" else "Rol: Client"
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), e.message ?: "Error", Toast.LENGTH_SHORT).show()
            }
        }

        btnLogout.setOnClickListener {
            prefs.edit().clear().apply()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }
    }
}