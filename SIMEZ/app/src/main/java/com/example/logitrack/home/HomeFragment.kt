package com.example.logitrack.home

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
import com.example.logitrack.utils.Constants
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = requireContext().getSharedPreferences(Constants.PREFS_NAME, 0)
        val nom = prefs.getString(Constants.PREF_USER_NOM, "")
        val rolId = prefs.getInt(Constants.PREF_ROL_ID, -1)

        view.findViewById<TextView>(R.id.tvBenvinguda).text = "Bon dia, $nom"

        val recyclerPendents = view.findViewById<RecyclerView>(R.id.recyclerPendents)
        val recyclerActives = view.findViewById<RecyclerView>(R.id.recyclerActives)
        recyclerPendents.layoutManager = LinearLayoutManager(requireContext())
        recyclerActives.layoutManager = LinearLayoutManager(requireContext())

        val fabCrear = view.findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fabCrearOfertaHome)
        
        if (rolId == Constants.ROL_AGENT || rolId == Constants.ROL_ADMIN) { 
            fabCrear.visibility = View.VISIBLE
            fabCrear.setOnClickListener {
                startActivity(Intent(requireContext(), com.example.logitrack.create.CreateOfertaActivity::class.java))
            }
        } else {
            fabCrear.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        loadOfertes()
    }

    private fun loadOfertes() {
        val view = view ?: return
        val prefs = requireContext().getSharedPreferences(Constants.PREFS_NAME, 0)
        val userId = prefs.getInt(Constants.PREF_USER_ID, -1)
        val rolId = prefs.getInt(Constants.PREF_ROL_ID, -1)

        val recyclerPendents = view.findViewById<RecyclerView>(R.id.recyclerPendents)
        val recyclerActives = view.findViewById<RecyclerView>(R.id.recyclerActives)
        val tvCountPendents = view.findViewById<TextView>(R.id.tvCountPendents)
        val tvCountActives = view.findViewById<TextView>(R.id.tvCountActives)

        lifecycleScope.launch {
            try {
                val response = if (rolId == Constants.ROL_AGENT || rolId == Constants.ROL_ADMIN) {
                    RetrofitClient.instance.getOfertes()
                } else {
                    RetrofitClient.instance.getOfertesByClient(userId)
                }

                if (response.isSuccessful) {
                    val ofertes = response.body() ?: emptyList()

                    val pendents = ofertes.filter { it.estatOfertaId == Constants.ESTAT_PENDENT }
                    tvCountPendents.text = pendents.size.toString()
                    recyclerPendents.adapter = HomeOfertesAdapter(pendents) { oferta ->
                        val intent = Intent(requireContext(), DetailActivity::class.java)
                        intent.putExtra("ofertaId", oferta.id)
                        startActivity(intent)
                    }

                    val actives = ofertes.filter { it.estatOfertaId in listOf(Constants.ESTAT_ACCEPTADA, Constants.ESTAT_EN_TRANSIT) }
                    tvCountActives.text = actives.size.toString()
                    recyclerActives.adapter = HomeOfertesAdapter(actives) { oferta ->
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
}

class HomeOfertesAdapter(
    private val items: List<Oferte>,
    private val onClick: (Oferte) -> Unit
) : RecyclerView.Adapter<HomeOfertesAdapter.ViewHolder>() {

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
            Constants.ESTAT_PENDENT -> "⏳ Pendent"
            Constants.ESTAT_ACCEPTADA -> "Aceptada"
            Constants.ESTAT_REBUTJADA -> "Rebutjada"
            Constants.ESTAT_EN_TRANSIT -> "🚢 En trànsit"
            Constants.ESTAT_LLIURADA -> "Lliurat"
            else -> "Estat: ${oferta.estatOfertaId}"
        }

        when (oferta.estatOfertaId) {
            Constants.ESTAT_ACCEPTADA, Constants.ESTAT_LLIURADA -> { 
                holder.tvEstat.setBackgroundResource(R.drawable.status_badge_bg)
                holder.tvEstat.backgroundTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#DCFCE7"))
                holder.tvEstat.setTextColor(android.graphics.Color.parseColor("#15803D"))
            }
            Constants.ESTAT_PENDENT -> { 
                holder.tvEstat.setBackgroundResource(R.drawable.status_badge_bg)
                holder.tvEstat.backgroundTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#FEF3C7"))
                holder.tvEstat.setTextColor(android.graphics.Color.parseColor("#B45309"))
            }
            Constants.ESTAT_EN_TRANSIT -> {
                holder.tvEstat.setBackgroundResource(R.drawable.status_badge_bg)
                holder.tvEstat.backgroundTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#DBEAFE"))
                holder.tvEstat.setTextColor(android.graphics.Color.parseColor("#1D4ED8"))
            }
            else -> {
                holder.tvEstat.setBackgroundResource(R.drawable.status_badge_bg)
                holder.tvEstat.backgroundTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#F3F4F6"))
                holder.tvEstat.setTextColor(android.graphics.Color.parseColor("#4B5563"))
            }
        }
        holder.tvData.text = "Data: ${oferta.dataCreacio}"
        holder.itemView.setOnClickListener { onClick(oferta) }
    }

    override fun getItemCount() = items.size
}