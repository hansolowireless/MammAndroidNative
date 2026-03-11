package com.mamm.mammapps.data.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.mamm.mammapps.remote.NullStringAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder()
        .registerTypeAdapter(String::class.java, NullStringAdapter())
        .create()
}