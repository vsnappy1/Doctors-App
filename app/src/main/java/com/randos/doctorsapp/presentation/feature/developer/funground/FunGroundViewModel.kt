package com.randos.doctorsapp.presentation.feature.developer.funground

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.randos.domain.repository.FunGroundRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FunGroundViewModel @Inject constructor(
    private val repository: FunGroundRepository
) : ViewModel() {

    private val _uiState = MutableLiveData(FunGroundScreenState())
    val uiState: LiveData<FunGroundScreenState> = _uiState


    fun startObservingAccelerometer() {
        viewModelScope.launch {
            repository.getAccelerometerDataStream().collect {
                _uiState.postValue(_uiState.value?.copy(accelerometer = it))
            }
        }
    }

    fun stopObservingAccelerometer() {
        viewModelScope.launch {
            repository.stopAccelerometerDataStream()
        }
    }
}