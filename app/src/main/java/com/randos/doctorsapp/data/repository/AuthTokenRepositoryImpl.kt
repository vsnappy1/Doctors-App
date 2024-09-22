package com.randos.doctorsapp.data.repository

import com.randos.doctorsapp.data.network.ApiService
import com.randos.domain.store.TokenStore
import com.randos.domain.type.NetworkResult
import com.randos.domain.model.Credential
import com.randos.domain.repository.AuthTokenRepository
import javax.inject.Inject

class AuthTokenRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val tokenStore: TokenStore
) :
    AuthTokenRepository {
    override suspend fun getAuthToken(credential: Credential): NetworkResult<Unit> {
        try {
            val token = apiService.getAuthToken(credential.email, credential.password)
            tokenStore.setToken(token)
            return NetworkResult.Success(Unit)
        } catch (exception: Exception) {
            return NetworkResult.Failure("Something went wrong: ${exception.localizedMessage}")
        }
    }
}