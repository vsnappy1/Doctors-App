package com.randos.domain.type

sealed class NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Failure(val message: String, val exception: Exception? = null) :
        NetworkResult<Nothing>()

    data object Loading : NetworkResult<Nothing>()
    data object NotStarted: NetworkResult<Nothing>()
}