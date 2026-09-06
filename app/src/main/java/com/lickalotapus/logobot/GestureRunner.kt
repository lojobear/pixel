package com.lickalotapus.logobot

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.graphics.PointF
import android.os.Handler
import android.os.Looper
import kotlin.math.min

class GestureRunner(
    private val service: AccessibilityService,
    private val calibration: CalibrationStore
) {
    private val handler = Handler(Looper.getMainLooper())

    fun tap(point: ControllerPoint, durationMs: Long = 70L, after: (() -> Unit)? = null): Boolean {
        val p = calibration.get(point) ?: return false
        val path = Path().apply { moveTo(p.x, p.y) }
        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, durationMs))
            .build()
        return service.dispatchGesture(gesture, object : AccessibilityService.GestureResultCallback() {
            override fun onCompleted(gestureDescription: GestureDescription?) { after?.invoke() }
        }, null)
    }

    fun stick(
        stick: ControllerPoint,
        dx: Float,
        dy: Float,
        durationMs: Long = 300L,
        after: (() -> Unit)? = null
    ): Boolean {
        require(stick == ControllerPoint.LEFT_STICK || stick == ControllerPoint.RIGHT_STICK)
        val center = calibration.get(stick) ?: return false
        val dm = service.resources.displayMetrics
        val radius = min(dm.widthPixels, dm.heightPixels) * 0.075f
        val end = PointF(center.x + dx.coerceIn(-1f, 1f) * radius, center.y + dy.coerceIn(-1f, 1f) * radius)
        val path = Path().apply {
            moveTo(center.x, center.y)
            lineTo(end.x, end.y)
        }
        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, durationMs))
            .build()
        return service.dispatchGesture(gesture, object : AccessibilityService.GestureResultCallback() {
            override fun onCompleted(gestureDescription: GestureDescription?) { after?.invoke() }
        }, null)
    }

    fun safeFineControlToggleTest(onDone: () -> Unit) {
        tap(ControllerPoint.TRIANGLE) {
            handler.postDelayed({ tap(ControllerPoint.TRIANGLE) { onDone() } }, 800L)
        }
    }
}
