package com.example.logitrack.orders

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.logitrack.R
import com.example.logitrack.data.Oferte
import com.example.logitrack.detail.DetailActivity
import com.example.logitrack.network.RetrofitClient
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch

class OrdersFragment : Fragment() {

    private var todesOfertes: List<Oferte> = emptyList()
    private lateinit var adapter: OfertesAdapter
    private lateinit var recycler: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_orders, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler = view.findViewById(R.id.recyclerOfertes)
        recycler.layoutManager = LinearLayoutManager(requireContext())

        val tabFilter = view.findViewById<TabLayout>(R.id.tabFilter)

        val prefs = requireContext().getSharedPreferences("logitrack", 0)
        val userId = prefs.getInt("userId", -1)
        val rolId = prefs.getInt("rolId", -1)

        lifecycleScope.launch {
            try {
                val response = if (rolId == 2) {
                    RetrofitClient.instance.getOfertes()
                } else {
                    RetrofitClient.instance.getOfertesByClient(userId)
                }

                if (response.isSuccessful) {
                    todesOfertes = response.body() ?: emptyList()
                    mostrarOfertes(todesOfertes)
                } else {
                    Toast.makeText(requireContext(), "Error carregant ofertes", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), e.message ?: "Error", Toast.LENGTH_SHORT).show()
            }
        }

        tabFilter.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val filtrades = when (tab?.position) {
                    1 -> todesOfertes.filter { it.estatOfertaId == 11 } // Pendent
                    2 -> todesOfertes.filter { it.estatOfertaId == 12 } // Acceptada
                    3 -> todesOfertes.filter { it.estatOfertaId == 14 } // En trànsit
                    else -> todesOfertes
                }
                mostrarOfertes(filtrades)
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun mostrarOfertes(ofertes: List<Oferte>) {
        recycler.adapter = OfertesAdapter(ofertes) { oferta ->
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra("ofertaId", oferta.id)
            startActivity(intent)
        }
    }
}

class OfertesAdapter(
    private val items: List<Oferte>,
    private val onClick: (Oferte) -> Unit
) : RecyclerView.Adapter<OfertesAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvId: TextView = view.findViewById(R.id.tvOfertaId)
        val tvEstat: TextView = view.findViewById(R.id.tvEstat)
        val tvData: TextView = view.findViewById(R.id.tvData)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_oferta, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val oferta = items[position]
        holder.tvId.text = "Oferta #${oferta.id}"
        holder.tvEstat.text = when (oferta.estatOfertaId) {
            11 -> "Estat: Pendent"
            12 -> "Estat: Acceptada"
            13 -> "Estat: Rebutjada"
            14 -> "Estat: En trànsit"
            15 -> "Estat: Finalitzada"
            else -> "Estat: ${oferta.estatOfertaId}"
        }
        holder.tvData.text = "Data: ${oferta.dataCreacio}"
        holder.itemView.setOnClickListener { onClick(oferta) }
    }

    override fun getItemCount() = items.size
}