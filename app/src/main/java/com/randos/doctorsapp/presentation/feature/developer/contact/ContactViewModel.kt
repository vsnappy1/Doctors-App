package com.randos.doctorsapp.presentation.feature.developer.contact

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.randos.domain.manager.PermissionManager
import com.randos.domain.repository.ContactRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactViewModel @Inject constructor(
    private val repository: ContactRepository,
    private val permission: PermissionManager
) : ViewModel() {

    private val _uiState = MutableLiveData(ContactScreenState())
    val uiState: LiveData<ContactScreenState> = _uiState

    fun getContacts() {
        _uiState.postValue(_uiState.value?.copy(fetchingContacts = true))
        viewModelScope.launch {
            val contacts = repository.getContacts()
            _uiState.postValue(_uiState.value?.copy(contacts = contacts, fetchingContacts = false))
        }
    }

    fun isReadContactPermissionGranted(): Boolean {
        return permission.isReadContactPermissionGranted()
    }
}