package com.randos.doctorsapp.presentation.feature.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.randos.domain.repository.HomeRepository
import com.randos.domain.type.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HomeRepository
) : ViewModel() {

    private val _uiState = MutableLiveData(HomeScreenState())
    val uiState: LiveData<HomeScreenState> = _uiState

    fun getUserDetails() {
        _uiState.postValue(_uiState.value?.copy(user = NetworkResult.Loading))
        viewModelScope.launch {
            val result = repository.getUserDetails()
            _uiState.postValue(_uiState.value?.copy(user = result))
        }
    }
}