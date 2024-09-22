package com.randos.domain.repository

import com.randos.domain.model.User
import com.randos.domain.type.NetworkResult

interface HomeRepository {
    suspend fun getUserDetails(): NetworkResult<User>
}