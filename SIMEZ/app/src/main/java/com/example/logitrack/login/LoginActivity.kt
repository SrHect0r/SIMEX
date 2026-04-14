package com.example.logitrack.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import android.widget.TextView
import com.example.logitrack.MainActivity
import com.example.logitrack.R
import com.example.logitrack.data.LoginRequest
import com.example.logitrack.network.RetrofitClient
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnRoleClient = findViewById<TextView>(R.id.btnRoleClient)
        val btnRoleEmployee = findViewById<TextView>(R.id.btnRoleEmployee)

        var selectedRole = "client" // por defecto

        btnRoleClient.setOnClickListener {
            selectedRole = "client"
            btnRoleClient.setBackgroundResource(android.R.color.white)
            btnRoleClient.setTextColor(getColor(R.color.brand_primary))
            btnRoleClient.setTypeface(null, android.graphics.Typeface.BOLD)
            
            btnRoleEmployee.setBackgroundResource(android.R.color.transparent)
            btnRoleEmployee.setTextColor(getColor(R.color.gray_500))
            btnRoleEmployee.setTypeface(null, android.graphics.Typeface.NORMAL)
            
            etEmail.setHint("client@empresa.cat")
        }

        btnRoleEmployee.setOnClickListener {
            selectedRole = "employee"
            btnRoleEmployee.setBackgroundResource(android.R.color.white)
            btnRoleEmployee.setTextColor(getColor(R.color.brand_primary))
            btnRoleEmployee.setTypeface(null, android.graphics.Typeface.BOLD)
            
            btnRoleClient.setBackgroundResource(android.R.color.transparent)
            btnRoleClient.setTextColor(getColor(R.color.gray_500))
            btnRoleClient.setTypeface(null, android.graphics.Typeface.NORMAL)
            
            etEmail.setHint("empleat@logitrack.cat")
        }

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Omple tots els camps", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.instance.login(LoginRequest(email, password))
                    if (response.isSuccessful) {
                        val usuari = response.body()!!
                        val prefs = getSharedPreferences("logitrack", MODE_PRIVATE)
                        prefs.edit()
                            .putInt("userId", usuari.id)
                            .putString("userName", usuari.nom)
                            .putString("userCognoms", usuari.cognoms)
                            .putString("userCorreu", usuari.correu)
                            .putInt("rolId", usuari.rolId)
                            .apply()

                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "Credencials incorrectes", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@LoginActivity, e.message ?: "Error desconegut", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}