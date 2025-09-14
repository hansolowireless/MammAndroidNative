package com.mamm.mammapps.data.model.player.customdatasourcefactory

//import android.util.Log
//import com.example.openstream_flutter_rw.domain.jwt.JwtManager
//import com.example.openstream_flutter_rw.data.model.JwTokenData
//import com.google.android.exoplayer2.upstream.DataSpec
//import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
//import com.google.android.exoplayer2.upstream.HttpDataSource
//
////Esta clase se usaría si metieramos un drmcallback en vez de ponerlo estático en el header
//class DrmDataSourceFactory(
//    private val tokenData: JwTokenData,
//    private val jwtManager: JwtManager
//) : HttpDataSource.Factory {
//
//    companion object {
//        private val TAG = "DRMDataSource"
//        fun d(mensaje: String) = Log.d(TAG, mensaje)
//    }
//
//    // Base factory
//    private val baseFactory = DefaultHttpDataSource.Factory()
//
//    // Propiedades para manejar el token de autorización
//    private var authToken: String? = null
//    private var tokenExpirationTime: Long = 0
//
//    override fun createDataSource(): HttpDataSource {
//        return object : DefaultHttpDataSource("ExoPlayer") {
//            override fun open(dataSpec: DataSpec): Long {
//                Log.d("DRM", "Requesting license from: ${dataSpec.uri}")
//
//                if (System.currentTimeMillis() > tokenExpirationTime) {
//                    refreshAuthToken()
//                }
//
//                // Establecer el header de autorización
//                setRequestProperty("Authorization", "Bearer $authToken")
//                d( "Using auth token for license request")
//
//                return super.open(dataSpec)
//            }
//        }
//    }
//
//    override fun setDefaultRequestProperties(defaultRequestProperties: Map<String, String>): DrmDataSourceFactory {
//        // Delegate configuration to baseFactory
//        baseFactory.setDefaultRequestProperties(defaultRequestProperties)
//        return this
//    }
//
//    // Método privado para renovar el token
//    private fun refreshAuthToken() {
//        authToken = jwtManager.generateJwtToken(tokenData)
//
//        // El token dura 600 segundos (10 min) según tu JwtManager
//        // Renovar 2 minutos antes de que expire
//        val safetyMarginMs = 120000 // 2 minutos en milisegundos
//        tokenExpirationTime = System.currentTimeMillis() + (600 * 1000) - safetyMarginMs
//
//        d("Auth token refreshed, valid until ${tokenExpirationTime}, $authToken")
//    }
//
//
//}