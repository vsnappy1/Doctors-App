package com.randos.doctorsapp.di

import com.randos.doctorsapp.data.manager.AccelerometerManagerImpl
import com.randos.doctorsapp.data.manager.BackgroundLocationManagerImpl
import com.randos.doctorsapp.data.manager.ContactManagerImpl
import com.randos.doctorsapp.data.manager.DownloadManagerImpl
import com.randos.domain.manager.LocationManager
import com.randos.doctorsapp.data.manager.LocationManagerImpl
import com.randos.domain.manager.OtpManager
import com.randos.doctorsapp.data.manager.OtpManagerImpl
import com.randos.doctorsapp.utils.manager.PermissionManagerImpl
import com.randos.domain.manager.AccelerometerManager
import com.randos.domain.manager.BackgroundLocationManager
import com.randos.domain.manager.ContactManager
import com.randos.domain.manager.DownloadManager
import com.randos.domain.manager.PermissionManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
@Suppress("Unused")
abstract class ManagerModule {

    @Binds
    abstract fun bindLocationManager(locationManager: LocationManagerImpl): LocationManager

    @Binds
    abstract fun bindOtpManager(otpManager: OtpManagerImpl): OtpManager

    @Binds
    abstract fun bindPermissionManager(permissionManager: PermissionManagerImpl): PermissionManager

    @Binds
    abstract fun bindAccelerometerManager(accelerometerManager: AccelerometerManagerImpl): AccelerometerManager

    @Binds
    abstract fun bindDownloadManager(downloadManager: DownloadManagerImpl): DownloadManager

    @Binds
    abstract fun bindBackgroundLocationManager(backgroundLocationManager: BackgroundLocationManagerImpl): BackgroundLocationManager

    @Binds
    abstract fun bindContactManager(contactManager: ContactManagerImpl): ContactManager
}