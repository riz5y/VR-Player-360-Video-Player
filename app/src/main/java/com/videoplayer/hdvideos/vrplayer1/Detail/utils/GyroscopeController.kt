package com.videoplayer.hdvideos.vrplayer1.Detail.utils

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.view.Surface
import android.view.WindowManager
import kotlin.math.abs
import kotlin.math.sqrt


//class GyroscopeController(context: Context) : SensorEventListener {
//
//    private val sensorManager =
//        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
//
//    private val gyroscope: Sensor? =
//        sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
//
//    // Detect screen orientation to fix axis mapping
//    private val windowManager =
//        context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
//
//    // ── State ─────────────────────────────────────────────────────────────────
//
//    var yaw: Float = 0f
//        private set
//
//    var pitch: Float = 0f
//        private set
//
//    // Full 3×3 rotation matrix — more accurate than simple angle integration
//    private val rotationMatrix = FloatArray(9).also {
//        // Identity matrix as starting point
//        it[0] = 1f; it[4] = 1f; it[8] = 1f
//    }
//
//    private var lastTimestamp = 0L
//    private val PITCH_CLAMP = Math.toRadians(80.0).toFloat()
//    private val DEAD_ZONE   = 0.005f   // tighter dead zone
//
//    val isAvailable: Boolean get() = gyroscope != null
//
//    // ── Public API ────────────────────────────────────────────────────────────
//
//    fun start() {
//        lastTimestamp = 0L
//        gyroscope?.let {
//            sensorManager.registerListener(
//                this, it,
//                SensorManager.SENSOR_DELAY_GAME   // ~50Hz, good balance
//            )
//        }
//    }
//
//    fun stop() {
//        sensorManager.unregisterListener(this)
//    }
//
//    fun reset() {
//        yaw   = 0f
//        pitch = 0f
//        lastTimestamp = 0L
//        // Reset rotation matrix to identity
//        rotationMatrix.fill(0f)
//        rotationMatrix[0] = 1f
//        rotationMatrix[4] = 1f
//        rotationMatrix[8] = 1f
//    }
//
//    // ── SensorEventListener ───────────────────────────────────────────────────
//
//    override fun onSensorChanged(event: SensorEvent) {
//        if (event.sensor.type != Sensor.TYPE_GYROSCOPE) return
//
//        if (lastTimestamp == 0L) {
//            lastTimestamp = event.timestamp
//            return
//        }
//
//        val dt = (event.timestamp - lastTimestamp) * 1e-9f
//        lastTimestamp = event.timestamp
//
//        if (dt > 0.1f) return
//
//        val gx = event.values[0]
//        val gy = event.values[1]
//
//        val ax = if (abs(gx) > DEAD_ZONE) gx else 0f
//        val ay = if (abs(gy) > DEAD_ZONE) gy else 0f
//
//        val rotation = windowManager.defaultDisplay.rotation
//
//        // ── FIXED: swap yaw/pitch assignment ─────────────────────────────────
//        val (deltaYaw, deltaPitch) = when (rotation) {
//            Surface.ROTATION_90  -> Pair( ax * dt, ay * dt)   // X → yaw, Y → pitch
//            Surface.ROTATION_270 -> Pair(-ax * dt, -ay * dt)
//            else                 -> Pair( ax * dt, ay * dt)
//        }
//
//        yaw   += deltaYaw
//        pitch  = (pitch + deltaPitch).coerceIn(-PITCH_CLAMP, PITCH_CLAMP)
//    }
//    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
//}




/**
 * GyroscopeController — Dedicated to GalleryPlayerActivity.
 *
 * Features:
 *  • Quaternion integration (no gimbal lock, smooth on ALL axes)
 *  • Low-pass pre-filter removes sensor jitter before integration
 *  • Adaptive smoothing: fast response to large moves, heavy damping for micro-jitter
 *  • Auto-centres on [start] / [reset] — first frame is always dead-ahead
 *  • Drift correction: slowly nudges pitch back to horizon when the user is still
 */
class GyroscopeController(context: Context) : SensorEventListener {

    // ── Sensor plumbing ───────────────────────────────────────────────────────
    private val sensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val gyroSensor: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

    // ── Quaternion state ──────────────────────────────────────────────────────
    private val qCurrent   = floatArrayOf(1f, 0f, 0f, 0f) // current orientation
    private val qReference = floatArrayOf(1f, 0f, 0f, 0f) // centre reference
    private val qRelative  = floatArrayOf(1f, 0f, 0f, 0f) // relative = ref⁻¹ ⊗ current

    // ── Filtered gyro ─────────────────────────────────────────────────────────
    private val filteredOmega = floatArrayOf(0f, 0f, 0f)

    // ── Smoothed output ───────────────────────────────────────────────────────
    private var smoothYaw   = 0f
    private var smoothPitch = 0f

    var yaw:   Float = 0f; private set
    var pitch: Float = 0f; private set

    // ── Timing ────────────────────────────────────────────────────────────────
    private var lastTimestampNs = 0L
    private var isFirstEvent    = true

    // ── Tuning ────────────────────────────────────────────────────────────────
    private val GYRO_LP_ALPHA    = 0.75f   // low-pass on raw sensor (0=max smooth, 1=raw)
    private val ALPHA_SLOW       = 0.05f   // output lerp when nearly still  → kills jitter
    private val ALPHA_FAST       = 0.40f   // output lerp during fast motion → stays responsive
    private val FAST_MOVE_THRESH = 0.015f  // rad/frame boundary between slow/fast alpha
    private val DRIFT_CORRECTION = 0.0004f // gentle pitch-toward-zero when still
    private val STILL_THRESH     = 0.008f  // rad/frame — below this = "user is still"

    // ─────────────────────────────────────────────────────────────────────────
    //  Public API
    // ─────────────────────────────────────────────────────────────────────────

    /** Register sensor. Resets centre so first frame is dead-ahead. */
//    fun start() {
//        reset()
//        gyroSensor?.let {
//            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_FASTEST)
//        }
//    }
    fun start() {
        reset()
        gyroSensor?.let {
            sensorManager.registerListener(
                this, it,
                SensorManager.SENSOR_DELAY_GAME  // ~50Hz — no special permission needed
            )
        }
    }

    /** Unregister sensor. */
    fun stop() {
        sensorManager.unregisterListener(this)
        isFirstEvent = true
    }

    /** Re-centres to the current physical orientation (call from "re-centre" button). */
    fun reset() {
        qReference[0] = qCurrent[0]
        qReference[1] = qCurrent[1]
        qReference[2] = qCurrent[2]
        qReference[3] = qCurrent[3]

        smoothYaw   = 0f
        smoothPitch = 0f
        yaw         = 0f
        pitch       = 0f
        filteredOmega[0] = 0f
        filteredOmega[1] = 0f
        filteredOmega[2] = 0f
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  SensorEventListener
    // ─────────────────────────────────────────────────────────────────────────

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type != Sensor.TYPE_GYROSCOPE) return

        val nowNs = event.timestamp
        if (isFirstEvent) {
            lastTimestampNs = nowNs
            isFirstEvent    = false
            // Snapshot the very first physical orientation as centre
            qReference[0] = qCurrent[0]; qReference[1] = qCurrent[1]
            qReference[2] = qCurrent[2]; qReference[3] = qCurrent[3]
            return
        }

        val dtNs = nowNs - lastTimestampNs
        lastTimestampNs = nowNs
        if (dtNs <= 0L || dtNs > 100_000_000L) return  // skip stale / huge gaps
        val dt = dtNs * 1e-9f

        // ── Low-pass filter on raw gyro ───────────────────────────────────────
        filteredOmega[0] = lerp(filteredOmega[0], event.values[0], GYRO_LP_ALPHA)
        filteredOmega[1] = lerp(filteredOmega[1], event.values[1], GYRO_LP_ALPHA)
        filteredOmega[2] = lerp(filteredOmega[2], event.values[2], GYRO_LP_ALPHA)

        // ── Quaternion integration via axis-angle ─────────────────────────────
        val omegaMag = sqrt(
            filteredOmega[0] * filteredOmega[0] +
                    filteredOmega[1] * filteredOmega[1] +
                    filteredOmega[2] * filteredOmega[2]
        )

        if (omegaMag > 1e-6f) {
            val angle   = omegaMag * dt
            val sinHalf = Math.sin((angle / 2.0)).toFloat()
            val cosHalf = Math.cos((angle / 2.0)).toFloat()
            val nx = filteredOmega[0] / omegaMag
            val ny = filteredOmega[1] / omegaMag
            val nz = filteredOmega[2] / omegaMag

            val dw = cosHalf
            val dx = nx * sinHalf
            val dy = ny * sinHalf
            val dz = nz * sinHalf

            // qCurrent = qCurrent ⊗ dq  (body-frame, right-multiply)
            val cw = qCurrent[0]; val cx = qCurrent[1]
            val cy = qCurrent[2]; val cz = qCurrent[3]

            qCurrent[0] = cw*dw - cx*dx - cy*dy - cz*dz
            qCurrent[1] = cw*dx + cx*dw + cy*dz - cz*dy
            qCurrent[2] = cw*dy - cx*dz + cy*dw + cz*dx
            qCurrent[3] = cw*dz + cx*dy - cy*dx + cz*dw
            normalise(qCurrent)
        }

        // ── Relative quaternion = qRef⁻¹ ⊗ qCurrent ─────────────────────────
        val rw =  qReference[0]; val rx = -qReference[1]
        val ry = -qReference[2]; val rz = -qReference[3]
        val cw = qCurrent[0];    val cx =  qCurrent[1]
        val cy = qCurrent[2];    val cz =  qCurrent[3]

        qRelative[0] = rw*cw - rx*cx - ry*cy - rz*cz
        qRelative[1] = rw*cx + rx*cw + ry*cz - rz*cy
        qRelative[2] = rw*cy - rx*cz + ry*cw + rz*cx
        qRelative[3] = rw*cz + rx*cy - ry*cx + rz*cw

        // ── Euler extraction ──────────────────────────────────────────────────
        val qw = qRelative[0]; val qx = qRelative[1]
        val qy = qRelative[2]; val qz = qRelative[3]

        // Yaw  = rotation about Y  (left-right head turn)
        val rawYaw = Math.atan2(
            (2.0 * (qw*qy + qx*qz)),
            (1.0 - 2.0 * (qy*qy + qz*qz))
        ).toFloat()

        // Pitch = rotation about X (up-down tilt)
        val sinP = (2.0 * (qw*qx - qy*qz)).coerceIn(-1.0, 1.0)
        val rawPitch = Math.asin(sinP).toFloat()

        // ── Adaptive output smoothing ─────────────────────────────────────────
        val maxDelta = maxOf(abs(rawYaw - smoothYaw), abs(rawPitch - smoothPitch))
        val alpha    = if (maxDelta > FAST_MOVE_THRESH) ALPHA_FAST else ALPHA_SLOW

        smoothYaw   = lerp(smoothYaw,   rawYaw,   alpha)
        smoothPitch = lerp(smoothPitch, rawPitch, alpha)

        // ── Gentle pitch drift correction (auto-horizon) ──────────────────────
        if (maxDelta < STILL_THRESH) {
            smoothPitch *= (1f - DRIFT_CORRECTION)
        }

        yaw   = smoothYaw
        pitch = smoothPitch
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) = Unit

    // ─────────────────────────────────────────────────────────────────────────
    //  Helpers
    // ─────────────────────────────────────────────────────────────────────────

    private fun normalise(q: FloatArray) {
        val len = sqrt(q[0]*q[0] + q[1]*q[1] + q[2]*q[2] + q[3]*q[3])
        if (len > 1e-6f) { q[0] /= len; q[1] /= len; q[2] /= len; q[3] /= len }
    }

    private fun lerp(a: Float, b: Float, t: Float) = a + (b - a) * t
}