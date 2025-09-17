package com.mamm.mammapps.data.di

import android.content.Context
import com.mamm.mammapps.data.local.SecurePreferencesManager
import com.mamm.mammapps.data.logger.Logger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SecurePreferencesModule {

    @Provides
    @Singleton
    fun provideSecurePreferencesManager(@ApplicationContext context: Context, logger: Logger): SecurePreferencesManager {
        return SecurePreferencesManager(context, logger)
    }
}