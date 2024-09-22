package com.randos.doctorsapp.data.manager

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Looper
import android.util.Log
import com.randos.domain.store.UserStore
import com.randos.domain.type.Location
import com.randos.domain.manager.PermissionManager
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import kotlin.coroutines.resume

class LocationManagerImpl @Inject constructor(
    private val context: Application,
    private val userStore: UserStore,
    private val permissionManager: PermissionManager
) : com.randos.domain.manager.LocationManager {

    companion object {
        private const val TAG = "LocationManagerImpl"
    }

    private val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private var locationUpdateListener: LocationListener? = null

    override suspend fun getLocation(): Location? = suspendCancellableCoroutine { continuation ->
        if (permissionManager.isLocationPermissionGranted()) {

            CoroutineScope(continuation.context).launch {
                // Try to get current location with a 2000 milli seconds time out.
                val currentLocation = withTimeoutOrNull(2000) {
                    getCurrentLocation(locationManager)
                }

                currentLocation?.let {
                    continuation.resume(it)
                    return@launch
                }

                // If for any reason, unable to get current location try to get last known location.
                val lastKnownLocation = getLastKnownLocation(locationManager)
                lastKnownLocation?.let {
                    continuation.resume(it)
                    return@launch
                }

                // If failed to get last know location try get user address from userStore.
                val userLocation = getUserAddress()
                continuation.resume(userLocation)
            }
        } else {
            // If location permission is not granted try get user address from userStore.
            val userLocation = userStore.getUser()?.let { Location.Address(it.address) }
            continuation.resume(userLocation)
        }
    }

    @SuppressLint("MissingPermission")
    override fun streamCurrentLocation(): Flow<Location.LatLng> = callbackFlow {
        if (!permissionManager.isLocationPermissionGranted()) {
            close()
            Log.e(TAG, "Location permission is not granted.")
            return@callbackFlow
        }

        locationUpdateListener = LocationListener {
            val location = Location.LatLng(it.latitude, it.longitude)
            Log.i(TAG, "streamCurrentLocation: $location")
            trySend(location)
        }

        val provider = getBestProvider(locationManager)

        if (provider != null) {
            locationManager.requestLocationUpdates(
                provider,
                1000L,
                0.1f,
                locationUpdateListener!!
            )
        } else {
            close()
            Log.e(TAG, "Location provider is not available.")
            return@callbackFlow
        }

        awaitClose {
            locationUpdateListener?.let { locationManager.removeUpdates(it) }
        }
    }

    override fun terminateCurrentLocationUpdates() {
        locationUpdateListener?.let { locationManager.removeUpdates(it) }
    }

    private fun getUserAddress(): Location? {
        return userStore.getUser()?.let { Location.Address(it.address) }
    }

    @SuppressLint("MissingPermission")
    @Suppress("Deprecation")
    private suspend fun getCurrentLocation(locationManager: LocationManager) =
        suspendCancellableCoroutine<Location?>
        { continuation ->
            val provider = getBestProvider(locationManager)
            if (provider != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    locationManager.getCurrentLocation(
                        provider,
                        null,
                        context.mainExecutor
                    ) { location ->
                        Log.i(TAG, "Current Location: $location ($provider).")
                        continuation.resumeSafely(
                            Location.LatLng(
                                location.latitude,
                                location.longitude
                            )
                        )
                    }
                } else {
                    var locationListener: LocationListener? = null
                    locationListener = LocationListener { location ->
                        Log.i(TAG, "Current Location: $location ($provider).")
                        locationListener?.let { locationManager.removeUpdates(it) }
                        continuation.resumeSafely(
                            Location.LatLng(
                                location.latitude,
                                location.longitude
                            )
                        )
                    }
                    locationManager.requestSingleUpdate(
                        provider,
                        locationListener,
                        Looper.getMainLooper()
                    )
                }
            } else {
                continuation.resumeSafely(null)
            }
        }

    @SuppressLint("MissingPermission")
    private fun getLastKnownLocation(locationManager: LocationManager): Location? {
        val provider = getBestProvider(locationManager)
        return provider?.let {
            val lastKnowLocation = locationManager.getLastKnownLocation(it)
            if (lastKnowLocation != null) {
                Log.d(TAG, "Last Known Location: $lastKnowLocation  ($provider).")
                Location.LatLng(lastKnowLocation.latitude, lastKnowLocation.longitude)
            } else {
                null
            }
        }
    }

    private fun <T> CancellableContinuation<T>.resumeSafely(value: T) {
        if (isCompleted) return
        resume(value)
    }

    private fun getBestProvider(locationManager: LocationManager): String? {
        return when {
            // Check if GPS provider is enabled and use it if available
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) -> LocationManager.GPS_PROVIDER

            // If GPS is not available, check for Network provider
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> LocationManager.NETWORK_PROVIDER

            // If both GPS and Network are not available, check for Passive provider
            locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER) -> LocationManager.PASSIVE_PROVIDER

            // If no providers are available, return null
            else -> null
        }
    }
}