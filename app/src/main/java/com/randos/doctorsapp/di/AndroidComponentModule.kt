package com.randos.doctorsapp.di

import android.app.Application
import android.content.Context
import android.location.LocationManager
import android.os.Looper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AndroidComponentModule {

    @Provides
    fun provideLocationManager(application: Application): LocationManager {
        return application.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    @Provides
    fun provideMainLooper(): Looper {
        return Looper.getMainLooper()
    }
}