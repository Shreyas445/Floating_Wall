package com.example.volumefixer2

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.PopupMenu
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val prefs = getSharedPreferences("FloatingWallPrefs", Context.MODE_PRIVATE)
        val spinner1 = findViewById<Spinner>(R.id.spinner1Tap)
        val spinner2 = findViewById<Spinner>(R.id.spinner2Tap)
        val spinner3 = findViewById<Spinner>(R.id.spinner3Tap)
        val btnOpenSettings = findViewById<Button>(R.id.btnOpenSettings)
        val btnBattery = findViewById<Button>(R.id.btnBattery)
        val menuIcon = findViewById<TextView>(R.id.menuIcon)

        // Load Preferences
        spinner1.setSelection(prefs.getInt("action_1", 1))
        spinner2.setSelection(prefs.getInt("action_2", 2))
        spinner3.setSelection(prefs.getInt("action_3", 0))

        val listener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                with(prefs.edit()) {
                    when (parent?.id) {
                        R.id.spinner1Tap -> putInt("action_1", position)
                        R.id.spinner2Tap -> putInt("action_2", position)
                        R.id.spinner3Tap -> putInt("action_3", position)
                    }
                    apply()
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinner1.onItemSelectedListener = listener
        spinner2.onItemSelectedListener = listener
        spinner3.onItemSelectedListener = listener

        // Dropdown Menu Logic
        menuIcon.setOnClickListener { view ->
            val popup = PopupMenu(this, view)
            popup.menu.add(0, 1, 0, "How to Use")
            popup.menu.add(0, 2, 0, "Privacy Policy")

            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    1 -> showHowToUseDialog()
                    2 -> showPrivacyPolicyDialog()
                }
                true
            }
            popup.show()
        }

        btnOpenSettings.setOnClickListener {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }

        btnBattery.setOnClickListener {
            val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
            } else {
                Toast.makeText(this, "Battery is already optimized!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showHowToUseDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("How to Use")
            .setMessage("1. Tap 'Enable Accessibility' and turn on the Floating Wall shortcut to reveal the green button.\n\n2. Assign actions to 1, 2, or 3 taps using the dropdowns.\n\n3. Tap 'Optimize Battery' and allow the exemption so Samsung doesn't close the app in the background.")
            .setPositiveButton("Got it", null)
            .show()
    }

    private fun showPrivacyPolicyDialog() {
        val policyText = """
            Privacy Policy for Floating Wall
            
            1. Data Collection
            Floating Wall operates entirely locally on your device. We do not collect, store, or transmit any personal data, usage statistics, or analytics.
            
            2. Accessibility Service
            This app requires the Android Accessibility Service to intercept on-screen button taps and execute system actions (like opening the volume panel or taking screenshots). It does NOT monitor your screen content, read your typing, or track your behavior across other apps.
            
            3. Open Source
            The source code for this application is public. You can verify all functionality and data practices independently.
        """.trimIndent()

        MaterialAlertDialogBuilder(this)
            .setTitle("Privacy Policy")
            .setMessage(policyText)
            .setPositiveButton("Close", null)
            .show()
    }
}