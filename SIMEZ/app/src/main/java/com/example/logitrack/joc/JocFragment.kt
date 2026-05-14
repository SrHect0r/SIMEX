package com.example.logitrack.joc

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.logitrack.R

class JocFragment : Fragment() {

    val emojis = listOf("✈️", "🚢", "🚛", "📦", "🌍", "🚂", "⚓", "🏭", "⚓")

    var cartas = mutableListOf<String>()
    var cartasVolteadas = mutableListOf<Int>()
    var cartasAcertadas = mutableSetOf<Int>()
    var intentos = 0
    var parejas = 0
    var bloqueado = false

    lateinit var gridJuego: GridLayout
    lateinit var tvIntentos: TextView
    lateinit var tvParejas: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_joc, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gridJuego  = view.findViewById(R.id.gridJuego)
        tvIntentos = view.findViewById(R.id.tvIntentos)
        tvParejas  = view.findViewById(R.id.tvParejas)

        val btnReiniciar = view.findViewById<Button>(R.id.btnReiniciar)
        btnReiniciar.setOnClickListener { iniciarJuego() }

        iniciarJuego()
    }

    fun iniciarJuego() {
        cartas = (emojis + emojis).shuffled().toMutableList()
        cartasVolteadas.clear()
        cartasAcertadas.clear()
        intentos = 0
        parejas  = 0
        bloqueado = false
        actualizarContadors()
        dibujarTablero()
    }

    fun dibujarTablero() {
        gridJuego.removeAllViews()

        for (i in cartas.indices) {
            val boton = Button(requireContext())

            val params = GridLayout.LayoutParams()
            params.width = 0
            params.height = 180
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            params.rowSpec   = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            params.setMargins(5, 5, 5, 5)
            boton.layoutParams = params

            boton.text = ""
            boton.setBackgroundColor(Color.parseColor("#1565C0"))

            boton.setOnClickListener { voltearCarta(i, boton) }

            gridJuego.addView(boton)
        }
    }

    fun voltearCarta(posicion: Int, boton: Button) {
        if (bloqueado) return
        if (cartasAcertadas.contains(posicion)) return
        if (cartasVolteadas.contains(posicion)) return

        boton.text = cartas[posicion]
        boton.textSize = 24f
        boton.setBackgroundColor(Color.WHITE)
        cartasVolteadas.add(posicion)

        if (cartasVolteadas.size == 2) {
            bloqueado = true
            intentos++
            actualizarContadors()

            val primera = cartasVolteadas[0]
            val segunda = cartasVolteadas[1]

            if (cartas[primera] == cartas[segunda]) {
                cartasAcertadas.add(primera)
                cartasAcertadas.add(segunda)
                parejas++
                actualizarContadors()
                cartasVolteadas.clear()
                bloqueado = false

                if (parejas == 9) {
                    tvParejas.text = "¡Guanyat en $intentos intents!"
                }
            } else {
                Handler(Looper.getMainLooper()).postDelayed({
                    if (isAdded) {
                        val botonPrimera = gridJuego.getChildAt(primera) as Button
                        val botonSegunda = gridJuego.getChildAt(segunda) as Button
                        botonPrimera.text = ""
                        botonSegunda.text = ""
                        botonPrimera.setBackgroundColor(Color.parseColor("#1565C0"))
                        botonSegunda.setBackgroundColor(Color.parseColor("#1565C0"))
                        cartasVolteadas.clear()
                        bloqueado = false
                    }
                }, 1000)
            }
        }
    }

    fun actualizarContadors() {
        tvIntentos.text = "Intents: $intentos"
        tvParejas.text  = "Parelles: $parejas/8"
    }
}