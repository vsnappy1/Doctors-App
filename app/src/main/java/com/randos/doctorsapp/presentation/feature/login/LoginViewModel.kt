package com.randos.doctorsapp.presentation.feature.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.randos.domain.model.Credential
import com.randos.domain.repository.AuthTokenRepository
import com.randos.domain.type.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "LoginViewModel"
@HiltViewModel
class LoginViewModel @Inject constructor(private val repository: AuthTokenRepository) :
    ViewModel() {

    private val _uiState = MutableLiveData(LoginScreenState())
    val uiState: LiveData<LoginScreenState> = _uiState

    fun onEmailChange(email: String) {
        _uiState.value?.apply {
            _uiState.postValue(
                copy(
                    email = email,
                    buttonEnabled = isButtonEnabled(email, password)
                )
            )
        }
    }

    fun onPasswordChange(password: String) {
        _uiState.value?.apply {
            _uiState.postValue(
                copy(
                    password = password,
                    buttonEnabled = isButtonEnabled(email, password)
                )
            )
        }
    }

    fun login() {
        _uiState.postValue(_uiState.value?.copy(networkResult = NetworkResult.Loading))
        viewModelScope.launch {
            _uiState.value?.apply {
                val networkResult = repository.getAuthToken(
                    Credential(
                        email,
                        password
                    )
                )
                _uiState.postValue(copy(networkResult = networkResult))
            }
        }
    }

    private fun isButtonEnabled(email: String, password: String): Boolean {
        return true//isValidEmail(email) && password.isNotEmpty()
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "onCleared: ")
    }
}