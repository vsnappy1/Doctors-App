package com.randos.doctorsapp.data.repository

import android.util.Log
import com.randos.doctorsapp.data.network.ApiService
import com.randos.domain.model.User
import com.randos.domain.repository.HomeRepository
import com.randos.domain.store.TokenStore
import com.randos.domain.store.UserStore
import com.randos.domain.type.NetworkResult
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val userStore: UserStore,
    private val tokenStore: TokenStore
) : HomeRepository {

    companion object {
        private const val TAG = "HomeRepositoryImpl"
    }

    override suspend fun getUserDetails(): NetworkResult<User> {
        val storedUser = userStore.getUser()
        return if (storedUser != null) {
            NetworkResult.Success(storedUser)
        } else {
            tokenStore.getToken()?.let { authToken ->
                try {
                    val user = apiService.getUser(authToken)
                    userStore.setUser(user)
                    NetworkResult.Success(user)
                } catch (exception: Exception) {
                    Log.e(TAG, "Failed to fetch doctor list", exception)
                    NetworkResult.Failure("Failed to fetch doctor list", exception)
                }
            } ?: NetworkResult.Failure("User is not validated (i.e. Auth token not present).")
        }
    }
}