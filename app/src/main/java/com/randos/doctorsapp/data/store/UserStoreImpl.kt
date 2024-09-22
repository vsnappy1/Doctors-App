package com.randos.doctorsapp.data.store

import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.randos.domain.model.User
import com.randos.domain.store.UserStore
import javax.inject.Inject

class UserStoreImpl @Inject constructor(private val sharedPreferences: SharedPreferences) :
    UserStore {
    companion object {
        private const val USER = "user"
        private const val TAG = "UserStoreImpl"
    }

    override fun getUser(): User? {
        if (sharedPreferences.contains(USER)) {
            try {
                val jsonUser = sharedPreferences.getString(USER, "")
                return Gson().fromJson(jsonUser, User::class.java)
            } catch (exception: Exception) {
                Log.i(TAG, "Failed to get user details: ", exception)
            }
        }
        return null
    }

    override fun setUser(user: User) {
        try {
            val jsonUser = Gson().toJson(user)
            sharedPreferences.edit().putString(USER, jsonUser).apply()
        } catch (exception: Exception) {
            Log.i(TAG, "Failed to store user details: ", exception)
        }
    }
}