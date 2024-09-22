package com.randos.doctorsapp.presentation.feature.developer.locationstream

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.randos.domain.manager.PermissionManager
import com.randos.domain.repository.LocationStreamRepository
import com.randos.domain.type.Location
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationStreamViewModel @Inject constructor(
    private val repository: LocationStreamRepository,
    private val permissionManager: PermissionManager
) : ViewModel() {

    private val _uiState = MutableLiveData(LocationStreamState())
    val uiState: LiveData<LocationStreamState> = _uiState

    init {
        viewModelScope.launch {
            repository.subscribeToLocationUpdates()
        }
    }

    fun observeLocationTrackingStatus() {
        viewModelScope.launch {
            repository.isLocationTrackingActive().collect {
                _uiState.value?.apply {
                    _uiState.postValue(
                        copy(
                            location = if (it) location else Location.LatLng(0.0, 0.0),
                            isLocationTrackingActive = it
                        )
                    )
                }
            }
        }

        viewModelScope.launch {
            delay(100)
            repository.streamCurrentLocation().collect {
                _uiState.value?.apply {
                    _uiState.postValue(
                        copy(
                            location = it,
                        )
                    )
                }
            }
        }
    }

    fun isLocationPermissionGranted(): Boolean {
        return permissionManager.isLocationPermissionGranted()
    }

    fun startLocationTracking() {
        viewModelScope.launch {
            repository.startLocationService()
        }
    }

    fun stopLocationTracking() {
        viewModelScope.launch {
            repository.stopLocationService()
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.unsubscribeFromLocationUpdates()
    }
}