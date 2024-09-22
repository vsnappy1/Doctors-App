package com.randos.doctorsapp.presentation.feature.developer.filedownload

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.randos.domain.repository.FileDownloadRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "FileDownloadViewModel"
@HiltViewModel
class FileDownloadViewModel @Inject constructor(
    private val repository: FileDownloadRepository
) : ViewModel() {

    private val _uiState = MutableLiveData(FileDownloadState())
    val uiState: LiveData<FileDownloadState> = _uiState

    fun download() {
        _uiState.postValue(
            _uiState.value?.copy(
                isDownloadStarted = true,
                downloadedFilePath = null
            )
        )
        viewModelScope.launch {
            repository.download(
                url = "https://files.testfile.org/PDF/10MB-TESTFILE.ORG.pdf",
                fileName = "Test File",
                extension = "pdf",
                onDownloadSuccess = {
                    _uiState.postValue(
                        _uiState.value?.copy(
                            isDownloadStarted = false,
                            downloadedFilePath = it
                        )
                    )
                    Log.d(TAG, "onDownloadSuccess")
                },
                onDownloadFailed = {
                    _uiState.postValue(
                        _uiState.value?.copy(
                            isDownloadStarted = false,
                            downloadedFilePath = it.localizedMessage
                        )
                    )
                }
            ).collect {
                _uiState.postValue(_uiState.value?.copy(percentageDownloaded = it))
                Log.d(TAG, "onCollect")
            }
        }
    }
}