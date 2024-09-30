package com.randos.doctorsapp.data.manager

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.util.Log
import com.randos.doctorsapp.service.LocationTrackingService
import com.randos.domain.manager.BackgroundLocationManager
import com.randos.domain.manager.PermissionManager
import com.randos.domain.type.Location
import io.mockk.CapturingSlot
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.time.Duration.Companion.milliseconds

class BackgroundLocationManagerImplTest {

    @MockK
    private lateinit var application: Application

    @MockK
    private lateinit var permissionManager: PermissionManager

    private val dispatcher = Dispatchers.Unconfined

    private lateinit var backgroundLocationManager: BackgroundLocationManager

    private lateinit var locationTrackingServiceIntent: CapturingSlot<Intent>
    private lateinit var serviceConnection: CapturingSlot<ServiceConnection>
    private lateinit var locationTrackingServiceLocalBinder: LocationTrackingService.LocalBinder
    private lateinit var locationTrackingService: LocationTrackingService

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        backgroundLocationManager =
            BackgroundLocationManagerImpl(application, permissionManager, dispatcher)
        mockkStatic(Log::class)
        every { Log.i(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
        setupCommonMocks()
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun startLocationService_whenLocationPermissionNotGranted_shouldLogError() = runTest {
        // Given
        every { permissionManager.isLocationPermissionGranted() } returns false

        // When
        backgroundLocationManager.startLocationService()

        // Then
        verify { Log.e(any(), any()) }
    }

    @Test
    fun startLocationService_whenPermissionGranted_shouldStartLocationServiceAndStartLocationTracking() =
        runTest {
            // Given
            /* All is taken care in setupCommonMocks method. */

            // When
            simulateServiceConnection()
            backgroundLocationManager.startLocationService()

            // Then
            verify { application.startForegroundService(locationTrackingServiceIntent.captured) }
            verify {
                application.bindService(
                    locationTrackingServiceIntent.captured,
                    serviceConnection.captured,
                    Context.BIND_AUTO_CREATE
                )
            }
            coVerify { locationTrackingService.startLocationTracking() }
        }

    @Test
    fun stopLocationService_whenInvoked_shouldStopLocationService() = runTest {
        // Given
        simulateServiceConnection()
        backgroundLocationManager.startLocationService()

        // When
        backgroundLocationManager.stopLocationService()

        // Then
        verify { application.unbindService(serviceConnection.captured) }
        verify { application.stopService(locationTrackingServiceIntent.captured) }
    }

    @Test
    fun subscribeToLocationUpdates_whenServiceIsNotRunning_shouldReturnIsLocationTrackingAsFalse() =
        runTest {
            // Given
            /* All is taken care in setupCommonMocks method. */

            // When
            simulateServiceConnection()
            backgroundLocationManager.subscribeToLocationUpdates()

            // Then
            assertFalse(backgroundLocationManager.isLocationTrackingActive().first())
            assertEquals(
                Location.LatLng(0.0, 0.0),
                backgroundLocationManager.streamCurrentLocation().first()
            )
        }

    @Test
    fun subscribeToLocationUpdates_whenServiceIsRunning_shouldReturnIsLocationTrackingAsTrue() =
        runTest {
            // Given
            val location = Location.LatLng(1.0, 1.0)
            every { locationTrackingService.isLocationTrackingActive } returns MutableStateFlow(true)
            every { locationTrackingService.locationFlow } returns MutableStateFlow(location)

            simulateServiceConnection()
            backgroundLocationManager.startLocationService()

            // When
            simulateServiceConnection()
            backgroundLocationManager.subscribeToLocationUpdates()

            // Then
            assertTrue(backgroundLocationManager.isLocationTrackingActive().first())
            assertEquals(location, backgroundLocationManager.streamCurrentLocation().first())
        }

    @Test
    fun unsubscribeFromLocationUpdates_whenInvoked_shouldUnbindFromService() = runTest {
        // Given
        simulateServiceConnection()
        backgroundLocationManager.subscribeToLocationUpdates()

        // When
        backgroundLocationManager.unsubscribeFromLocationUpdates()

        // Then
        verify { application.unbindService(serviceConnection.captured) }
    }

    private fun setupCommonMocks() {
        // Initialize common mocks
        locationTrackingServiceIntent = slot<Intent>()
        serviceConnection = slot<ServiceConnection>()
        locationTrackingServiceLocalBinder = mockk<LocationTrackingService.LocalBinder>()
        locationTrackingService = mockk<LocationTrackingService>()

        // Mock permission
        every { permissionManager.isLocationPermissionGranted() } returns true

        // Mock application
        every { application.startForegroundService(capture(locationTrackingServiceIntent)) } returns ComponentName(
            "",
            ""
        )
        every { application.stopService(any()) } returns true
        every {
            application.bindService(
                capture(locationTrackingServiceIntent),
                capture(serviceConnection),
                Context.BIND_AUTO_CREATE
            )
        } returns true
        every { application.unbindService(any()) } just runs

        // Mock service
        every { locationTrackingServiceLocalBinder.getService() } returns locationTrackingService
        every { locationTrackingService.isLocationTrackingActive } returns MutableStateFlow(false)
        every { locationTrackingService.locationFlow } returns MutableStateFlow(null)
        coEvery { locationTrackingService.startLocationTracking() } just runs
    }

    private fun TestScope.simulateServiceConnection() {
        launch {
            delay(1.milliseconds)
            serviceConnection.captured.onServiceConnected(
                ComponentName("", ""),
                locationTrackingServiceLocalBinder
            )
        }
    }
}