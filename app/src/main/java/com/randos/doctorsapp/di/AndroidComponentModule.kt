package com.randos.doctorsapp.di

import android.app.Application
import android.app.DownloadManager
import android.content.Context
import android.hardware.SensorManager
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
    fun provideDownloadManager(application: Application): DownloadManager {
        return application.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    }

    @Provides
    fun provideSensorManager(application: Application): SensorManager {
        return application.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    @Provides
    fun provideMainLooper(): Looper {
        return Looper.getMainLooper()
    }
}