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
import com.example.logitrack.utils.Constants
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

        val prefs = requireContext().getSharedPreferences(Constants.PREFS_NAME, 0)
        val userId = prefs.getInt(Constants.PREF_USER_ID, -1)
        val rolId = prefs.getInt(Constants.PREF_ROL_ID, -1)

        lifecycleScope.launch {
            try {
                val response = if (rolId == Constants.ROL_AGENT || rolId == Constants.ROL_ADMIN) {
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
                    1 -> todesOfertes.filter { it.estatOfertaId == Constants.ESTAT_PENDENT }
                    2 -> todesOfertes.filter { it.estatOfertaId == Constants.ESTAT_ACCEPTADA }
                    3 -> todesOfertes.filter { it.estatOfertaId == Constants.ESTAT_EN_TRANSIT }
                    else -> todesOfertes
                }
                mostrarOfertes(filtrades)
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        val fabCrear = view.findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fabCrearOferta)
        if (rolId == Constants.ROL_AGENT || rolId == Constants.ROL_ADMIN) {
            fabCrear.visibility = View.VISIBLE
            fabCrear.setOnClickListener {
                startActivity(Intent(requireContext(), com.example.logitrack.create.CreateOfertaActivity::class.java))
            }
        }
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
        val context = holder.itemView.context

        holder.tvId.text = "LOG-2024-${oferta.id}"
        holder.tvData.text = oferta.dataCreacio

        val (text, bgColor, textColor) = when (oferta.estatOfertaId) {
            Constants.ESTAT_PENDENT -> Triple("PENDENT", R.color.status_pending_bg, R.color.status_pending_text)
            Constants.ESTAT_ACCEPTADA -> Triple("ACCEPTADA", R.color.status_recollit_bg, R.color.status_recollit_text)
            Constants.ESTAT_EN_TRANSIT -> Triple("EN TRÀNSIT", R.color.status_transit_bg, R.color.status_transit_text)
            Constants.ESTAT_LLIURADA -> Triple("LLIURADA", R.color.status_delivered_bg, R.color.status_delivered_text)
            else -> Triple("OFERTA", R.color.gray_100, R.color.gray_600)
        }

        holder.tvEstat.text = text
        holder.tvEstat.backgroundTintList = android.content.res.ColorStateList.valueOf(
            androidx.core.content.ContextCompat.getColor(context, bgColor)
        )
        holder.tvEstat.setTextColor(androidx.core.content.ContextCompat.getColor(context, textColor))
        holder.tvEstat.setBackgroundResource(R.drawable.input_bg)

        holder.itemView.setOnClickListener { onClick(oferta) }
    }

    override fun getItemCount() = items.size
}