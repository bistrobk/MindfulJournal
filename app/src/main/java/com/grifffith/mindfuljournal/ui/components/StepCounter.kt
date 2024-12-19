package com.grifffith.mindfuljournal

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

@Composable
fun stepCounter(): Int {
    // Get context and initialize state for step count
    val context = LocalContext.current
    var stepCount by remember { mutableStateOf(0) } // State to store the current step count

    // Access the sensor manager and the step counter sensor
    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    val stepCounterSensor = remember { sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) }

    // Get the lifecycle owner to manage lifecycle events
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    // Sensor event listener to handle step counting
    val sensorEventListener = remember {
        object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor.type == Sensor.TYPE_STEP_COUNTER) {
                    // Update the step count when a new step is detected
                    stepCount = event.values[0].toInt()
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                // You can handle accuracy changes if needed, but it's not used here
            }
        }
    }

    // Manage sensor listener lifecycle using DisposableEffect
    DisposableEffect(lifecycleOwner) {
        // Observer for lifecycle events (ON_RESUME and ON_PAUSE)
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    // Register the sensor listener when the composable is in the resumed state
                    stepCounterSensor?.let {
                        sensorManager.registerListener(sensorEventListener, it, SensorManager.SENSOR_DELAY_UI)
                    }
                }
                Lifecycle.Event.ON_PAUSE -> {
                    // Unregister the sensor listener when the composable is paused
                    sensorManager.unregisterListener(sensorEventListener)
                }
                else -> {} // Do nothing on other lifecycle events
            }
        }

        // Add the observer to the lifecycle
        lifecycleOwner.lifecycle.addObserver(observer)

        // Cleanup when the composable is disposed
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer) // Remove the observer when the composable is disposed
            sensorManager.unregisterListener(sensorEventListener) // Unregister the sensor listener
        }
    }

    // Return the current step count, which is updated on each sensor event
    return stepCount
}
