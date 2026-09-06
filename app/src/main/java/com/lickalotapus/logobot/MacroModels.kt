package com.lickalotapus.logobot

sealed class MacroAction {
    data class Tap(val point: ControllerPoint, val delayAfterMs: Long = 250L) : MacroAction()
    data class Stick(val point: ControllerPoint, val dx: Float, val dy: Float, val durationMs: Long, val delayAfterMs: Long = 250L) : MacroAction()
    data class Wait(val ms: Long) : MacroAction()
}

data class LogoLayerSpec(
    val index: Int,
    val name: String,
    val role: String,
    val colorHex: String,
    val notes: String
)
