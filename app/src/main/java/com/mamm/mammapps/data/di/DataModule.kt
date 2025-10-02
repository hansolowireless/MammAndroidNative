package com.mamm.mammapps.data.di

import com.mamm.mammapps.data.datasource.local.LocalDataSource
import com.mamm.mammapps.data.datasource.local.LocalDataSourceImpl
import com.mamm.mammapps.data.repository.CustomContentRepositoryImpl
import com.mamm.mammapps.data.repository.EPGRepositoryImpl
import com.mamm.mammapps.data.repository.LoginRepositoryImpl
import com.mamm.mammapps.data.repository.MammRepositoryImpl
import com.mamm.mammapps.data.repository.PlaybackRepositoryImpl
import com.mamm.mammapps.data.repository.TokenRepositoryImpl
import com.mamm.mammapps.domain.interfaces.CustomContentRepository
import com.mamm.mammapps.domain.interfaces.TokenRepository
import com.mamm.mammapps.domain.interfaces.EPGRepository
import com.mamm.mammapps.domain.interfaces.LoginRepository
import com.mamm.mammapps.domain.interfaces.MammRepository
import com.mamm.mammapps.domain.interfaces.PlaybackRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindLocalDataSource(
        localDataSource: LocalDataSourceImpl
    ): LocalDataSource

    @Binds
    @Singleton
    abstract fun bindLoginRepository(
        loginRepository: LoginRepositoryImpl
    ): LoginRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        impl: MammRepositoryImpl
    ): MammRepository

    @Binds
    @Singleton
    abstract fun bindEPGRepository(
        impl: EPGRepositoryImpl
    ): EPGRepository

    @Binds
    @Singleton
    abstract fun bindPlaybackRepository(
        impl: PlaybackRepositoryImpl
    ): PlaybackRepository

    @Binds
    @Singleton
    abstract fun bindTokenRepository(
        impl: TokenRepositoryImpl
    ): TokenRepository

    @Binds
    @Singleton
    abstract fun bindCustomContentRepository(
        impl: CustomContentRepositoryImpl
    ): CustomContentRepository


}