package com.randos.doctorsapp.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.ServiceCompat
import com.randos.doctorsapp.R
import com.randos.domain.manager.LocationManager
import com.randos.domain.type.Location
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

private const val TAG = "LocationTrackingService"

@AndroidEntryPoint
class LocationTrackingService : Service() {

    @Inject
    lateinit var locationManager: LocationManager

    private val binder = LocalBinder()
    private val scope = CoroutineScope(Dispatchers.Main)

    companion object {
        private const val NOTIFICATION_ID = 50
        private const val NOTIFICATION_CHANNEL_ID = "location_tacking_notification"
        private const val NOTIFICATION_CHANNEL_NAME = "Location Tacking Notification"
    }

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "onCreate: ")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "onStartCommand: ")
        createNotificationChannel()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ServiceCompat.startForeground(
                this,
                NOTIFICATION_ID,
                createNotification(),
                FOREGROUND_SERVICE_TYPE_LOCATION
            )
        } else {
            startForeground(NOTIFICATION_ID, createNotification())
        }
        return START_STICKY
    }

    private fun createNotification(): Notification {
        return Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Tracking Location")
            .setContentText("Service is running")
            .build()
    }

    private fun createNotificationChannel() {
        val notificationChannel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        )

        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)
    }

    private val _isLocationTrackingActive = MutableStateFlow(false)
    val isLocationTrackingActive: StateFlow<Boolean> = _isLocationTrackingActive

    private val _locationFlow = MutableStateFlow<Location.LatLng?>(null)
    val locationFlow: Flow<Location.LatLng?> = _locationFlow

    suspend fun startLocationTracking() {
        if (!_isLocationTrackingActive.value) {
            Log.i(TAG, "trackLocationUpdates: ")
            _isLocationTrackingActive.value = true
            locationManager.streamCurrentLocation().collect {
                _locationFlow.value = it
            }
        }
    }

    private fun stopLocationTracking() {
        if (_isLocationTrackingActive.value) {
            Log.i(TAG, "stopLocationTracking: ")
            _isLocationTrackingActive.value = false
            _locationFlow.value = null
            locationManager.terminateCurrentLocationUpdates()
        }
    }

    override fun onBind(p0: Intent): IBinder {
        Log.i(TAG, "onBind: ")
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.i(TAG, "onUnbind: ")
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy: ")
        scope.cancel()
        stopLocationTracking()
    }

    inner class LocalBinder : Binder() {
        fun getService(): LocationTrackingService = this@LocationTrackingService
    }
}