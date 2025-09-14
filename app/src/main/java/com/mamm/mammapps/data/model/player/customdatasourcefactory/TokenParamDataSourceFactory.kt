package com.example.openstream_flutter_rw.data.model.customdatasourcefactory

import android.net.Uri
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.HttpDataSource
import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.domain.interfaces.TokenRepository
import com.mamm.mammapps.util.replaceQueryParameter
import javax.inject.Inject

enum class TokenMode {
    STOKEN,
    RTOKEN,
    NONE
}

class TokenParamDataSourceFactory @Inject constructor (
    private val tokenRepository: TokenRepository,
    private val logger: Logger
) : HttpDataSource.Factory {

    companion object {
        private const val TAG = "TokenParamDataSource"
        private const val RTOKEN = "rtoken"
        private const val STOKEN = "stoken"
        private const val MAX_RETRIES = 3  // Máximo de reintentos por expiración
    }

    fun d(mensaje: String) = logger.debug(TAG, mensaje)
    fun e(mensaje: String) = logger.debug(TAG, mensaje)

    private var tokenMode: TokenMode = TokenMode.NONE
    private var rtoken: String = ""

    // Base factory
    private val baseFactory = DefaultHttpDataSource.Factory()

    override fun createDataSource(): HttpDataSource {
        return object : DefaultHttpDataSource() {

            override fun open(dataSpec: DataSpec): Long {
                var modifiedUri: Uri = dataSpec.uri
                var attempts = 0

                // Determina el modo una vez (basado en la URI inicial)
                if (tokenMode == TokenMode.NONE) {
                    if (modifiedUri.toString().contains(RTOKEN)) {
                        tokenMode = TokenMode.RTOKEN
                        rtoken = modifiedUri.getQueryParameter(RTOKEN) ?: ""
                    } else if (modifiedUri.toString().contains(STOKEN)) {
                        tokenMode = TokenMode.STOKEN
                    }
                }

                while (attempts < MAX_RETRIES) {
                    // Inserta o refresca el token según el modo
                    if (tokenMode == TokenMode.RTOKEN && rtoken.isNotEmpty() && !modifiedUri.queryParameterNames.contains(RTOKEN)) {
                        modifiedUri = modifiedUri.buildUpon().appendQueryParameter(RTOKEN, rtoken).build()
                    } else if (tokenMode == TokenMode.STOKEN) {
                        runCatching {
                            val sToken = tokenRepository.generateSToken(modifiedUri.toString()).getOrThrow()
                            modifiedUri = modifiedUri.replaceQueryParameter(STOKEN, sToken)
                            //d("SToken insertado/refrescado en URI: $modifiedUri")
                        }.onFailure { exception ->
                            throw exception
                        }
                    }

                    // Crea el DataSpec modificado
                    val modifiedDataSpec = dataSpec.buildUpon()
                        .setUri(modifiedUri)
                        .build()

                    //d("Solicitando chunk: $modifiedUri")

                    try {
                        return super.open(modifiedDataSpec)
                    } catch (e: HttpDataSource.HttpDataSourceException) {
                        // Verifica si es por token expirado (asumiendo código 401 o 403; ajusta según tu backend)
                        if (e is HttpDataSource.InvalidResponseCodeException &&
                            (e.responseCode == 401 || e.responseCode == 403)
                        ) {
                            e("Error de token expirado (${e.responseCode}), reintentando ($attempts/$MAX_RETRIES)")
                            attempts++
                            // Para STOKEN, se refrescará en la siguiente iteración
                            // Para RTOKEN, si necesita refresco, agrega lógica aquí (ej. refresca rtoken)
                            if (tokenMode == TokenMode.RTOKEN) {
                                // Si rtoken expira, implementa refresco similar si aplica
                                // Por ahora, asume que no se refresca y falla después de reintentos
                            }
                        } else {
                            // Otros errores, relanza
                            throw e
                        }
                    }
                }

                throw HttpDataSource.HttpDataSourceException(
                    "Máximos reintentos alcanzados por token expirado",
                    dataSpec,
                    HttpDataSource.HttpDataSourceException.TYPE_OPEN
                )
            }
        }
    }

    override fun setDefaultRequestProperties(defaultRequestProperties: Map<String, String>): TokenParamDataSourceFactory {
        // Delegate configuration to baseFactory
        baseFactory.setDefaultRequestProperties(defaultRequestProperties)
        return this
    }
}