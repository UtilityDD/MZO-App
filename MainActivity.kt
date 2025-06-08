package com.utilitydd.mzoreports

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.annotation.SuppressLint
import android.content.Intent
import android.webkit.WebViewClient
import androidx.appcompat.app.AlertDialog
import com.utilitydd.mzoreports.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val websiteUrl = "https://utilitydd.github.io/mzo_report/"

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Handle system UI insets
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            // Apply top padding to status bar spacer
            binding.statusBarSpacer.layoutParams.height = systemBars.top
            binding.statusBarSpacer.requestLayout()

            // Apply bottom padding to FAB
            val fabLayoutParams = binding.logoutFab.layoutParams as androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
            fabLayoutParams.bottomMargin = 16 + systemBars.bottom
            binding.logoutFab.layoutParams = fabLayoutParams

            insets
        }

        // Configure WebView
        binding.webView.apply {
            webViewClient = WebViewClient() // Keeps navigation inside the app
            settings.javaScriptEnabled = true
            loadUrl(websiteUrl)
        }

        // Configure Logout Button with confirmation
        binding.logoutFab.setOnClickListener {
            showLogoutConfirmationDialog()
        }
    }

    private fun showLogoutConfirmationDialog() {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Confirm Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { _, _ ->
                performLogout()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .setIcon(android.R.drawable.ic_dialog_alert)
            .create()

        dialog.show()

        // Change icon color to red
        val iconImageView = dialog.findViewById<android.widget.ImageView>(android.R.id.icon)
        iconImageView?.setColorFilter(
            android.graphics.Color.RED,
            android.graphics.PorterDuff.Mode.SRC_IN
        )
    }

    private fun performLogout() {
        val intent = Intent(this, LoginActivity::class.java)
        // Clears the activity stack and starts a new task for the login screen
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish() // Explicitly finish this activity
    }

    // Handle back press to navigate within the WebView
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
