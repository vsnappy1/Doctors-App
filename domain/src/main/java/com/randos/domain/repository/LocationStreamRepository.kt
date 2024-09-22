package com.randos.domain.repository

import com.randos.domain.type.Location
import kotlinx.coroutines.flow.Flow

interface LocationStreamRepository {
    suspend fun startLocationService()
    suspend fun stopLocationService()
    suspend fun subscribeToLocationUpdates()
    fun unsubscribeFromLocationUpdates()
    fun isLocationTrackingActive(): Flow<Boolean>
    fun streamCurrentLocation(): Flow<Location.LatLng>
}