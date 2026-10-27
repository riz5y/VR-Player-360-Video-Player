package com.videoplayer.hdvideos.vrplayer1.cinema

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.abs

class TheatreGyroscopeController(context: Context) : SensorEventListener {

    private val sensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val gyroscope: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

    @Volatile var yaw: Float = 0f
        private set

    @Volatile var pitch: Float = 0f
        private set

    private val DEAD_ZONE = 0.02f
    private val PITCH_LIMIT = 80f

    private val YAW_SENS   = 1.5f
    private val PITCH_SENS = 1.2f
    private val SMOOTHING  = 0.1f

    private var lastTimestamp = 0L
    private var smoothYaw   = 0f
    private var smoothPitch = 0f

    val isAvailable: Boolean get() = gyroscope != null

    fun start() {
        lastTimestamp = 0L
        gyroscope?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    fun reset() {
        yaw = 0f
        pitch = 0f
        smoothYaw = 0f
        smoothPitch = 0f
        lastTimestamp = 0L
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type != Sensor.TYPE_GYROSCOPE) return

        if (lastTimestamp == 0L) {
            lastTimestamp = event.timestamp
            return
        }

        val dt = (event.timestamp - lastTimestamp) * 1e-9f
        lastTimestamp = event.timestamp

        // ── LANDSCAPE FIX ────────────────────────────────────────────
        // In landscape mode the device is rotated 90° counter-clockwise.
        // values[1] = Y axis → becomes LEFT/RIGHT rotation (yaw).
        // values[0] = X axis → becomes UP/DOWN tilt (pitch), negated
        //             because the X axis flips direction in landscape.
        // values[2] = Z axis → is ROLL in landscape, ignore it for VR.
        val rawYaw   =  event.values[1]   // Y axis → horizontal look
        val rawPitch = -event.values[0]   // X axis → vertical look (negated!)

        val yawDelta   = if (abs(rawYaw)   > DEAD_ZONE) rawYaw   else 0f
        val pitchDelta = if (abs(rawPitch) > DEAD_ZONE) rawPitch else 0f

        val yawDeg   = Math.toDegrees((yawDelta   * dt).toDouble()).toFloat() * YAW_SENS
        val pitchDeg = Math.toDegrees((pitchDelta * dt).toDouble()).toFloat() * PITCH_SENS

        smoothYaw   += yawDeg
        smoothPitch  = (smoothPitch + pitchDeg).coerceIn(-PITCH_LIMIT, PITCH_LIMIT)

        yaw   += (smoothYaw   - yaw)   * SMOOTHING
        pitch += (smoothPitch - pitch) * SMOOTHING
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
}
