package com.example.logitrack.agent

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.logitrack.databinding.FragmentAgentOrdersBinding
import com.example.logitrack.detail.DetailActivity
import com.example.logitrack.network.RetrofitClient
import com.example.logitrack.orders.OfertesAdapter
import kotlinx.coroutines.launch

class AgentOrdersFragment : Fragment() {

    private var _binding: FragmentAgentOrdersBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAgentOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvOfertes.layoutManager = LinearLayoutManager(requireContext())
        carregarTotesOfertes()
    }

    private fun carregarTotesOfertes() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getOfertes()
                if (response.isSuccessful) {
                    val ofertes = response.body() ?: emptyList()
                    binding.rvOfertes.adapter = OfertesAdapter(ofertes) { oferta ->
                        val intent = Intent(requireContext(), DetailActivity::class.java)
                        intent.putExtra("ofertaId", oferta.id)
                        startActivity(intent)
                    }
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