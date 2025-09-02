package com.mamm.mammapps.data.di

import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.data.logger.SimpleLogger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LoggerModule {

    @Provides
    @Singleton
    fun provideLogger(): Logger {
        return SimpleLogger()
    }
}
