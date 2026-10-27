package com.videoplayer.hdvideos.vrplayer1.cinema

import android.view.MotionEvent

class TheatreTouchController {

    // ── Public state (degrees) ────────────────────────────────────────────────

    /** Horizontal rotation in degrees. Positive = look right. */
    @Volatile var yaw: Float = 0f
        private set

    /** Vertical rotation in degrees. Clamped to ±80°. */
    @Volatile var pitch: Float = 0f
        private set

    // ── Config ────────────────────────────────────────────────────────────────

    /**
     * Degrees rotated per pixel dragged.
     * Increase for faster rotation, decrease for slower.
     * 0.15f feels natural on most screen sizes.
     */
    var sensitivity: Float = 0.15f

    private val PITCH_CLAMP_DEG = 80f

    // ── Internal state ────────────────────────────────────────────────────────

    private var lastX = 0f
    private var lastY = 0f
    private var isDragging = false

    // ── Public API ────────────────────────────────────────────────────────────

    /** Reset to forward-facing position. */
    fun reset() {
        yaw = 0f
        pitch = 0f
        isDragging = false
    }

    /**
     * Feed raw MotionEvents from the Activity's onTouchEvent.
     * @return true if the event was consumed.
     */
    fun onTouchEvent(event: MotionEvent): Boolean {
        return when (event.actionMasked) {

            MotionEvent.ACTION_DOWN,
            MotionEvent.ACTION_POINTER_DOWN -> {
                lastX = event.x
                lastY = event.y
                isDragging = true
                true
            }

            MotionEvent.ACTION_MOVE -> {
                if (!isDragging) return false

                val dx = event.x - lastX
                val dy = event.y - lastY
                lastX = event.x
                lastY = event.y

                // Drag right → look right (subtract because screen-X grows rightward)
                yaw -= dx * sensitivity

                // Drag down → look down
                pitch = (pitch + dy * sensitivity)
                    .coerceIn(-PITCH_CLAMP_DEG, PITCH_CLAMP_DEG)

                true
            }

            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> {
                isDragging = false
                true
            }

            else -> false
        }
    }
}