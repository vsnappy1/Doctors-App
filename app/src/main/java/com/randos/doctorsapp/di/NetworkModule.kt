package com.randos.doctorsapp.di

import com.randos.doctorsapp.data.network.ApiService
import com.randos.doctorsapp.data.network.ApiServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
@Suppress("Unused")
abstract class NetworkModule {

    @Binds
    @Singleton
    abstract fun bindApiService(apiService: ApiServiceImpl): ApiService
}