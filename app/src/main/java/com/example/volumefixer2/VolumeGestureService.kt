package com.example.volumefixer2

import android.accessibilityservice.AccessibilityButtonController
import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.accessibility.AccessibilityEvent

class VolumeGestureService : AccessibilityService() {

    private lateinit var audioManager: AudioManager
    private lateinit var vibrator: Vibrator
    private var tapCount = 0
    private val handler = Handler(Looper.getMainLooper())

    private val tapRunnable = Runnable {
        executeAction(tapCount)
        tapCount = 0
    }

    private val buttonCallback = object : AccessibilityButtonController.AccessibilityButtonCallback() {
        override fun onClicked(controller: AccessibilityButtonController) {
            // FIRE HAPTIC FEEDBACK INSTANTLY
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
        val actionId = when (taps) {
            1 -> prefs.getInt("action_1", 1)
            2 -> prefs.getInt("action_2", 2)
            3 -> prefs.getInt("action_3", 0)
            else -> 0
        }

        when (actionId) {
            1 -> audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_SAME, AudioManager.FLAG_SHOW_UI)
            2 -> performGlobalAction(GLOBAL_ACTION_TAKE_SCREENSHOT)
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}
    override fun onInterrupt() {}

    override fun onDestroy() {
        super.onDestroy()
        accessibilityButtonController.unregisterAccessibilityButtonCallback(buttonCallback)
    }
}