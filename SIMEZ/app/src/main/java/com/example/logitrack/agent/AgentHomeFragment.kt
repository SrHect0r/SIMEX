package com.example.logitrack.agent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.logitrack.databinding.FragmentAgentHomeBinding
import com.example.logitrack.network.RetrofitClient
import com.example.logitrack.utils.Constants
import kotlinx.coroutines.launch

class AgentHomeFragment : Fragment() {

    private var _binding: FragmentAgentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAgentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = requireContext().getSharedPreferences("logitrack", 0)
        val nom = prefs.getString("userName", "Agent")
        val userId = prefs.getInt("userId", -1)

        binding.tvWelcome.text = "Benvingut, $nom"

        carregarResum(userId)
    }

    private fun carregarResum(userId: Int) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getOfertes()
                if (response.isSuccessful) {
                    val totes = response.body() ?: emptyList()
                    val pendents = totes.count { it.estatOfertaId == Constants.ESTAT_PENDENT }
                    val actives  = totes.count { it.estatOfertaId == Constants.ESTAT_ACCEPTADA || it.estatOfertaId == Constants.ESTAT_EN_TRANSIT }
                    val tancades = totes.count { it.estatOfertaId == Constants.ESTAT_LLIURADA }

                    binding.tvPendents.text = pendents.toString()
                    binding.tvActives.text  = actives.toString()
                    binding.tvTancades.text = tancades.toString()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), e.message ?: "Error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}