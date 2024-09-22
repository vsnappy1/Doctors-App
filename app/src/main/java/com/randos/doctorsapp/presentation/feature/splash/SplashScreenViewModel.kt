package com.randos.doctorsapp.presentation.feature.splash

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.randos.domain.repository.SplashRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "SplashScreenViewModel"

@HiltViewModel
class SplashScreenViewModel @Inject constructor(
    private val repository: SplashRepository,
) : ViewModel() {

    private val _uiState = MutableLiveData(SplashScreenState())
    val uiState: LiveData<SplashScreenState> = _uiState

    fun checkIsVerifiedUser() {
        _uiState.postValue(_uiState.value?.copy(isVerifiedUser = null))
        viewModelScope.launch {
            val result = repository.isAuthTokenPresent() && repository.hasOtpBeenVerifiedBefore()
            _uiState.postValue(_uiState.value?.copy(isVerifiedUser = result))
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "onCleared: ")
    }
}