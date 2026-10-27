package com.videoplayer.hdvideos.vrplayer1.Detail.utils

import android.view.MotionEvent
import kotlin.math.abs

/**
 * Translates finger-drag gestures on the player surface into yaw/pitch deltas.
 *
 * Wire up [onTouchEvent] from the activity's window.  The accumulated [yaw]
 * and [pitch] values can be read each frame and added to any offset from
 * [GyroscopeController] when BOTH mode is active.
 */
//class TouchController {
//
//    // ── State ─────────────────────────────────────────────────────────────────
//
//    /** Total horizontal angle (radians). */
//    var yaw: Float = 0f
//        private set
//
//    /** Total vertical angle (radians), clamped to ±80°. */
//    var pitch: Float = 0f
//        private set
//
//    private var lastX = 0f
//    private var lastY = 0f
//    private var isDragging = false
//
//    /** Sensitivity: pixels-per-radian. Lower value = faster rotation. */
//    var sensitivity: Float = 600f
//
//    private val PITCH_CLAMP = Math.toRadians(80.0).toFloat()
//
//    // ── Public API ────────────────────────────────────────────────────────────
//
//    /** Reset accumulated angles (e.g. when re-centring). */
//    fun reset() {
//        yaw   = 0f
//        pitch = 0f
//    }
//
//    /**
//     * Feed raw touch events from the activity.
//     * Returns true if the event was consumed.
//     */
//    fun onTouchEvent(event: MotionEvent): Boolean {
//        return when (event.actionMasked) {
//            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
//                lastX      = event.x
//                lastY      = event.y
//                isDragging = true
//                true
//            }
//            MotionEvent.ACTION_MOVE -> {
//                if (!isDragging) return false
//                val dx = event.x - lastX
//                val dy = event.y - lastY
//                lastX = event.x
//                lastY = event.y
//
//                yaw   -= dx / sensitivity          // drag right → look right
//                pitch  = (pitch + dy / sensitivity) // drag down  → look down
//                    .coerceIn(-PITCH_CLAMP, PITCH_CLAMP)
//                true
//            }
//            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
//                isDragging = false
//                true
//            }
//            else -> false
//        }
//    }
//}


class TouchController {

    // ── Output (what the renderer reads each frame) ───────────────────────────
    var yaw:   Float = 0f; private set
    var pitch: Float = 0f; private set

    // ── Velocity for fling ────────────────────────────────────────────────────
    private var velYaw   = 0f
    private var velPitch = 0f

    // ── Previous touch position ───────────────────────────────────────────────
    private var lastX = 0f
    private var lastY = 0f
    private var isTracking = false

    // ── Tuning ────────────────────────────────────────────────────────────────

    /** How many radians the camera moves per pixel of drag. */
    private val SENSITIVITY_X = 0.003f   // yaw   (left/right)
    private val SENSITIVITY_Y = 0.003f   // pitch (up/down)

    /**
     * Dead-zone in pixels on first touch-move — prevents accidental nudges
     * when the user taps or quickly lifts their finger.
     */
    private val DEAD_ZONE_PX = 6f

    /** Fling friction per frame (called at ~60 fps).  0.88 → decays in ~20 frames */
    private val FRICTION = 0.88f

    /** Velocity below this is clamped to zero (avoids never-ending micro-drift). */
    private val VEL_STOP_THRESH = 0.0001f

    /** Max velocity cap (prevents accidental flick from moving too far). */
    private val VEL_MAX = 0.12f

    /** Pitch hard limits in radians (~85°). */
    private val PITCH_MAX =  Math.toRadians(85.0).toFloat()
    private val PITCH_MIN = -Math.toRadians(85.0).toFloat()

    // ─────────────────────────────────────────────────────────────────────────
    //  Public API
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Feed raw MotionEvents from Activity.onTouchEvent or a GestureDetector.
     * Returns true if the event was consumed.
     */
    fun onTouchEvent(event: MotionEvent): Boolean {
        return when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                lastX      = event.x
                lastY      = event.y
                isTracking = true
                velYaw     = 0f    // kill any in-progress fling on new touch
                velPitch   = 0f
                true
            }
            MotionEvent.ACTION_MOVE -> {
                if (!isTracking) return false
                val dx = event.x - lastX
                val dy = event.y - lastY

                // Dead-zone: ignore the very first tiny movement
                if (!isTracking || (abs(dx) < DEAD_ZONE_PX && abs(dy) < DEAD_ZONE_PX)) {
                    return true
                }

                lastX = event.x
                lastY = event.y

                // Compute this frame's velocity contribution
                val frameVelYaw   = -dx * SENSITIVITY_X    // invert X so drag-right = look right
                val frameVelPitch =  dy * SENSITIVITY_Y    // drag-down = look down

                // Blend with previous velocity for smoothness
                velYaw   = lerp(velYaw,   frameVelYaw,   0.6f).coerceIn(-VEL_MAX, VEL_MAX)
                velPitch = lerp(velPitch, frameVelPitch, 0.6f).coerceIn(-VEL_MAX, VEL_MAX)

                applyVelocity()
                true
            }
            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> {
                isTracking = false
                // Keep current velocity for fling — it decays via update()
                true
            }
            else -> false
        }
    }

    /**
     * Call once per frame (e.g. from your 16 ms frameRunnable) to apply fling deceleration.
     * This is separate from onTouchEvent so fling runs even when no touch events arrive.
     */
    fun update() {
        if (isTracking) return   // don't decelerate mid-drag

        if (abs(velYaw) > VEL_STOP_THRESH || abs(velPitch) > VEL_STOP_THRESH) {
            velYaw   *= FRICTION
            velPitch *= FRICTION
            if (abs(velYaw)   < VEL_STOP_THRESH) velYaw   = 0f
            if (abs(velPitch) < VEL_STOP_THRESH) velPitch = 0f
            applyVelocity()
        }
    }

    /** Snap both axes back to dead-centre (call from "re-centre" button). */
    fun reset() {
        yaw      = 0f
        pitch    = 0f
        velYaw   = 0f
        velPitch = 0f
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Private helpers
    // ─────────────────────────────────────────────────────────────────────────

    private fun applyVelocity() {
        yaw    += velYaw
        pitch   = (pitch + velPitch).coerceIn(PITCH_MIN, PITCH_MAX)
    }

    private fun lerp(a: Float, b: Float, t: Float) = a + (b - a) * t
}
