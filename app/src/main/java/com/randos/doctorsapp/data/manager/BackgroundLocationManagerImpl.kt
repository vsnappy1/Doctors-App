package com.randos.doctorsapp.data.manager

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.randos.doctorsapp.di.Dispatcher
import com.randos.doctorsapp.service.LocationTrackingService
import com.randos.domain.manager.BackgroundLocationManager
import com.randos.domain.manager.PermissionManager
import com.randos.domain.type.Location
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

private const val TAG = "BackgroundLocationManager"

class BackgroundLocationManagerImpl @Inject constructor(
    private val application: Application,
    private val permissionManager: PermissionManager,
    @Dispatcher.Io private val dispatcher: CoroutineDispatcher
) : BackgroundLocationManager {

    private val locationTrackingServiceIntent =
        Intent(application, LocationTrackingService::class.java)
    private var serviceConnection: ServiceConnection? = null
    private val scope = CoroutineScope(dispatcher)

    override suspend fun startLocationService() {
        Log.i(TAG, "startLocationService: ")
        if (!permissionManager.isLocationPermissionGranted()) {
            Log.e(TAG, "Location permission not granted.")
            return
        }
        unbindLocationTrackingService()
        application.startForegroundService(locationTrackingServiceIntent)
        bindLocationTrackingService().run {
            observeLocationUpdates(this)
            startLocationTracking()
        }
    }

    override suspend fun stopLocationService() {
        Log.i(TAG, "stopLocationService: ")
        unbindLocationTrackingService()
        application.stopService(locationTrackingServiceIntent)
    }

    override suspend fun subscribeToLocationUpdates() {
        Log.i(TAG, "subscribeToLocationUpdates: ")
        bindLocationTrackingService().run {
            observeLocationUpdates(this)
        }
    }

    private fun observeLocationUpdates(locationTrackingService: LocationTrackingService) {
        scope.launch {
            locationTrackingService.isLocationTrackingActive.collect {
                Log.i(TAG, "isLocationTrackingActive: $it")
                isLocationTrackingActive.value = it
            }
        }
        scope.launch {
            locationTrackingService.locationFlow.collect {
                if (it != null) {
                    streamCurrentLocation.value = it
                }
            }
        }
    }

    override fun unsubscribeFromLocationUpdates() {
        Log.i(TAG, "unsubscribeFromLocationUpdates: ")
        unbindLocationTrackingService()
        scope.cancel()
    }

    private val isLocationTrackingActive = MutableStateFlow(false)
    override fun isLocationTrackingActive(): Flow<Boolean> {
        return isLocationTrackingActive
    }

    private val streamCurrentLocation = MutableStateFlow(Location.LatLng(0.0, 0.0))
    override fun streamCurrentLocation(): Flow<Location.LatLng> {
        return streamCurrentLocation
    }

    private fun unbindLocationTrackingService() {
        serviceConnection?.let { application.unbindService(it) }
        serviceConnection = null
    }

    private suspend fun bindLocationTrackingService() =
        suspendCancellableCoroutine { continuation ->
            serviceConnection = object : ServiceConnection {
                override fun onServiceConnected(componentName: ComponentName, binder: IBinder) {
                    Log.i(TAG, "onServiceConnected: ")
                    val locationTrackingService =
                        (binder as LocationTrackingService.LocalBinder).getService()
                    continuation.resume(locationTrackingService)
                }

                override fun onServiceDisconnected(componentName: ComponentName) {
                    Log.i(TAG, "onServiceDisconnected: ")
                    serviceConnection = null
                }
            }

            application.bindService(
                locationTrackingServiceIntent,
                serviceConnection!!,
                Context.BIND_AUTO_CREATE
            )
        }
}