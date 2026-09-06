package com.lickalotapus.logobot

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.MotionEvent
import android.view.View

class CalibrationOverlayView(
    context: Context,
    private val onPoint: (ControllerPoint, Float, Float) -> Unit,
    private val onDone: () -> Unit
) : View(context) {

    private val points = ControllerPoint.entries
    private var index = 0
    private val bannerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = 0xE6111418.toInt() }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 46f
        textAlign = Paint.Align.CENTER
    }
    private val subPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFFB7C7DD.toInt()
        textSize = 28f
        textAlign = Paint.Align.CENTER
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat()
        canvas.drawRoundRect(RectF(18f, 24f, w - 18f, 170f), 30f, 30f, bannerPaint)
        canvas.drawText("Tap the center of: ${points[index].label}", w / 2f, 86f, textPaint)
        canvas.drawText("LogoBot records the location; the tap will not reach Remote Play.", w / 2f, 132f, subPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            val p = points[index]
            onPoint(p, event.rawX, event.rawY)
            index++
            if (index >= points.size) onDone() else invalidate()
        }
        return true
    }
}
