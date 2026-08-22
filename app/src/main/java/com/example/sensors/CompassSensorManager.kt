package com.example.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.example.data.models.CompassData
import com.example.data.models.SensorAccuracy
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class CompassSensorManager(private val context: Context) {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager

    private val rotationVectorSensor: Sensor? =
        sensorManager?.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
    private val accelerometerSensor: Sensor? =
        sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val magneticSensor: Sensor? =
        sensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

    val hasHardwareSensors: Boolean
        get() = rotationVectorSensor != null || (accelerometerSensor != null && magneticSensor != null)

    fun getCompassDataFlow(): Flow<CompassData> = callbackFlow {
        if (sensorManager == null || !hasHardwareSensors) {
            // Emitting default safe state if no sensors found
            trySend(
                CompassData(
                    azimuthDegrees = 0f,
                    pitchDegrees = 0f,
                    rollDegrees = 0f,
                    magneticFieldUt = 48.0f,
                    accuracy = SensorAccuracy.NO_SENSOR,
                    isHardwareSensor = false
                )
            )
            awaitClose { }
            return@callbackFlow
        }

        val rotationMatrix = FloatArray(9)
        val orientationAngles = FloatArray(3)
        val truncatedRotationVector = FloatArray(4)

        // Accelerometer & Magnetometer fallback arrays
        val accelerometerReading = FloatArray(3)
        val magnetometerReading = FloatArray(3)
        var hasAccel = false
        var hasMag = false

        var currentAccuracy = SensorAccuracy.HIGH
        var magneticMagnitude = 45.0f

        // Smoothing variables using sin/cos unit circle smoothing
        var smoothSin = 0.0
        var smoothCos = 1.0
        val alpha = 0.15 // Low pass filter factor (0.15 provides great smoothness with immediate responsiveness)
        var initialized = false

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event == null) return

                when (event.sensor.type) {
                    Sensor.TYPE_ROTATION_VECTOR -> {
                        // Rotation vector is 4 or 5 elements; truncate if needed for getRotationMatrixFromVector
                        val vector = if (event.values.size > 4) {
                            System.arraycopy(event.values, 0, truncatedRotationVector, 0, 4)
                            truncatedRotationVector
                        } else {
                            event.values
                        }

                        SensorManager.getRotationMatrixFromVector(rotationMatrix, vector)
                        SensorManager.getOrientation(rotationMatrix, orientationAngles)

                        val rawAzimuth = Math.toDegrees(orientationAngles[0].toDouble()).toFloat()
                        val normalizedAzimuth = (rawAzimuth + 360f) % 360f

                        val pitch = Math.toDegrees(orientationAngles[1].toDouble()).toFloat()
                        val roll = Math.toDegrees(orientationAngles[2].toDouble()).toFloat()

                        val rad = Math.toRadians(normalizedAzimuth.toDouble())
                        if (!initialized) {
                            smoothSin = sin(rad)
                            smoothCos = cos(rad)
                            initialized = true
                        } else {
                            smoothSin = smoothSin * (1.0 - alpha) + sin(rad) * alpha
                            smoothCos = smoothCos * (1.0 - alpha) + cos(rad) * alpha
                        }

                        val smoothDeg = ((Math.toDegrees(atan2(smoothSin, smoothCos)) + 360.0) % 360.0).toFloat()

                        trySend(
                            CompassData(
                                azimuthDegrees = smoothDeg,
                                pitchDegrees = pitch,
                                rollDegrees = roll,
                                magneticFieldUt = magneticMagnitude,
                                accuracy = currentAccuracy,
                                isHardwareSensor = true
                            )
                        )
                    }

                    Sensor.TYPE_ACCELEROMETER -> {
                        System.arraycopy(event.values, 0, accelerometerReading, 0, 3)
                        hasAccel = true
                        if (rotationVectorSensor == null && hasMag) {
                            computeOrientationFromAccelAndMag()
                        }
                    }

                    Sensor.TYPE_MAGNETIC_FIELD -> {
                        System.arraycopy(event.values, 0, magnetometerReading, 0, 3)
                        hasMag = true
                        val mx = event.values[0].toDouble()
                        val my = event.values[1].toDouble()
                        val mz = event.values[2].toDouble()
                        magneticMagnitude = sqrt(mx * mx + my * my + mz * mz).toFloat()

                        if (rotationVectorSensor == null && hasAccel) {
                            computeOrientationFromAccelAndMag()
                        }
                    }
                }
            }

            private fun computeOrientationFromAccelAndMag() {
                val success = SensorManager.getRotationMatrix(
                    rotationMatrix,
                    null,
                    accelerometerReading,
                    magnetometerReading
                )
                if (success) {
                    SensorManager.getOrientation(rotationMatrix, orientationAngles)
                    val rawAzimuth = Math.toDegrees(orientationAngles[0].toDouble()).toFloat()
                    val normalizedAzimuth = (rawAzimuth + 360f) % 360f
                    val pitch = Math.toDegrees(orientationAngles[1].toDouble()).toFloat()
                    val roll = Math.toDegrees(orientationAngles[2].toDouble()).toFloat()

                    val rad = Math.toRadians(normalizedAzimuth.toDouble())
                    if (!initialized) {
                        smoothSin = sin(rad)
                        smoothCos = cos(rad)
                        initialized = true
                    } else {
                        smoothSin = smoothSin * (1.0 - alpha) + sin(rad) * alpha
                        smoothCos = smoothCos * (1.0 - alpha) + cos(rad) * alpha
                    }
                    val smoothDeg = ((Math.toDegrees(atan2(smoothSin, smoothCos)) + 360.0) % 360.0).toFloat()

                    trySend(
                        CompassData(
                            azimuthDegrees = smoothDeg,
                            pitchDegrees = pitch,
                            rollDegrees = roll,
                            magneticFieldUt = magneticMagnitude,
                            accuracy = currentAccuracy,
                            isHardwareSensor = true
                        )
                    )
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                if (sensor?.type == Sensor.TYPE_ROTATION_VECTOR || sensor?.type == Sensor.TYPE_MAGNETIC_FIELD) {
                    currentAccuracy = when (accuracy) {
                        SensorManager.SENSOR_STATUS_ACCURACY_HIGH -> SensorAccuracy.HIGH
                        SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM -> SensorAccuracy.MEDIUM
                        SensorManager.SENSOR_STATUS_ACCURACY_LOW -> SensorAccuracy.LOW
                        SensorManager.SENSOR_STATUS_UNRELIABLE -> SensorAccuracy.UNRELIABLE
                        else -> SensorAccuracy.MEDIUM
                    }
                }
            }
        }

        if (rotationVectorSensor != null) {
            sensorManager.registerListener(
                listener,
                rotationVectorSensor,
                SensorManager.SENSOR_DELAY_UI
            )
        }
        if (magneticSensor != null) {
            sensorManager.registerListener(
                listener,
                magneticSensor,
                SensorManager.SENSOR_DELAY_UI
            )
        }
        if (rotationVectorSensor == null && accelerometerSensor != null) {
            sensorManager.registerListener(
                listener,
                accelerometerSensor,
                SensorManager.SENSOR_DELAY_UI
            )
        }

        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }
}
