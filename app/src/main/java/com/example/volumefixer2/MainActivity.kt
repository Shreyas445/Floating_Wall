package com.example.volumefixer2

import android.content.Context
import android.content.Intent
import android.media.AudioManager
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
        val spinner1Delay = findViewById<Spinner>(R.id.spinner1TapDelay)
        val spinner2 = findViewById<Spinner>(R.id.spinner2Tap)
        val spinner2Delay = findViewById<Spinner>(R.id.spinner2TapDelay)
        val spinner3 = findViewById<Spinner>(R.id.spinner3Tap)
        val spinner3Delay = findViewById<Spinner>(R.id.spinner3TapDelay)
        val btnOpenSettings = findViewById<Button>(R.id.btnOpenSettings)
        val btnBattery = findViewById<Button>(R.id.btnBattery)
        val menuIcon = findViewById<TextView>(R.id.menuIcon)

        spinner1.setSelection(prefs.getInt("action_1", 1))
        spinner2.setSelection(prefs.getInt("action_2", 2))
        spinner3.setSelection(prefs.getInt("action_3", 0))

        spinner1Delay.setSelection(prefs.getInt("delay_1", 0))
        spinner2Delay.setSelection(prefs.getInt("delay_2", 0))
        spinner3Delay.setSelection(prefs.getInt("delay_3", 0))

        val actionListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                with(prefs.edit()) {
                    when (parent?.id) {
                        R.id.spinner1Tap -> { putInt("action_1", position); if (position == 3) showAppPickerDialog(1) }
                        R.id.spinner2Tap -> { putInt("action_2", position); if (position == 3) showAppPickerDialog(2) }
                        R.id.spinner3Tap -> { putInt("action_3", position); if (position == 3) showAppPickerDialog(3) }
                    }
                    apply()
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        val delayListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                with(prefs.edit()) {
                    when (parent?.id) {
                        R.id.spinner1TapDelay -> putInt("delay_1", position)
                        R.id.spinner2TapDelay -> putInt("delay_2", position)
                        R.id.spinner3TapDelay -> putInt("delay_3", position)
                    }
                    apply()
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinner1.onItemSelectedListener = actionListener
        spinner2.onItemSelectedListener = actionListener
        spinner3.onItemSelectedListener = actionListener
        spinner1Delay.onItemSelectedListener = delayListener
        spinner2Delay.onItemSelectedListener = delayListener
        spinner3Delay.onItemSelectedListener = delayListener

        // The Updated Menu Logic
        menuIcon.setOnClickListener { view ->
            val popup = PopupMenu(this, view)
            popup.menu.add(0, 1, 0, "Default Volume")
            popup.menu.add(0, 2, 0, "How to Use")
            popup.menu.add(0, 3, 0, "Privacy Policy")

            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    1 -> showDefaultVolumeDialog()
                    2 -> showHowToUseDialog()
                    3 -> showPrivacyPolicyDialog()
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

    // THE NEW DEFAULT VOLUME SELECTOR
    private fun showDefaultVolumeDialog() {
        val streamNames = arrayOf("Media Volume", "Ringtone", "Notifications", "Alarms", "System Volume", "In-Call Volume")
        val streamValues = intArrayOf(
            AudioManager.STREAM_MUSIC,
            AudioManager.STREAM_RING,
            AudioManager.STREAM_NOTIFICATION,
            AudioManager.STREAM_ALARM,
            AudioManager.STREAM_SYSTEM,
            AudioManager.STREAM_VOICE_CALL
        )

        val prefs = getSharedPreferences("FloatingWallPrefs", Context.MODE_PRIVATE)
        val currentSavedStream = prefs.getInt("default_volume_stream", AudioManager.STREAM_MUSIC)

        // Find which radio button should be checked based on saved data
        val currentIndex = streamValues.indexOf(currentSavedStream).takeIf { it >= 0 } ?: 0

        MaterialAlertDialogBuilder(this)
            .setTitle("Set Default Volume")
            .setSingleChoiceItems(streamNames, currentIndex) { dialog, which ->
                prefs.edit().putInt("default_volume_stream", streamValues[which]).apply()
                Toast.makeText(this, "Default set to ${streamNames[which]}", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showAppPickerDialog(tapCount: Int) {
        val pm = packageManager
        val intent = Intent(Intent.ACTION_MAIN, null).apply { addCategory(Intent.CATEGORY_LAUNCHER) }
        val resolveInfos = pm.queryIntentActivities(intent, 0)

        resolveInfos.sortBy { it.loadLabel(pm).toString().lowercase() }

        val appNames = resolveInfos.map { it.loadLabel(pm).toString() }.toTypedArray()
        val appPackages = resolveInfos.map { it.activityInfo.packageName }.toTypedArray()

        MaterialAlertDialogBuilder(this)
            .setTitle("Select App to Launch")
            .setItems(appNames) { _, which ->
                val selectedPackage = appPackages[which]
                getSharedPreferences("FloatingWallPrefs", Context.MODE_PRIVATE)
                    .edit()
                    .putString("package_$tapCount", selectedPackage)
                    .apply()
                Toast.makeText(this, "Mapped to: ${appNames[which]}", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showHowToUseDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("How to Use")
            .setMessage("1. Enable Accessibility to reveal the button.\n2. Configure actions and delays for your taps.\n3. Optimize Battery to run seamlessly.")
            .setPositiveButton("Got it", null)
            .show()
    }

    private fun showPrivacyPolicyDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Privacy Policy")
            .setMessage("Floating Wall operates entirely locally on your device. We do not collect, store, or transmit any data.")
            .setPositiveButton("Close", null)
            .show()
    }
}