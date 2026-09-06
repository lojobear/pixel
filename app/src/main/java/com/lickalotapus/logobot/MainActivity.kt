package com.lickalotapus.logobot

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.Settings
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

class MainActivity : Activity() {
    private lateinit var status: TextView
    private lateinit var calibrationStore: CalibrationStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        calibrationStore = CalibrationStore(this)
        buildUi()
    }

    override fun onResume() {
        super.onResume()
        updateStatus()
    }

    private fun buildUi() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(42, 46, 42, 46)
            setBackgroundColor(Color.rgb(16, 19, 24))
        }
        val title = TextView(this).apply {
            text = "Toronto Lickalotapus • LogoBot"
            textSize = 25f
            setTextColor(Color.WHITE)
        }
        val body = TextView(this).apply {
            text = "Mobile-only controller automation for PS Remote Play. First enable the LogoBot accessibility service, then calibrate against the visible Remote Play controller."
            textSize = 16f
            setTextColor(Color.rgb(190, 205, 225))
            setPadding(0, 18, 0, 24)
        }
        status = TextView(this).apply {
            textSize = 16f
            setTextColor(Color.WHITE)
            setPadding(0, 0, 0, 24)
        }

        fun button(label: String, click: () -> Unit) = Button(this).apply {
            text = label
            setOnClickListener { click() }
            val p = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            p.setMargins(0, 8, 0, 8)
            layoutParams = p
        }

        root.addView(title)
        root.addView(body)
        root.addView(status)
        root.addView(button("1. Enable LogoBot Accessibility Service") {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        })
        root.addView(button("2. Start calibration in 7 seconds") {
            sendBroadcast(Intent(LogoAccessibilityService.ACTION_CALIBRATE).setPackage(packageName))
        })
        root.addView(button("3. Safe test in 5 seconds (Triangle on → off)") {
            sendBroadcast(Intent(LogoAccessibilityService.ACTION_TEST).setPackage(packageName))
        })
        root.addView(button("Clear calibration") {
            sendBroadcast(Intent(LogoAccessibilityService.ACTION_CLEAR).setPackage(packageName))
            calibrationStore.clear()
            updateStatus()
        })

        val recipe = TextView(this).apply {
            val lines = LickalotapusRecipe.layers.joinToString("\n") { "${it.index}. ${it.name} — ${it.colorHex}" }
            text = "\nLickalotapus v1 logical recipe\n\n$lines\n\nThe next phase maps the exact MLB The Show 26 shape/menu selections to these layers."
            textSize = 14f
            setTextColor(Color.rgb(190, 205, 225))
        }
        root.addView(recipe)

        setContentView(ScrollView(this).apply { addView(root) })
    }

    private fun updateStatus() {
        val enabled = isServiceEnabled()
        val calibrated = calibrationStore.isComplete()
        status.text = "Accessibility service: ${if (enabled) "ENABLED" else "NOT ENABLED"}\nController calibration: ${if (calibrated) "COMPLETE" else "NOT COMPLETE"}"
    }

    private fun isServiceEnabled(): Boolean {
        val enabled = Settings.Secure.getString(contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES) ?: return false
        return enabled.contains("$packageName/${LogoAccessibilityService::class.java.name}") || enabled.contains(packageName)
    }
}
