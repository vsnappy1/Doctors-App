package com.randos.doctorsapp.di

import android.app.Application
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.randos.doctorsapp.data.store.FlagStoreImpl
import com.randos.doctorsapp.data.store.TokenStoreImpl
import com.randos.doctorsapp.data.store.UserStoreImpl
import com.randos.domain.store.FlagStore
import com.randos.domain.store.TokenStore
import com.randos.domain.store.UserStore
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    fun provideEncryptedSharedPreference(application: Application): SharedPreferences {
        val masterKey: MasterKey = MasterKey.Builder(application)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            application,
            "doctors app",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
}

@Module
@InstallIn(SingletonComponent::class)
@Suppress("Unused")
abstract class DataBindingModule {
    @Binds
    abstract fun bindTokenStore(tokenStore: TokenStoreImpl): TokenStore

    @Binds
    abstract fun bindUserStore(userStore: UserStoreImpl): UserStore

    @Binds
    abstract fun bindFlagStore(flagStore: FlagStoreImpl): FlagStore
}