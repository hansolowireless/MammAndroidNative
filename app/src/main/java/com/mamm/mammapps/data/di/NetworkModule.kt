package com.mamm.mammapps.data.di

import com.mamm.mammapps.BuildConfig
import com.mamm.mammapps.data.config.Config
import com.mamm.mammapps.data.local.SecurePreferencesManager
import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.data.session.SessionManager
import com.mamm.mammapps.remote.ApiService
import com.mamm.mammapps.remote.interceptor.AuthInterceptor
import com.mamm.mammapps.remote.interceptor.DynamicUrlInterceptor
import com.mamm.mammapps.remote.interceptor.QosAuthInterceptor
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

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BaseUrlApi

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class NoBaseUrlApi

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class NoBaseUrlNoRedirectApi

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class QosApi

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class CustomContentApi

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(logger: Logger): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor { message ->
            logger.debug("OkHttpClient", message)
        }.apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BASIC
            } else {
                HttpLoggingInterceptor.Level.BASIC
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
    fun provideIdmRetrofit(okHttpClient: OkHttpClient, sessionManager: SessionManager): Retrofit {
        val idmClient = okHttpClient.newBuilder()
            .addInterceptor(AuthInterceptor(sessionManager))
            .addInterceptor(DynamicUrlInterceptor())
            .build()

        return Retrofit.Builder()
            .baseUrl(Config.idmUrl)
            .client(idmClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @SearchApi
    @Provides
    @Singleton
    fun provideSearchRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val searchClient = okHttpClient.newBuilder()
            .addInterceptor(DynamicUrlInterceptor())
            .build()

        return Retrofit.Builder()
            .baseUrl(Config.searchUrl)
            .client(searchClient)
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

    @BaseUrlApi
    @Provides
    @Singleton
    fun provideBaseUrlRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val baseUrlClient = okHttpClient.newBuilder()
            .addInterceptor(DynamicUrlInterceptor())
            .build()

        return Retrofit.Builder()
            .baseUrl(Config.baseUrl)
            .client(baseUrlClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Nueva instancia de Retrofit sin baseUrl para HomeContent
    @NoBaseUrlApi
    @Provides
    @Singleton
    fun provideHomeContentRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://example.com/") // URL dummy requerida por Retrofit
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @NoBaseUrlNoRedirectApi
    @Provides
    @Singleton
    fun provideNoRedirectContentRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val noRedirectClient = okHttpClient.newBuilder()
            .followRedirects(false)
            .followSslRedirects(false)
            .build()

        return Retrofit.Builder()
            .baseUrl("https://example.com/")
            .client(noRedirectClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @QosApi
    @Provides
    @Singleton
    fun provideQosRetrofit(
        okHttpClient: OkHttpClient,
        securePreferencesManager: SecurePreferencesManager,
        sessionManager: SessionManager
    ): Retrofit {
        val qosClient = okHttpClient.newBuilder()
            .addInterceptor(QosAuthInterceptor(sessionManager, securePreferencesManager))
            .build()

        return Retrofit.Builder()
            .baseUrl(Config.qosUrl)
            .client(qosClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @CustomContentApi
    @Provides
    @Singleton
    fun provideBookmarksRetrofit(
        okHttpClient: OkHttpClient,
        sessionManager: SessionManager
    ): Retrofit {
        val bookmarksClient = okHttpClient.newBuilder()
            .addInterceptor(AuthInterceptor(sessionManager))
            .build()
        return Retrofit.Builder()
            .baseUrl(Config.customContentUrl)
            .client(bookmarksClient)
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

    @BaseUrlApi
    @Provides
    @Singleton
    fun provideBaseUrlApi(@BaseUrlApi retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    // Nuevo provider para HomeContentApi
    @NoBaseUrlApi
    @Provides
    @Singleton
    fun provideHomeContentApi(@NoBaseUrlApi retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @NoBaseUrlNoRedirectApi
    @Provides
    @Singleton
    fun provideNoRedirectContentApi(@NoBaseUrlNoRedirectApi retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @QosApi
    @Provides
    @Singleton
    fun provideQosApi(@QosApi retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @CustomContentApi
    @Provides
    @Singleton
    fun provideBookmarksApi(@CustomContentApi retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

}