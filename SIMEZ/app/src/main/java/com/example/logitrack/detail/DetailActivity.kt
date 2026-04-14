package com.example.logitrack.detail

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.logitrack.R
import com.example.logitrack.data.Oferte
import com.example.logitrack.data.RebutjarRequest
import com.example.logitrack.data.TrackingStep
import com.example.logitrack.network.RetrofitClient
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {

    private var oferta: Oferte? = null
    private var trackingSteps: List<TrackingStep> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val ofertaId = intent.getIntExtra("ofertaId", -1)
        val rolId = getSharedPreferences("logitrack", MODE_PRIVATE).getInt("rolId", -1)

        val tvTitle = findViewById<TextView>(R.id.tvDetailTitle)
        val tvEstat = findViewById<TextView>(R.id.tvDetailEstat)
        val tvData = findViewById<TextView>(R.id.tvDetailData)
        val recycler = findViewById<RecyclerView>(R.id.recyclerTracking)
        val btnNext = findViewById<Button>(R.id.btnNextEstat)
        val btnAcceptar = findViewById<Button>(R.id.btnAcceptar)
        val btnRebutjar = findViewById<Button>(R.id.btnRebutjar)

        recycler.layoutManager = LinearLayoutManager(this)

        if (rolId == 2) btnNext.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val responseOferta = RetrofitClient.instance.getOferte(ofertaId)
                val responseTracking = RetrofitClient.instance.getTrackingSteps()

                if (responseOferta.isSuccessful && responseTracking.isSuccessful) {
                    oferta = responseOferta.body()
                    trackingSteps = responseTracking.body() ?: emptyList()

                    oferta?.let { o ->
                        tvTitle.text = "Oferta #${o.id}"
                        tvEstat.text = "Estat: ${o.estatOfertaId}"
                        tvData.text = "Data: ${o.dataCreacio}"

                        recycler.adapter = TrackingAdapter(trackingSteps, o.estatOfertaId)

                        btnNext.setOnClickListener { avancarEstat(o) }

                        // Mostrar botones solo si es client y estat es Pendent (11)
                        if (rolId != 2 && o.estatOfertaId == 11) {
                            btnAcceptar.visibility = View.VISIBLE
                            btnRebutjar.visibility = View.VISIBLE
                        }

                        btnAcceptar.setOnClickListener {
                            lifecycleScope.launch {
                                try {
                                    RetrofitClient.instance.acceptarOferta(o.id)
                                    Toast.makeText(this@DetailActivity, "Oferta acceptada!", Toast.LENGTH_SHORT).show()
                                    finish()
                                } catch (e: Exception) {
                                    Toast.makeText(this@DetailActivity, e.message ?: "Error", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }

                        btnRebutjar.setOnClickListener {
                            val input = EditText(this@DetailActivity)
                            input.hint = "Motiu del rebuig"
                            AlertDialog.Builder(this@DetailActivity)
                                .setTitle("Rebutjar oferta")
                                .setView(input)
                                .setPositiveButton("Rebutjar") { _, _ ->
                                    val rao = input.text.toString()
                                    if (rao.isEmpty()) {
                                        Toast.makeText(this@DetailActivity, "Has d'indicar un motiu", Toast.LENGTH_SHORT).show()
                                        return@setPositiveButton
                                    }
                                    lifecycleScope.launch {
                                        try {
                                            RetrofitClient.instance.rebutjarOferta(o.id, RebutjarRequest(rao))
                                            Toast.makeText(this@DetailActivity, "Oferta rebutjada", Toast.LENGTH_SHORT).show()
                                            finish()
                                        } catch (e: Exception) {
                                            Toast.makeText(this@DetailActivity, e.message ?: "Error", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                                .setNegativeButton("Cancel·lar", null)
                                .show()
                        }
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this@DetailActivity, e.message ?: "Error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun avancarEstat(o: Oferte) {
        val currentIndex = trackingSteps.indexOfFirst { it.id == o.estatOfertaId }
        if (currentIndex == -1 || currentIndex >= trackingSteps.size - 1) {
            Toast.makeText(this, "Ja és l'últim estat", Toast.LENGTH_SHORT).show()
            return
        }
        val nextEstat = trackingSteps[currentIndex + 1]
        val updatedOferta = o.copy(estatOfertaId = nextEstat.id)

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.updateOferte(o.id, updatedOferta)
                if (response.isSuccessful) {
                    Toast.makeText(this@DetailActivity, "Estat actualitzat!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } catch (e: Exception) {
                Toast.makeText(this@DetailActivity, e.message ?: "Error", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

class TrackingAdapter(
    private val steps: List<TrackingStep>,
    private val currentEstatId: Int
) : RecyclerView.Adapter<TrackingAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNum: TextView = view.findViewById(R.id.tvTrackingNum)
        val tvNom: TextView = view.findViewById(R.id.tvTrackingNom)
        val tvEstat: TextView = view.findViewById(R.id.tvTrackingEstat)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tracking, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val step = steps[position]
        holder.tvNum.text = (position + 1).toString()
        holder.tvNom.text = step.nom ?: ""
        holder.tvEstat.text = if (step.id == currentEstatId) "← Estat actual" else ""
    }

    override fun getItemCount() = steps.size
}