package com.randos.doctorsapp.data.manager

import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import com.randos.domain.manager.AccelerometerManager
import com.randos.domain.model.Accelerometer
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

private const val TAG = "AccelerometerManagerImp"

class AccelerometerManagerImpl @Inject constructor(application: Application) :
    AccelerometerManager {
    private var sensorManager: SensorManager =
        application.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private lateinit var sensorEventListener: SensorEventListener

    override fun accelerometerDataStream(): Flow<Accelerometer> = callbackFlow {
        Log.d(TAG, "accelerometerDataStream: ")
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        sensorEventListener = object : SensorEventListener {
            override fun onSensorChanged(p0: SensorEvent) {
                val array = p0.values
                val data = Accelerometer(x = array[0], y = array[1], z = array[2])
                trySend(data)
                Log.d(TAG, "onSensorChanged: $data")
            }

            override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
                // Do nothing
            }
        }

        sensorManager.registerListener(
            sensorEventListener,
            accelerometer,
            SensorManager.SENSOR_DELAY_GAME
        )

        awaitClose {
            sensorManager.unregisterListener(sensorEventListener)
            Log.d(TAG, "Accelerometer flow closed")
        }
    }

    override fun stopAccelerometerDataStream() {
        if (!this::sensorEventListener.isInitialized) return
        sensorManager.unregisterListener(sensorEventListener)
        Log.d(TAG, "stopAccelerometerDataStream: ")
    }
}