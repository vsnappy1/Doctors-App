package com.randos.doctorsapp.data.store

import android.content.SharedPreferences
import com.randos.domain.store.TokenStore
import javax.inject.Inject


class TokenStoreImpl @Inject constructor(private val sharedPreferences: SharedPreferences) :
    TokenStore {

    companion object {
        private const val AUTH_TOKEN = "auth_token"
    }

    override fun getToken(): String? {
        if (sharedPreferences.contains(AUTH_TOKEN)) {
            return sharedPreferences.getString(AUTH_TOKEN, "")
        }
        return null
    }

    override fun setToken(token: String) {
        sharedPreferences.edit().putString(AUTH_TOKEN, token).apply()
    }
}