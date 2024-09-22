package com.randos.domain.repository

import com.randos.domain.type.NetworkResult


interface OtpRepository {
    suspend fun requestOtp(): NetworkResult<Unit>
    suspend fun verify(otp: String): NetworkResult<Unit>
    suspend fun getOtp(): String?
}