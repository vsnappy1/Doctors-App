package com.randos.doctorsapp.data.repository

import android.util.Log
import com.randos.doctorsapp.data.network.ApiService
import com.randos.domain.exception.LocationDetailsNotAvailableException
import com.randos.domain.type.Location
import com.randos.domain.type.NetworkResult
import com.randos.domain.model.Doctor
import com.randos.domain.repository.DoctorListRepository
import com.randos.domain.manager.LocationManager
import javax.inject.Inject

class DoctorListRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val locationManager: LocationManager
) : DoctorListRepository {

    companion object {
        private const val TAG = "DoctorListRepositoryImp"
    }

    override suspend fun getDoctors(): NetworkResult<Pair<List<Doctor>, Location>> {
        try {
            when (val location = locationManager.getLocation()) {
                is Location.Address -> {
                    return NetworkResult.Success(
                        Pair(apiService.getDoctorList(location.address), location)
                    )
                }

                is Location.LatLng -> {
                    return NetworkResult.Success(
                        Pair(apiService.getDoctorList(location.lat, location.lng), location)
                    )
                }

                null -> {
                    return NetworkResult.Failure(
                        "Failed to fetch doctor list",
                        LocationDetailsNotAvailableException()
                    )
                }
            }
        } catch (exception: Exception) {
            Log.e(TAG, "Failed to fetch doctor list", exception)
            return NetworkResult.Failure("Failed to fetch doctor list", exception)
        }
    }
}