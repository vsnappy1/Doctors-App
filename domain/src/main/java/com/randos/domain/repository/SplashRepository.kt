package com.randos.domain.repository

interface SplashRepository {
    suspend fun isAuthTokenPresent(): Boolean
    suspend fun hasOtpBeenVerifiedBefore(): Boolean
}