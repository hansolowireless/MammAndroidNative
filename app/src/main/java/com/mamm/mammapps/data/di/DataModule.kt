package com.mamm.mammapps.data.di

import com.mamm.mammapps.data.datasource.local.LocalDataSource
import com.mamm.mammapps.data.datasource.local.LocalDataSourceImpl
import com.mamm.mammapps.data.repository.MammRepositoryImpl
import com.mamm.mammapps.domain.interfaces.MammRepository
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
    abstract fun bindUserRepository(
        impl: MammRepositoryImpl
    ): MammRepository

}