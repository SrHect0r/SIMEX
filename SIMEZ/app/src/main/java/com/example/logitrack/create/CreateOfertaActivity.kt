package com.example.logitrack.create

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.logitrack.R
import com.example.logitrack.data.*
import com.example.logitrack.network.RetrofitClient
import com.example.logitrack.utils.Constants
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class CreateOfertaActivity : AppCompatActivity() {

    private var transports: List<TipusTransport> = emptyList()
    private var fluxes: List<TipusFlux> = emptyList()
    private var carregues: List<TipusCarrega> = emptyList()
    private var incoterms: List<Incoterm> = emptyList()
    private var clients: List<Client> = emptyList()
    private var ports: List<Port> = emptyList()
    private var aeroports: List<Aeroport> = emptyList()
    private var liniesMaritimes: List<LiniesTransportMaritim> = emptyList()
    private var tipusContenidors: List<TipusContenidor> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_oferta)

        val spinnerTransport = findViewById<Spinner>(R.id.spinnerTransport)
        val spinnerFlux = findViewById<Spinner>(R.id.spinnerFlux)
        val spinnerCarrega = findViewById<Spinner>(R.id.spinnerCarrega)
        val spinnerIncoterm = findViewById<Spinner>(R.id.spinnerIncoterm)
        val spinnerClient = findViewById<Spinner>(R.id.spinnerClient)
        
        val spinnerPortOrigen = findViewById<Spinner>(R.id.spinnerPortOrigen)
        val spinnerPortDesti = findViewById<Spinner>(R.id.spinnerPortDesti)
        val spinnerAeroportOrigen = findViewById<Spinner>(R.id.spinnerAeroportOrigen)
        val spinnerAeroportDesti = findViewById<Spinner>(R.id.spinnerAeroportDesti)
        val spinnerLiniaMaritima = findViewById<Spinner>(R.id.spinnerLiniaMaritima)
        val spinnerTipusContenidor = findViewById<Spinner>(R.id.spinnerTipusContenidor)
        
        val tvPortOrigen = findViewById<TextView>(R.id.tvPortOrigen)
        val tvPortDesti = findViewById<TextView>(R.id.tvPortDesti)
        val tvAeroportOrigen = findViewById<TextView>(R.id.tvAeroportOrigen)
        val tvAeroportDesti = findViewById<TextView>(R.id.tvAeroportDesti)
        val tvLiniaMaritima = findViewById<TextView>(R.id.tvLiniaMaritima)
        val tvTipusContenidor = findViewById<TextView>(R.id.tvTipusContenidor)

        val etPesBrut = findViewById<EditText>(R.id.etPesBrut)
        val etVolum = findViewById<EditText>(R.id.etVolum)
        val etComentaris = findViewById<EditText>(R.id.etComentaris)
        val btnCrear = findViewById<Button>(R.id.btnCrearOferta)

        val prefs = getSharedPreferences("logitrack", MODE_PRIVATE)
        val agentId = prefs.getInt("userId", -1)

        lifecycleScope.launch {
            try {
                transports = RetrofitClient.instance.getTipusTransports().body() ?: emptyList()
                fluxes = RetrofitClient.instance.getTipusFluxes().body() ?: emptyList()
                carregues = RetrofitClient.instance.getTipusCarregas().body() ?: emptyList()
                incoterms = RetrofitClient.instance.getIncoterms().body() ?: emptyList()
                clients = RetrofitClient.instance.getClients().body() ?: emptyList()
                ports = RetrofitClient.instance.getPorts().body() ?: emptyList()
                aeroports = RetrofitClient.instance.getAeroports().body() ?: emptyList()
                liniesMaritimes = RetrofitClient.instance.getLiniesTransportMaritim().body() ?: emptyList()
                tipusContenidors = RetrofitClient.instance.getTipusContenidors().body() ?: emptyList()

                spinnerTransport.adapter = ArrayAdapter(this@CreateOfertaActivity, android.R.layout.simple_spinner_dropdown_item, transports.map { it.tipus })
                spinnerFlux.adapter = ArrayAdapter(this@CreateOfertaActivity, android.R.layout.simple_spinner_dropdown_item, fluxes.map { it.tipus })
                spinnerCarrega.adapter = ArrayAdapter(this@CreateOfertaActivity, android.R.layout.simple_spinner_dropdown_item, carregues.map { it.tipus })
                spinnerIncoterm.adapter = ArrayAdapter(this@CreateOfertaActivity, android.R.layout.simple_spinner_dropdown_item, incoterms.map { it.id.toString() })
                spinnerClient.adapter = ArrayAdapter(this@CreateOfertaActivity, android.R.layout.simple_spinner_dropdown_item, clients.map { it.id.toString() })
                
                val portNames = ports.map { it.nom }
                spinnerPortOrigen.adapter = ArrayAdapter(this@CreateOfertaActivity, android.R.layout.simple_spinner_dropdown_item, portNames)
                spinnerPortDesti.adapter = ArrayAdapter(this@CreateOfertaActivity, android.R.layout.simple_spinner_dropdown_item, portNames)
                
                val aeroNames = aeroports.map { it.nom }
                spinnerAeroportOrigen.adapter = ArrayAdapter(this@CreateOfertaActivity, android.R.layout.simple_spinner_dropdown_item, aeroNames)
                spinnerAeroportDesti.adapter = ArrayAdapter(this@CreateOfertaActivity, android.R.layout.simple_spinner_dropdown_item, aeroNames)

                spinnerLiniaMaritima.adapter = ArrayAdapter(this@CreateOfertaActivity, android.R.layout.simple_spinner_dropdown_item, liniesMaritimes.map { it.nom })
                spinnerTipusContenidor.adapter = ArrayAdapter(this@CreateOfertaActivity, android.R.layout.simple_spinner_dropdown_item, tipusContenidors.map { it.tipus })

            } catch (e: Exception) {
                Toast.makeText(this@CreateOfertaActivity, e.message ?: "Error carregant dades", Toast.LENGTH_SHORT).show()
            }
        }

        spinnerTransport.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val transport = transports.getOrNull(position)
                // Marítim = 1, Aeri = 2, Terrestre = 3
                when (transport?.id) {
                    1 -> {
                        tvPortOrigen.visibility = View.VISIBLE
                        spinnerPortOrigen.visibility = View.VISIBLE
                        tvPortDesti.visibility = View.VISIBLE
                        spinnerPortDesti.visibility = View.VISIBLE
                        tvLiniaMaritima.visibility = View.VISIBLE
                        spinnerLiniaMaritima.visibility = View.VISIBLE
                        tvTipusContenidor.visibility = View.VISIBLE
                        spinnerTipusContenidor.visibility = View.VISIBLE
                        
                        tvAeroportOrigen.visibility = View.GONE
                        spinnerAeroportOrigen.visibility = View.GONE
                        tvAeroportDesti.visibility = View.GONE
                        spinnerAeroportDesti.visibility = View.GONE
                    }
                    2 -> {
                        tvPortOrigen.visibility = View.GONE
                        spinnerPortOrigen.visibility = View.GONE
                        tvPortDesti.visibility = View.GONE
                        spinnerPortDesti.visibility = View.GONE
                        tvLiniaMaritima.visibility = View.GONE
                        spinnerLiniaMaritima.visibility = View.GONE
                        tvTipusContenidor.visibility = View.GONE
                        spinnerTipusContenidor.visibility = View.GONE
                        
                        tvAeroportOrigen.visibility = View.VISIBLE
                        spinnerAeroportOrigen.visibility = View.VISIBLE
                        tvAeroportDesti.visibility = View.VISIBLE
                        spinnerAeroportDesti.visibility = View.VISIBLE
                    }
                    else -> {
                        tvPortOrigen.visibility = View.GONE
                        spinnerPortOrigen.visibility = View.GONE
                        tvPortDesti.visibility = View.GONE
                        spinnerPortDesti.visibility = View.GONE
                        tvAeroportOrigen.visibility = View.GONE
                        spinnerAeroportOrigen.visibility = View.GONE
                        tvAeroportDesti.visibility = View.GONE
                        spinnerAeroportDesti.visibility = View.GONE
                        tvLiniaMaritima.visibility = View.GONE
                        spinnerLiniaMaritima.visibility = View.GONE
                        tvTipusContenidor.visibility = View.GONE
                        spinnerTipusContenidor.visibility = View.GONE
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        btnCrear.setOnClickListener {
            val transport = transports.getOrNull(spinnerTransport.selectedItemPosition)
            val flux = fluxes.getOrNull(spinnerFlux.selectedItemPosition)
            val carrega = carregues.getOrNull(spinnerCarrega.selectedItemPosition)
            val incoterm = incoterms.getOrNull(spinnerIncoterm.selectedItemPosition)
            val client = clients.getOrNull(spinnerClient.selectedItemPosition)
            val comentaris = etComentaris.text.toString()
            val pesBrutStr = etPesBrut.text.toString()
            val volumStr = etVolum.text.toString()

            if (transport == null || flux == null || carrega == null || incoterm == null || client == null) {
                Toast.makeText(this, "Omple tots els camps obligatoris", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (pesBrutStr.isEmpty() || volumStr.isEmpty()) {
                Toast.makeText(this, "El pes i el volum són obligatoris", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val pesBrut = pesBrutStr.toDoubleOrNull() ?: 0.0
            val volum = volumStr.toDoubleOrNull() ?: 0.0

            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val dateStr = sdf.format(Date())

            val novaOferta = Oferte(
                id = 0,
                tipusTransportId = transport.id,
                tipusFluxeId = flux.id,
                tipusCarregaId = carrega.id,
                incotermId = incoterm.id,
                clientId = client.id,
                comentaris = if (comentaris.isEmpty()) null else comentaris,
                agentComercialId = agentId,
                transportistaId = null,
                pesBrut = pesBrut,
                volum = volum,
                tipusValidacioId = 9,
                portOrigenId = if (transport.id == 1) ports.getOrNull(spinnerPortOrigen.selectedItemPosition)?.id else null,
                portDestiId = if (transport.id == 1) ports.getOrNull(spinnerPortDesti.selectedItemPosition)?.id else null,
                aeroportOrigenId = if (transport.id == 2) aeroports.getOrNull(spinnerAeroportOrigen.selectedItemPosition)?.id else null,
                aeroportDestiId = if (transport.id == 2) aeroports.getOrNull(spinnerAeroportDesti.selectedItemPosition)?.id else null,
                liniaTransportMaritimId = if (transport.id == 1) liniesMaritimes.getOrNull(spinnerLiniaMaritima.selectedItemPosition)?.id else null,
                estatOfertaId = Constants.ESTAT_PENDENT,
                operadorId = agentId,
                dataCreacio = dateStr,
                dataValidesaInicial = dateStr,
                dataValidesaFinal = dateStr,
                raoRebuig = null,
                tipusContenidorId = if (transport.id == 1) tipusContenidors.getOrNull(spinnerTipusContenidor.selectedItemPosition)?.id else null
            )

            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.instance.createOferta(novaOferta)
                    if (response.isSuccessful) {
                        Toast.makeText(this@CreateOfertaActivity, "Oferta creada!", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        val errorMsg = response.errorBody()?.string() ?: "Error desconegut"
                        android.util.Log.e("CreateOferta", "Error: $errorMsg")
                        Toast.makeText(this@CreateOfertaActivity, "Error servidor: $errorMsg", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@CreateOfertaActivity, e.message ?: "Error en la petició", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
