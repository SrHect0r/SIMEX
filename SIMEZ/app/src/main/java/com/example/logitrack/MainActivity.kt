package com.example.logitrack

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.logitrack.databinding.ActivityMainBinding
import com.example.logitrack.documents.DocumentsFragment
import com.example.logitrack.historial.HistorialFragment
import com.example.logitrack.home.HomeFragment
import com.example.logitrack.orders.OrdersFragment
import com.example.logitrack.profile.ProfileFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Mostrar HomeFragment por defecto
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameContainer, HomeFragment())
            .commit()

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.nav_inici -> HomeFragment()
                R.id.nav_orders -> OrdersFragment()
                R.id.nav_documents -> DocumentsFragment()
                R.id.nav_historial -> HistorialFragment()
                R.id.nav_profile -> ProfileFragment()
                else -> HomeFragment()
            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.frameContainer, fragment)
                .commit()
            true
        }
    }
}