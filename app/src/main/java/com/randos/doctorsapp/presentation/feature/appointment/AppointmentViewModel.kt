package com.randos.doctorsapp.presentation.feature.appointment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.randos.domain.model.Appointment
import com.randos.domain.repository.AppointmentRepository
import com.randos.domain.type.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "AppointmentViewModel"

@HiltViewModel
class AppointmentViewModel @Inject constructor(
    private val repository: AppointmentRepository
) : ViewModel() {

    private val _uiState = MutableLiveData(AppointmentScreenState())
    val uiState: LiveData<AppointmentScreenState> = _uiState

    fun getAppointments() {
        _uiState.postValue(_uiState.value?.copy(appointments = NetworkResult.Loading))
        viewModelScope.launch {
            val result = repository.getAvailableAppointments(1)
            _uiState.postValue(_uiState.value?.copy(appointments = result))
        }
    }

    fun bookAppointment() {
        _uiState.postValue(_uiState.value?.copy(appointmentConfirmation = NetworkResult.Loading))
        viewModelScope.launch {
            _uiState.value?.selectedAppointment?.let {
                val result = repository.bookAppointment(it.id)
                _uiState.postValue(_uiState.value?.copy(appointmentConfirmation = result))
            }
        }
    }

    fun onAppointmentSelected(appointment: Appointment) {
        _uiState.postValue(_uiState.value?.copy(selectedAppointment = appointment))
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "onCleared: ")
    }
}