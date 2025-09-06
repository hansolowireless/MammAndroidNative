package com.mamm.mammapps.data.di

import android.app.UiModeManager
import android.content.Context
import android.content.res.Configuration
import android.provider.Settings
import com.mamm.mammapps.util.isAndroidTV
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier

// DeviceQualifiers.kt
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DeviceTypeQualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DeviceSerialQualifier

@Module
@InstallIn(SingletonComponent::class)
object DeviceModule {

    @Provides
    @DeviceTypeQualifier
    fun provideDeviceType(@ApplicationContext context: Context): String {
        return if (isAndroidTV(context)) "504" else "154"
    }

    @Provides
    @DeviceSerialQualifier
    fun provideDeviceSerial(@ApplicationContext context: Context): String {
        return Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        ) ?: "unknown_device"
    }

}
