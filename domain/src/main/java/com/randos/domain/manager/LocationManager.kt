package com.randos.domain.manager

import com.randos.domain.type.Location
import kotlinx.coroutines.flow.Flow

interface LocationManager {
    suspend fun getLocation(): Location?
    fun streamCurrentLocation(): Flow<Location.LatLng>
    fun terminateCurrentLocationUpdates()
}