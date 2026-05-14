package com.example.logitrack

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.logitrack.agent.AgentHomeFragment
import com.example.logitrack.agent.AgentOrdersFragment
import com.example.logitrack.databinding.ActivityMainBinding
import com.example.logitrack.documents.DocumentsFragment
import com.example.logitrack.historial.HistorialFragment
import com.example.logitrack.home.HomeFragment
import com.example.logitrack.joc.JocFragment
import com.example.logitrack.orders.OrdersFragment
import com.example.logitrack.profile.ProfileFragment
import com.example.logitrack.utils.Constants

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var rolId: Int = Constants.ROL_CLIENT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE)
        rolId = prefs.getInt(Constants.PREF_ROL_ID, Constants.ROL_CLIENT)

        loadFragment(getHomeFragment())

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.nav_inici     -> getHomeFragment()
                R.id.nav_orders    -> getOrdersFragment()
                R.id.nav_documents -> DocumentsFragment()
                R.id.nav_historial -> HistorialFragment()
                R.id.nav_profile   -> ProfileFragment()
                else               -> getHomeFragment()
            }
            loadFragment(fragment)
            true
        }
    }

    private fun getHomeFragment(): Fragment =
        if (rolId == Constants.ROL_AGENT || rolId == Constants.ROL_ADMIN) AgentHomeFragment() else HomeFragment()

    private fun getOrdersFragment(): Fragment =
        if (rolId == Constants.ROL_AGENT || rolId == Constants.ROL_ADMIN) AgentOrdersFragment() else OrdersFragment()

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameContainer, fragment)
            .commit()
    }
}