package com.mamm.mammapps.data.di

import android.util.Base64
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DrmUrlQualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DrmIVQualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DrmSecretKeyQualifier

@Module
@InstallIn(SingletonComponent::class)
object DrmModule {

    @Provides
    @DrmUrlQualifier
    fun provideDrmUrl(): String {
        return "https://drm.sys.opensport.es/RDRM/index.php/"
    }

    @Provides
    @DrmIVQualifier
    fun provideDrmIV(): ByteArray {
        return Base64.decode("hjhWAQo0VANtLB8b9x5RyA==", Base64.DEFAULT)
    }

    @Provides
    @DrmSecretKeyQualifier
    fun provideDrmSecretKey(): ByteArray {
        return Base64.decode("Wd5NgVL4FgPmZqfPoJxXJw==", Base64.DEFAULT)
    }
}

