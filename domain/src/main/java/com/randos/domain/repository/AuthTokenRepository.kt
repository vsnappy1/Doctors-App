package com.randos.domain.repository

import com.randos.domain.type.NetworkResult
import com.randos.domain.model.Credential

interface AuthTokenRepository {
    suspend fun getAuthToken(credential: Credential): NetworkResult<Unit>
}