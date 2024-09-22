package com.randos.doctorsapp.di

import com.randos.doctorsapp.data.repository.AppointmentRepositoryImpl
import com.randos.doctorsapp.data.repository.AuthTokenRepositoryImpl
import com.randos.doctorsapp.data.repository.DoctorListRepositoryImpl
import com.randos.doctorsapp.data.repository.HomeRepositoryImpl
import com.randos.doctorsapp.data.repository.developer.LocationStreamRepositoryImpl
import com.randos.doctorsapp.data.repository.OtpRepositoryImpl
import com.randos.doctorsapp.data.repository.SplashRepositoryImpl
import com.randos.doctorsapp.data.repository.developer.ContactRepositoryImpl
import com.randos.doctorsapp.data.repository.developer.FileDownloadRepositoryImpl
import com.randos.doctorsapp.data.repository.developer.FunGroundRepositoryImpl
import com.randos.domain.repository.AppointmentRepository
import com.randos.domain.repository.AuthTokenRepository
import com.randos.domain.repository.ContactRepository
import com.randos.domain.repository.DoctorListRepository
import com.randos.domain.repository.FileDownloadRepository
import com.randos.domain.repository.FunGroundRepository
import com.randos.domain.repository.HomeRepository
import com.randos.domain.repository.LocationStreamRepository
import com.randos.domain.repository.OtpRepository
import com.randos.domain.repository.SplashRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
@Suppress("Unused")
abstract class RepositoryModule {

    @Binds
    abstract fun bindAuthTokenRepository(authTokenRepository: AuthTokenRepositoryImpl): AuthTokenRepository

    @Binds
    abstract fun bindDoctorListRepository(doctorListRepository: DoctorListRepositoryImpl): DoctorListRepository

    @Binds
    abstract fun bindOtpRepository(otpRepository: OtpRepositoryImpl): OtpRepository

    @Binds
    abstract fun bindSplashRepository(splashRepository: SplashRepositoryImpl): SplashRepository

    @Binds
    abstract fun bindAppointmentRepository(appointmentRepository: AppointmentRepositoryImpl): AppointmentRepository

    @Binds
    abstract fun bindHomeRepository(homeRepository: HomeRepositoryImpl): HomeRepository

    @Binds
    abstract fun bindFunGroundRepository(funGroundRepository: FunGroundRepositoryImpl): FunGroundRepository

    @Binds
    abstract fun bindFileDownloadRepository(fileDownloadRepository: FileDownloadRepositoryImpl): FileDownloadRepository

    @Binds
    abstract fun bindLocationStreamRepository(locationStreamRepository: LocationStreamRepositoryImpl): LocationStreamRepository

    @Binds
    abstract fun bindContactRepository(contactRepository: ContactRepositoryImpl): ContactRepository
}