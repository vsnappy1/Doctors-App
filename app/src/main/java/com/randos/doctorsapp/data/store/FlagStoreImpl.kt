package com.randos.doctorsapp.data.store

import android.content.SharedPreferences
import com.randos.domain.store.FlagStore
import javax.inject.Inject

class FlagStoreImpl @Inject constructor(private val sharedPreferences: SharedPreferences) :
    FlagStore {

    companion object {
        private const val IS_FIRST_TIME_OTP_VERIFIED = "is_first_time_otp_verified"
    }

    override fun hasOtpBeenVerifiedBefore(): Boolean {
        return sharedPreferences.getBoolean(IS_FIRST_TIME_OTP_VERIFIED, false)
    }

    override fun markOtpAsVerified() {
        sharedPreferences.edit().putBoolean(IS_FIRST_TIME_OTP_VERIFIED, true).apply()
    }
}