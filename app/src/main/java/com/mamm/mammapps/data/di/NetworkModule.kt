package com.mamm.mammapps.data.di

import com.mamm.mammapps.BuildConfig
import com.mamm.mammapps.data.config.Config
import com.mamm.mammapps.remote.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IdmApi

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class SearchApi

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class LocatorApi

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @IdmApi
    @Provides
    @Singleton
    fun provideIdmRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Config.idmUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @SearchApi
    @Provides
    @Singleton
    fun provideSearchRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Config.searchUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @LocatorApi
    @Provides
    @Singleton
    fun provideLocatorRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Config.locatorUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @IdmApi
    @Provides
    @Singleton
    fun provideIdmApi(@IdmApi retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @SearchApi
    @Provides
    @Singleton
    fun provideSearchApi(@SearchApi retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @LocatorApi
    @Provides
    @Singleton
    fun provideLocatorApi(@LocatorApi retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

}