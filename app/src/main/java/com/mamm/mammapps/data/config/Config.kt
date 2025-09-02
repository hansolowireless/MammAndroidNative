package com.mamm.mammapps.data.config

import com.mamm.mammapps.BuildConfig
import com.mamm.mammapps.data.model.LocatorResponse

object Config {

    // variables que se pueden actualizar en caliente (solo se usan si DYNAMIC_URLS = true)
    private var dynamicBaseUrl: String? = null
    private var dynamicIdmUrl: String? = null
    private var dynamicSearchUrl: String? = null
    private var dynamicPasswordRecUrl: String? = null

    // valores fijos desde BuildConfig
    val locatorUrl: String get() = BuildConfig.LOCATOR_URL
    val metricsUrl: String get() = BuildConfig.METRICS_URL

    val shouldUseDynamicUrls: Boolean get() = BuildConfig.DYNAMIC_URLS

    val baseUrl: String
        get() = if (BuildConfig.DYNAMIC_URLS && dynamicBaseUrl != null) {
            dynamicBaseUrl!!
        } else {
            BuildConfig.BASE_URL
        }

    val idmUrl: String
        get() = if (BuildConfig.DYNAMIC_URLS && dynamicIdmUrl != null) {
            dynamicIdmUrl!!
        } else {
            BuildConfig.IDM_URL
        }

    val searchUrl: String
        get() = if (BuildConfig.DYNAMIC_URLS && dynamicSearchUrl != null) {
            dynamicSearchUrl!!
        } else {
            BuildConfig.SEARCH_URL
        }

    val passwordRecoveryUrl: String
        get() = if (BuildConfig.DYNAMIC_URLS && dynamicPasswordRecUrl != null) {
            dynamicPasswordRecUrl!!
        } else {
            BuildConfig.PASSWORD_REC_URL
        }

    /**
     * Método para actualizar las URLs dinámicas en caliente.
     * Solo tiene efecto si el flavor tiene `DYNAMIC_URLS = true`.
     */
    fun updateDynamicUrls(
        locatorResponse: LocatorResponse
    ) {
        dynamicBaseUrl = locatorResponse.data.endpointStatic ?: dynamicBaseUrl
        dynamicIdmUrl = locatorResponse.data.endpointIdm ?: dynamicIdmUrl
        dynamicSearchUrl = locatorResponse.data.endpointSearch ?: dynamicSearchUrl
        dynamicPasswordRecUrl = locatorResponse.data.endpointManager ?: dynamicPasswordRecUrl
    }
}