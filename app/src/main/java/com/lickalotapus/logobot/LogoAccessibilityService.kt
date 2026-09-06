package com.lickalotapus.logobot

import android.accessibilityservice.AccessibilityService
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast

class LogoAccessibilityService : AccessibilityService() {
    private lateinit var windowManager: WindowManager
    private lateinit var calibration: CalibrationStore
    private val handler = Handler(Looper.getMainLooper())
    private var calibrationView: CalibrationOverlayView? = null

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                ACTION_CALIBRATE -> handler.postDelayed({ showCalibrationOverlay() }, 7000L)
                ACTION_TEST -> handler.postDelayed({ runSafeTest() }, 5000L)
                ACTION_CLEAR -> {
                    calibration.clear()
                    Toast.makeText(this@LogoAccessibilityService, "Calibration cleared", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        calibration = CalibrationStore(this)
        val filter = IntentFilter().apply {
            addAction(ACTION_CALIBRATE)
            addAction(ACTION_TEST)
            addAction(ACTION_CLEAR)
        }
        if (Build.VERSION.SDK_INT >= 33) registerReceiver(receiver, filter, RECEIVER_NOT_EXPORTED)
        else @Suppress("DEPRECATION") registerReceiver(receiver, filter)
    }

    private fun showCalibrationOverlay() {
        if (calibrationView != null) return
        val view = CalibrationOverlayView(this,
            onPoint = { p, x, y -> calibration.save(p, x, y) },
            onDone = {
                calibrationView?.let { windowManager.removeView(it) }
                calibrationView = null
                Toast.makeText(this, "LogoBot calibration saved", Toast.LENGTH_LONG).show()
            }
        )
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply { gravity = Gravity.TOP or Gravity.START }
        windowManager.addView(view, params)
        calibrationView = view
    }

    private fun runSafeTest() {
        if (!calibration.isComplete()) {
            Toast.makeText(this, "Calibrate first", Toast.LENGTH_SHORT).show()
            return
        }
        GestureRunner(this, calibration).safeFineControlToggleTest {
            Toast.makeText(this, "Safe test complete", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) = Unit
    override fun onInterrupt() = Unit

    override fun onDestroy() {
        runCatching { unregisterReceiver(receiver) }
        calibrationView?.let { runCatching { windowManager.removeView(it) } }
        super.onDestroy()
    }

    companion object {
        const val ACTION_CALIBRATE = "com.lickalotapus.logobot.CALIBRATE"
        const val ACTION_TEST = "com.lickalotapus.logobot.TEST"
        const val ACTION_CLEAR = "com.lickalotapus.logobot.CLEAR"
    }
}
