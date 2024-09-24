package com.randos.doctorsapp.data.manager

import android.app.Application
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Looper
import android.util.Log
import com.randos.doctorsapp.utils.SdkVersionProvider
import com.randos.domain.manager.PermissionManager
import com.randos.domain.model.Address
import com.randos.domain.model.User
import com.randos.domain.store.UserStore
import com.randos.domain.type.Location
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.function.Consumer
import kotlin.time.Duration.Companion.milliseconds

class LocationManagerImplTest {

    @MockK(relaxed = true)
    private lateinit var application: Application

    @MockK
    private lateinit var userStore: UserStore

    @MockK
    private lateinit var permissionManager: PermissionManager

    @MockK(relaxed = true)
    private lateinit var androidLocationManager: LocationManager

    @MockK
    private lateinit var looper: Looper

    @MockK
    private lateinit var sdkVersionProvider: SdkVersionProvider

    private lateinit var locationManager: com.randos.domain.manager.LocationManager

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        locationManager =
            LocationManagerImpl(
                application,
                userStore,
                permissionManager,
                androidLocationManager,
                sdkVersionProvider,
                looper
            )

        mockkStatic(Log::class)
        every { Log.i(any(), any()) } returns 1
        every { Log.d(any(), any()) } returns 1
        every { Log.e(any(), any()) } returns 1
    }

    @Test
    fun getLocation_whenLocationPermissionNotGranted_shouldReturnUserAddress() = runTest {
        // Given
        val address = Address("", "San Jose", "CA", "", "US")
        val user = User(1, "kumar", address)
        every { permissionManager.isLocationPermissionGranted() } returns false
        every { userStore.getUser() } returns user

        // When
        val location = locationManager.getLocation()

        // Then
        assertTrue(location is Location.Address)
        assertEquals(address, (location as Location.Address).address)
    }

    @Test
    fun getLocation_whenLocationPermissionGrantedAndAllLocationProviderAreDisabled_shouldReturnUserAddress() =
        runTest {
            // Given
            val address = Address("", "San Jose", "CA", "", "US")
            val user = User(1, "kumar", address)
            every { permissionManager.isLocationPermissionGranted() } returns true
            every { userStore.getUser() } returns user

            // When
            val location = locationManager.getLocation()

            // Then
            assertTrue(location is Location.Address)
            assertEquals(address, (location as Location.Address).address)
        }

    @Test
    fun getLocation_whenLocationPermissionGrantedAndGpsEnabledOnSdkVersionR_shouldReturnCurrentLocation() =
        runTest {
            // Given
            val latitude = 27.000
            val longitude = -36.00
            val androidLocation: android.location.Location = mockk()
            val currentLocationCallback = slot<Consumer<android.location.Location>>()

            every { androidLocation.latitude } returns latitude
            every { androidLocation.longitude } returns longitude
            every { permissionManager.isLocationPermissionGranted() } returns true
            every { androidLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) } returns true
            every { sdkVersionProvider.get() } returns Build.VERSION_CODES.R
            every {
                androidLocationManager.getCurrentLocation(
                    any(),
                    any(),
                    any(),
                    capture(currentLocationCallback)
                )
            } just Runs

            // When
            val location = async { locationManager.getLocation() }
            delay(1.milliseconds)
            currentLocationCallback.captured.accept(androidLocation)

            // Then
            assertTrue(location.await() is Location.LatLng)
            assertEquals(
                Location.LatLng(latitude, longitude),
                (location.await() as Location.LatLng)
            )
        }

    @Test
    @Suppress("Deprecation")
    fun getLocation_whenLocationPermissionGrantedAndGpsEnabledOnSdkVersionBelowR_shouldReturnCurrentLocation() =
        runTest {
            // Given
            val provider = LocationManager.GPS_PROVIDER
            val latitude = 27.000
            val longitude = -36.00
            val androidLocation: android.location.Location = mockk()
            val currentLocationCallback = slot<LocationListener>()

            every { androidLocation.latitude } returns latitude
            every { androidLocation.longitude } returns longitude
            every { permissionManager.isLocationPermissionGranted() } returns true
            every { androidLocationManager.isProviderEnabled(provider) } returns true
            every { sdkVersionProvider.get() } returns Build.VERSION_CODES.Q
            every {
                androidLocationManager.requestSingleUpdate(
                    provider,
                    capture(currentLocationCallback),
                    any()
                )
            } just Runs

            // When
            val location = async { locationManager.getLocation() }
            delay(1.milliseconds)
            currentLocationCallback.captured.onLocationChanged(androidLocation)

            // Then
            assertTrue(location.await() is Location.LatLng)
            assertEquals(
                Location.LatLng(latitude, longitude),
                (location.await() as Location.LatLng)
            )
        }

    @Test
    fun getLocation_whenUnableToGetLastKnownLocation_shouldReturnUserAddress() =
        runTest {
            // Given
            val address = Address("", "San Jose", "CA", "", "US")
            val user = User(1, "kumar", address)
            every { permissionManager.isLocationPermissionGranted() } returns true
            every { userStore.getUser() } returns user
            every { permissionManager.isLocationPermissionGranted() } returns true

            // When
            val location = locationManager.getLocation()

            // Then
            assertTrue(location is Location.Address)
            assertEquals(address, (location as Location.Address).address)
        }

    @Test
    fun streamCurrentLocation_whenLocationPermissionNotGranted_shouldCloseFlow() = runTest {
        // Given
        every { permissionManager.isLocationPermissionGranted() } returns false

        // When
        val stream = locationManager.streamCurrentLocation()

        // Then
        stream.onCompletion {
            assertTrue(true)
        }.collect {
            assertTrue(false)
        }
    }

    @Test
    fun streamCurrentLocation_whenLocationPermissionGranted_shouldEmitCurrentLocation() = runTest {
        // Given
        val latitude = 27.000
        val longitude = -36.00
        val androidLocation: android.location.Location = mockk()
        val locationListener = slot<LocationListener>()

        every { androidLocation.latitude } returns latitude
        every { androidLocation.longitude } returns longitude
        every { permissionManager.isLocationPermissionGranted() } returns true
        every { androidLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) } returns true
        every {
            androidLocationManager.requestLocationUpdates(
                any<String>(),
                any<Long>(),
                any<Float>(),
                capture(locationListener)
            )
        } just Runs

        // When
        launch {
            delay(1.milliseconds)
            locationListener.captured.onLocationChanged(androidLocation)
        }
        val stream = locationManager.streamCurrentLocation()

        // Then
        var job: Job? = null
        job = launch {
            stream.collect {
                assertEquals(latitude, it.lat, 0.0)
                assertEquals(longitude, it.lng, 0.0)
                job?.cancel()
            }
        }
        job.join()
    }

    @Test
    fun streamCurrentLocation_whenLocationPermissionGrantedButProvidersNotAvailable_shouldCloseFlow() =
        runTest {
            // Given
            every { permissionManager.isLocationPermissionGranted() } returns true

            // When
            val stream = locationManager.streamCurrentLocation()

            // Then
            stream.onCompletion {
                assertTrue(true)
            }.collect {
                assertTrue(false)
            }
        }

    @Test
    fun terminateCurrentLocationUpdates_whenInvoked_shouldRemoveUpdateListener() = runTest {
        // Given
        every { permissionManager.isLocationPermissionGranted() } returns true
        every { androidLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) } returns true

        // When
        val job = launch { locationManager.streamCurrentLocation().collect {} }
        delay(1.milliseconds)
        locationManager.terminateCurrentLocationUpdates()
        job.cancel()

        // Then
        verify { androidLocationManager.removeUpdates(any<LocationListener>()) }
    }
}