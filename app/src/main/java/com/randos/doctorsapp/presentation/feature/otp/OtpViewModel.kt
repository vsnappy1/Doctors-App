package com.randos.doctorsapp.presentation.feature.otp

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.randos.domain.manager.PermissionManager
import com.randos.domain.repository.OtpRepository
import com.randos.domain.type.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

private const val TAG = "OtpViewModel"

@HiltViewModel
class OtpViewModel @Inject constructor(
    private val repository: OtpRepository,
    private val permissionManager: PermissionManager
) : ViewModel() {

    private val _uiState = MutableLiveData(OtpScreenState())
    val uiState: LiveData<OtpScreenState> = _uiState


    fun isReceiveSmsPermissionGranted() = permissionManager.isReceiveSmsPermissionGranted()

    fun requestOtp() {
        viewModelScope.launch {
            repository.requestOtp()
        }
    }

    fun observeReceivedSms() {
        viewModelScope.launch {
            // Wait for up to 30 seconds to receive OTP.
            val otp = withTimeoutOrNull(30.seconds) { repository.getOtp() }
            _uiState.postValue(
                _uiState.value?.copy(
                    otp = otp,
                    buttonEnabled = otp?.length == 6
                )
            )
        }
    }

    fun onOtpChange(otp: String) {
        _uiState.postValue(_uiState.value?.copy(otp = otp, buttonEnabled = otp.length == 6))
    }

    fun verify() {
        _uiState.postValue(_uiState.value?.copy(otpVerification = NetworkResult.Loading))
        viewModelScope.launch {
            val result = repository.verify(_uiState.value?.otp.orEmpty())
            _uiState.postValue(_uiState.value?.copy(otpVerification = result))
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "onCleared: ")
    }
}