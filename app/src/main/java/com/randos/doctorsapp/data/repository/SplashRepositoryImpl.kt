package com.randos.doctorsapp.data.repository

import com.randos.domain.repository.SplashRepository
import com.randos.domain.store.FlagStore
import com.randos.domain.store.TokenStore
import javax.inject.Inject

class SplashRepositoryImpl @Inject constructor(
    private val tokenStore: TokenStore,
    private val flagStore: FlagStore
) :
    SplashRepository {
    override suspend fun isAuthTokenPresent(): Boolean {
        return tokenStore.getToken() != null
    }

    override suspend fun hasOtpBeenVerifiedBefore(): Boolean {
        return flagStore.hasOtpBeenVerifiedBefore()
    }
}