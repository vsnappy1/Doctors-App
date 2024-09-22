package com.randos.doctorsapp.data.repository.developer

import com.randos.domain.manager.BackgroundLocationManager
import com.randos.domain.repository.LocationStreamRepository
import com.randos.domain.type.Location
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocationStreamRepositoryImpl @Inject constructor(
    private val backgroundLocationManager: BackgroundLocationManager
) : LocationStreamRepository {

    override suspend fun startLocationService() {
        backgroundLocationManager.startLocationService()
    }

    override suspend fun stopLocationService() {
        backgroundLocationManager.stopLocationService()
    }

    override suspend fun subscribeToLocationUpdates() {
        backgroundLocationManager.subscribeToLocationUpdates()
    }

    override fun unsubscribeFromLocationUpdates() {
        backgroundLocationManager.unsubscribeFromLocationUpdates()
    }

    override fun isLocationTrackingActive(): Flow<Boolean> {
        return backgroundLocationManager.isLocationTrackingActive()
    }

    override fun streamCurrentLocation(): Flow<Location.LatLng> {
        return backgroundLocationManager.streamCurrentLocation()
    }
}