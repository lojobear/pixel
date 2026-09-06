package com.lickalotapus.logobot

import android.content.Context
import android.graphics.PointF

class CalibrationStore(context: Context) {
    private val prefs = context.getSharedPreferences("controller_calibration", Context.MODE_PRIVATE)

    fun save(point: ControllerPoint, x: Float, y: Float) {
        prefs.edit().putFloat("${point.name}_x", x).putFloat("${point.name}_y", y).apply()
    }

    fun get(point: ControllerPoint): PointF? {
        val xKey = "${point.name}_x"
        val yKey = "${point.name}_y"
        if (!prefs.contains(xKey) || !prefs.contains(yKey)) return null
        return PointF(prefs.getFloat(xKey, 0f), prefs.getFloat(yKey, 0f))
    }

    fun isComplete(): Boolean = ControllerPoint.entries.all { get(it) != null }

    fun clear() = prefs.edit().clear().apply()
}
