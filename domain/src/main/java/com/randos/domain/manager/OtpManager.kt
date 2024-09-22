package com.randos.domain.manager


interface OtpManager {
    suspend fun getOtp(): String?
}