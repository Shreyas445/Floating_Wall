package com.example.volumefixer2

import android.accessibilityservice.AccessibilityButtonController
import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast

class VolumeGestureService : AccessibilityService() {

    private lateinit var audioManager: AudioManager
    private lateinit var vibrator: Vibrator
    private var tapCount = 0
    private val handler = Handler(Looper.getMainLooper())

    private val tapRunnable = Runnable {
        val currentTaps = tapCount
        tapCount = 0

        val prefs = getSharedPreferences("FloatingWallPrefs", Context.MODE_PRIVATE)
        val delayPos = prefs.getInt("delay_$currentTaps", 0)
        val delayTimeMs = when (delayPos) {
            1 -> 3000L
            2 -> 7000L
            3 -> 15000L
            else -> 0L
        }

        handler.postDelayed({
            executeAction(currentTaps)
        }, delayTimeMs)
    }

    private val buttonCallback = object : AccessibilityButtonController.AccessibilityButtonCallback() {
        override fun onClicked(controller: AccessibilityButtonController) {
            vibrator.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE))

            tapCount++
            handler.removeCallbacks(tapRunnable)

            if (tapCount == 3) {
                handler.post(tapRunnable)
            } else {
                handler.postDelayed(tapRunnable, 300)
            }
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        accessibilityButtonController.registerAccessibilityButtonCallback(buttonCallback)
    }

    private fun executeAction(taps: Int) {
        val prefs = getSharedPreferences("FloatingWallPrefs", Context.MODE_PRIVATE)
        val actionId = prefs.getInt("action_$taps", 0)

        when (actionId) {
            1 -> {
                // THE NEW CONTEXT & DEFAULT VOLUME LOGIC
                val mode = audioManager.mode
                val defaultStream = prefs.getInt("default_volume_stream", AudioManager.STREAM_MUSIC)

                val targetStream = when {
                    // 1. Check if in a phone call or VoIP call
                    mode == AudioManager.MODE_IN_CALL || mode == AudioManager.MODE_IN_COMMUNICATION -> AudioManager.STREAM_VOICE_CALL
                    // 2. Check if Spotify, YouTube, etc., is playing
                    audioManager.isMusicActive -> AudioManager.STREAM_MUSIC
                    // 3. Nothing is happening? Use the user's default choice
                    else -> defaultStream
                }

                audioManager.adjustStreamVolume(targetStream, AudioManager.ADJUST_SAME, AudioManager.FLAG_SHOW_UI)
            }
            2 -> {
                performGlobalAction(GLOBAL_ACTION_TAKE_SCREENSHOT)
            }
            3 -> {
                val packageToLaunch = prefs.getString("package_$taps", "")
                if (!packageToLaunch.isNullOrEmpty()) {
                    val launchIntent = packageManager.getLaunchIntentForPackage(packageToLaunch)
                    if (launchIntent != null) {
                        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(launchIntent)
                    } else {
                        Toast.makeText(this, "App not found on device.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}
    override fun onInterrupt() {}

    override fun onDestroy() {
        super.onDestroy()
        accessibilityButtonController.unregisterAccessibilityButtonCallback(buttonCallback)
    }
}