package com.randos.doctorsapp.data.manager

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import com.randos.domain.manager.AccelerometerManager
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkClass
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import kotlin.time.Duration.Companion.milliseconds

class AccelerometerManagerImplTest {

    @MockK
    private lateinit var sensorManager: SensorManager

    private lateinit var accelerometerManager: AccelerometerManager

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        accelerometerManager = AccelerometerManagerImpl(sensorManager)
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun accelerometerDataStream_whenInvoked_shouldReturnAFlowOfAccelerometer() = runBlocking {
        // Given
        val accelerometer: Sensor = mockk()
        val sensorEventListener = slot<SensorEventListener>()
        val mockSensorEvent = createSensorEvent()

        every { sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) } returns accelerometer
        every {
            sensorManager.registerListener(
                capture(sensorEventListener),
                any(),
                SensorManager.SENSOR_DELAY_GAME
            )
        } returns true
        every { sensorManager.unregisterListener(capture(sensorEventListener)) } just Runs

        // When
        val flow = accelerometerManager.accelerometerDataStream()
        var x = 0f
        var y = 0f
        var z = 0f
        var job: Job? = null
        job = launch {
            flow.collect {
                x = it.x
                y = it.y
                z = it.z
                job?.cancel()
            }
        }
        launch {
            delay(1.milliseconds)
            sensorEventListener.captured.onSensorChanged(mockSensorEvent)
        }.join()

        // Then
        assertEquals(1.0f, x)
        assertEquals(2.0f, y)
        assertEquals(3.0f, z)
    }

    @Test
    fun stopAccelerometerDataStream_whenInvoked_shouldUnregisterListener() = runBlocking {
        // Given
        val accelerometer: Sensor = mockk()
        val sensorEventListener = slot<SensorEventListener>()

        every { sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) } returns accelerometer
        every {
            sensorManager.registerListener(
                capture(sensorEventListener),
                any(),
                SensorManager.SENSOR_DELAY_GAME
            )
        } returns true
        every { sensorManager.unregisterListener(capture(sensorEventListener)) } just Runs

        // When
        withTimeoutOrNull(1.milliseconds) {
            accelerometerManager.accelerometerDataStream().first()
        }

        launch {
            delay(1.milliseconds)
            accelerometerManager.stopAccelerometerDataStream()
        }.join()

        // Then
        verify(exactly = 2) { sensorManager.unregisterListener(any<SensorEventListener>()) }
    }

    private fun createSensorEvent(): SensorEvent {
        val sensorEvent = mockkClass(SensorEvent::class)
        /**
         * This is a hack to set the values of the SensorEvent object, since only mocking was not working.
         */
        try {
            val field = SensorEvent::class.java.getDeclaredField("values")
            field.isAccessible = true
            field.set(sensorEvent, floatArrayOf(1.0f, 2.0f, 3.0f))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return sensorEvent
    }
}