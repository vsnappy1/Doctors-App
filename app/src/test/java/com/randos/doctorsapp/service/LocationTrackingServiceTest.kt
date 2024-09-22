package com.randos.doctorsapp.service

import android.util.Log
import com.randos.domain.manager.LocationManager
import com.randos.domain.type.Location
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class LocationTrackingServiceTest {

    private lateinit var service: LocationTrackingService

    private lateinit var locationManager: LocationManager

    @Before
    fun setUp() {
        locationManager = mockk()
        service = LocationTrackingService().apply {
            locationManager = this@LocationTrackingServiceTest.locationManager
        }
        mockkStatic(Log::class)
        every { Log.i(any(), any()) } returns 1
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun startLocationTracking_whenLocationTrackingIsInactive_shouldStartLocationTracking() =
        runTest {
            // Given
            val latLng = Location.LatLng(1.0, 2.0)
            coEvery { locationManager.streamCurrentLocation() } returns flowOf(latLng)

            // When
            service.startLocationTracking()

            // Then
            assertTrue(service.isLocationTrackingActive.value)
            assertEquals(latLng, service.locationFlow.first())
        }

    @Test
    fun startLocationTracking_whenLocationTrackingIsActive_shouldNotStartLocationTrackingAgain() =
        runTest {
            // Given
            val latLng = Location.LatLng(1.0, 2.0)
            coEvery { locationManager.streamCurrentLocation() } returns flowOf(latLng)
            // Already running (i.e. active)
            service.startLocationTracking()

            // When
            service.startLocationTracking()

            // Then
            verify(exactly = 1) { locationManager.streamCurrentLocation() }
        }

    @Test
    fun onDestroy_shouldStopLocationTracking() = runTest {
        // Given
        coEvery { locationManager.streamCurrentLocation() } returns flowOf()
        coEvery { locationManager.terminateCurrentLocationUpdates() } returns Unit
        service.startLocationTracking()

        // When
        service.onDestroy()

        // Then
        assertFalse(service.isLocationTrackingActive.value)
        verify { locationManager.terminateCurrentLocationUpdates() }
    }
}