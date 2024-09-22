package com.randos.doctorsapp.data.repository

import com.randos.doctorsapp.data.network.ApiService
import com.randos.domain.manager.OtpManager
import com.randos.domain.repository.OtpRepository
import com.randos.domain.store.FlagStore
import com.randos.domain.type.NetworkResult
import javax.inject.Inject

class OtpRepositoryImpl @Inject constructor(
    private val otpManager: OtpManager,
    private val apiService: ApiService,
    private val flagStore: FlagStore
) : OtpRepository {

    override suspend fun requestOtp(): NetworkResult<Unit> {
        return try {
            apiService.requestOtp()
            NetworkResult.Success(Unit)
        } catch (exception: Exception) {
            NetworkResult.Failure("Failed to request otp")
        }
    }

    override suspend fun verify(otp: String): NetworkResult<Unit> {
        return try {
            apiService.verifyOtp()
            flagStore.markOtpAsVerified()
            NetworkResult.Success(Unit)
        } catch (exception: Exception) {
            NetworkResult.Failure("Failed to verify otp")
        }
    }

    override suspend fun getOtp(): String? {
        return otpManager.getOtp()
    }
}