package com.example.logitrack

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.logitrack.databinding.ActivityMainBinding
import com.example.logitrack.orders.OrdersFragment
import com.example.logitrack.profile.ProfileFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Mostrar OrdersFragment por defecto
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameContainer, OrdersFragment())
            .commit()

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_orders -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameContainer, OrdersFragment())
                        .commit()
                    true
                }
                R.id.nav_profile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameContainer, ProfileFragment())
                        .commit()
                    true
                }
                else -> false
            }
        }
    }
}