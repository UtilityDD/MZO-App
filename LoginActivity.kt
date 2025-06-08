package com.utilitydd.mzoreports


import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.utilitydd.mzoreports.databinding.ActivityLoginBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val credentials = mutableMapOf<String, String>()
    private val csvUrl = "https://docs.google.com/spreadsheets/d/e/2PACX-1vRGTMdZNG8ktcf_-CWlhykS75a9RVAL5YHIN7glhcxIDOZssS_c_OmQEx_TzG8Hz1Wa1FbMIKxlHVeR/pub?gid=0&single=true&output=csv"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fetchCredentials()

        binding.loginButton.setOnClickListener {
            val username = binding.usernameEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()
            validateLogin(username, password)
        }
    }

    private fun fetchCredentials() {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val csvData = URL(csvUrl).readText()
                val lines = csvData.split("\n").drop(1) // Drop header row
                for (line in lines) {
                    val parts = line.split(",")
                    if (parts.size == 2) {
                        val user = parts[0].trim()
                        val pin = parts[1].trim()
                        credentials[user] = pin
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(applicationContext, "Failed to load credentials", Toast.LENGTH_LONG).show()
                }
            } finally {
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun validateLogin(username: String, pin: String) {
        if (username.isEmpty() || pin.isEmpty()) {
            Toast.makeText(this, "Please enter username and PIN", Toast.LENGTH_SHORT).show()
            return
        }

        if (credentials.containsKey(username) && credentials[username] == pin) {
            Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Prevents user from going back to login screen
        } else {
            Toast.makeText(this, "Invalid username or PIN", Toast.LENGTH_SHORT).show()
        }
    }
}
