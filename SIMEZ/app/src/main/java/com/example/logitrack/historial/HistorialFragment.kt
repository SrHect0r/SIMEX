package com.example.logitrack.historial

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
import com.example.logitrack.network.RetrofitClient
import kotlinx.coroutines.launch

class HistorialFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_historial, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recycler = view.findViewById<RecyclerView>(R.id.recyclerHistorial)
        recycler.layoutManager = LinearLayoutManager(requireContext())

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
                    // Filtra solo les completades (estat 15)
                    val completades = response.body()?.filter { it.estatOfertaId == 15 } ?: emptyList()
                    recycler.adapter = HistorialAdapter(completades)
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), e.message ?: "Error", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

class HistorialAdapter(private val items: List<Oferte>) : RecyclerView.Adapter<HistorialAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvRef: TextView = view.findViewById(R.id.tvHistorialRef)
        val tvData: TextView = view.findViewById(R.id.tvHistorialData)
        val tvEstat: TextView = view.findViewById(R.id.tvHistorialEstat)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_historial, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val oferta = items[position]
        holder.tvRef.text = "Oferta #${oferta.id}"
        holder.tvData.text = "Data: ${oferta.dataCreacio}"
        holder.tvEstat.text = "✅ Lliurat"
    }

    override fun getItemCount() = items.size
}