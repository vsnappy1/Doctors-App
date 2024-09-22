package com.randos.domain.manager

import com.randos.domain.type.Location
import kotlinx.coroutines.flow.Flow

interface BackgroundLocationManager {
    suspend fun startLocationService()
    suspend fun stopLocationService()
    suspend fun subscribeToLocationUpdates()
    fun unsubscribeFromLocationUpdates()
    fun isLocationTrackingActive(): Flow<Boolean>
    fun streamCurrentLocation(): Flow<Location.LatLng>
}