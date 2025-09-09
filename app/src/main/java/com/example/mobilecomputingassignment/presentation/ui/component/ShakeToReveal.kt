package com.example.mobilecomputingassignment.presentation.ui.component

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import kotlin.math.sqrt

@Composable
fun ShakeToReveal(
    onShake: () -> Unit,
    // ~2.5–3.0 works well for most devices; lower -> more sensitive
    sensitivityG: Float = 4.7f,
    cooldownMs: Long = 1200L
) {
    val context = LocalContext.current
    val onShakeState by rememberUpdatedState(onShake)
    val sensorManager = remember {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    val lastTrigger = remember { mutableStateOf(0L) }

    DisposableEffect(sensorManager, sensitivityG, cooldownMs) {
        val accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        if (accel == null) {
            onDispose { }
        } else {
            val listener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent) {
                    val x = event.values[0]
                    val y = event.values[1]
                    val z = event.values[2]

                    val gX = x / SensorManager.GRAVITY_EARTH
                    val gY = y / SensorManager.GRAVITY_EARTH
                    val gZ = z / SensorManager.GRAVITY_EARTH
                    val gForce = sqrt(gX * gX + gY * gY + gZ * gZ)

                    if (gForce >= sensitivityG) {
                        val now = System.currentTimeMillis()
                        if (now - lastTrigger.value >= cooldownMs) {
                            lastTrigger.value = now
                            onShakeState()
                        }
                    }
                }
                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
            }

            sensorManager.registerListener(
                listener,
                accel,
                SensorManager.SENSOR_DELAY_GAME
            )

            onDispose {
                sensorManager.unregisterListener(listener)
            }
        }
    }
}
