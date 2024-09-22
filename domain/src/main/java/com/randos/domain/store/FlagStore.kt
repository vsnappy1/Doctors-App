package com.randos.domain.store

interface FlagStore {
    fun hasOtpBeenVerifiedBefore(): Boolean
    fun markOtpAsVerified()
}