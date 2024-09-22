package com.randos.doctorsapp.presentation.feature.doctorlist

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.randos.domain.manager.PermissionManager
import com.randos.domain.repository.DoctorListRepository
import com.randos.domain.type.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "DoctorListViewModel"

@HiltViewModel
class DoctorListViewModel @Inject constructor(
    private val repository: DoctorListRepository,
    private val permissionManager: PermissionManager,
) : ViewModel() {

    private val _uiState = MutableLiveData(DoctorListScreenState())
    val uiState: LiveData<DoctorListScreenState> = _uiState

    fun isLocationPermissionGranted() = permissionManager.isLocationPermissionGranted()

    fun fetchDoctors() {
        _uiState.postValue(_uiState.value?.copy(networkResult = NetworkResult.Loading))
        viewModelScope.launch {
            _uiState.postValue(uiState.value?.copy(networkResult = repository.getDoctors()))
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "onCleared: ")
    }
}