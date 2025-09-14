package com.mamm.mammapps.data.repository

import android.content.Context
import android.net.Uri
import com.example.openstream_flutter_rw.data.model.STokenData
import com.example.openstream_flutter_rw.data.security.AES128KeyDecryptor
import com.example.openstream_flutter_rw.data.security.AES256Encryptor
import com.mamm.mammapps.data.config.Config
import com.mamm.mammapps.data.datasource.remote.RemoteDatasource
import com.mamm.mammapps.data.di.DeviceSerialQualifier
import com.mamm.mammapps.data.di.DeviceTypeQualifier
import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.data.model.player.JwTokenData
import com.mamm.mammapps.data.session.SessionManager
import com.mamm.mammapps.domain.interfaces.TokenRepository
import com.mamm.mammapps.util.AppConstants.Companion.STOKEN_PARAM_NAME
import com.mamm.mammapps.util.cleanUrl
import dagger.hilt.android.qualifiers.ApplicationContext
import io.jsonwebtoken.Jwts
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.spec.PKCS8EncodedKeySpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject
import io.jsonwebtoken.SignatureAlgorithm

class TokenRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context,
    private val remoteDatasource: RemoteDatasource,
    @DeviceSerialQualifier private val deviceSerial: String,
    @DeviceTypeQualifier private val deviceType: String,
    private val sessionManager: SessionManager,
    private val logger: Logger
) : TokenRepository {

    companion object {
        private const val TAG = "TokenRepositoryImpl"
        const val JWTOKEN_EXPIRATION_TIME_SECS = 12 * 60 * 60 //12 horas
        const val STOKEN_EXPIRATION_TIME_SECS = 20
        const val IP_CACHE_DURATION_MS = 12 * 60 * 5 * 60 * 1000L // 12 horas de cache
    }

    // Cache de IP con timestamp para renovación
    @Volatile
    private var cachedIp: String? = null

    @Volatile
    private var ipCacheTime: Long = 0

    private val privateKey: PrivateKey by lazy {
        loadPrivateKeyFromAssets(context)
    }

    @Volatile
    private var _k2DecryptedKey: String? = null

    @Volatile
    private var _k1EncryptedKey: ByteArray? = null

    private val k1KeyEncrypted: Result<ByteArray>
        get() = if (_k1EncryptedKey != null) {
            Result.success(_k1EncryptedKey!!)
        } else {
            Result.failure(Exception("K1 key not available"))
        }

    /**
     * Refresca la IP desde la red y actualiza cache
     */
    override suspend fun refreshIp(): Result<Unit> {
        return runCatching {
            remoteDatasource.getCurrentUserIp().also {
                cachedIp = it
                ipCacheTime = System.currentTimeMillis()
            }
            logger.debug(TAG, "refreshIp IP refrescada con éxito")
            Unit
        }.onFailure { excp ->
            logger.debug(TAG, "refreshIp ❌ No se pudo refrescar la IP")
            Result.failure<Unit>(excp)
        }
    }

    // Verifica si el cache está disponible y válido
    fun isCacheValid(): Boolean {
        val currentTime = System.currentTimeMillis()
        return cachedIp != null && (currentTime - ipCacheTime) <= IP_CACHE_DURATION_MS
    }

    private suspend fun decryptK2Key(url: String): Result<String> {
        return runCatching {
            val uri = Uri.parse(url)
            val stoken = uri.getQueryParameter("stoken")

            if (stoken.isNullOrEmpty()) {
                logger.debug(TAG, "decryptK2Key ❌ No se encontró stoken en la URL")
                throw Exception("Token no encontrado en la URL")
            }

            _k2DecryptedKey = withContext(Dispatchers.IO) {
                AES128KeyDecryptor.decryptServerKey(stoken)
            }

            // Verifica que la clave sea válida
            if (!_k2DecryptedKey.isNullOrEmpty()) {
//                d("✅ Clave 256-bit válida: ${_k2DecryptedKey!!.substring(0, 8)}...")

                _k2DecryptedKey!! // Devuelve la clave descifrada
            } else {
                logger.debug(TAG, "❌ Clave inválida: ${_k2DecryptedKey?.length ?: 0} caracteres")
                throw Exception("Clave inválida: ${_k2DecryptedKey?.length ?: 0} caracteres")
            }
        }.onFailure { e ->
            logger.debug(TAG, "Error procesando URL: ${e.message}")
        }
    }

    /**Obtiene la clave K1 encriptada en formato hexadecimal*/
    private suspend fun encryptK1Key(decryptedKey: String): Result<Unit> {
        return runCatching {
            _k1EncryptedKey = withContext(Dispatchers.IO) {
                AES256Encryptor.encryptReturnByteArray(decryptedKey)
            }
        }
    }

    override suspend fun storeK1KeyEncrypted(url: String): Result<Unit> {
        if (!url.contains(STOKEN_PARAM_NAME)) {
            logger.info(TAG, "storeK1KeyEncrypted ⚠️ No se encontró stoken en la URL")
            return Result.success(Unit)
        }

        return runCatching {
            val decryptedK2Key = decryptK2Key(url).getOrThrow()
            //d("Clave procesada exitosamente")

            encryptK1Key(decryptedK2Key).onSuccess {
                //d("✅ Clave almacenada")
            }
        }
    }


    private fun loadPrivateKeyFromAssets(context: Context): PrivateKey {
        try {
            val inputStream = context.assets.open("user_rsa.prv")
            val pemContent = inputStream.bufferedReader().use { it.readText() }
            inputStream.close()

            // Extraer la parte Base64 (quitar cabeceras PEM)
            val privateKeyPEM = pemContent
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replace("-----BEGIN RSA PRIVATE KEY-----", "")
                .replace("-----END RSA PRIVATE KEY-----", "")
                .replace("\\s+".toRegex(), "") // Eliminar espacios en blanco

            // Decodificar Base64
            val keyBytes = android.util.Base64.decode(privateKeyPEM, android.util.Base64.DEFAULT)

            // Crear clave privada
            val keySpec = PKCS8EncodedKeySpec(keyBytes)
            val keyFactory = KeyFactory.getInstance("RSA") // O "EC" si es una clave EC
            return keyFactory.generatePrivate(keySpec)
        } catch (e: Exception) {
            throw RuntimeException("Error loading private key: ${e.message}", e)
        }
    }

    override fun generateJwtToken(contentID: String, eventType: String): String {
        val currentTimeSeconds = System.currentTimeMillis() / 1000

        val tokenData = JwTokenData(
            uID = sessionManager.loginData?.userId ?: 0,
            cID = contentID,
            sType = eventType,
            dvID = deviceSerial,
            dvTag = deviceType.toIntOrNull() ?: 0,
            opName = Config.operatorNameDRM
        )

        return Jwts.builder()
            .setHeaderParam("alg", SignatureAlgorithm.RS256.value)
            // Convertir el objeto TokenData a un Map para el claim "dat"
            .claim(
                "dat", mapOf(
                    "uID" to tokenData.uID,
                    "cID" to tokenData.cID,
                    "sType" to tokenData.sType,
                    "dvID" to tokenData.dvID,
                    "dvTag" to tokenData.dvTag,
                    "opName" to tokenData.opName
                )
            )
            .claim("iat", currentTimeSeconds)
            .claim("exp", currentTimeSeconds + JWTOKEN_EXPIRATION_TIME_SECS) // 12 horas desde ahora
            .signWith(privateKey, SignatureAlgorithm.RS256)
            .compact()
    }

    override fun generateSToken(url: String): Result<String> {

        val tokenData = STokenData(
            uip = cachedIp ?: throw IllegalStateException("generateSToken IP no disponible en caché"),
            url = cleanUrl(url, parameterName = STOKEN_PARAM_NAME)
        )

        return k1KeyEncrypted
            .mapCatching { keyByteArray ->
                val currentTimeSeconds = System.currentTimeMillis() / 1000

//                d("generateSToken Generando SToken $tokenData")
//                d("generateSToken KeyString es $keyByteArray")

                val secretKey = SecretKeySpec(keyByteArray, "HmacSHA256")

                val jwtString = Jwts.builder()
                    .claim("uip", tokenData.uip)
                    .claim("url", tokenData.url)
                    .claim("exp", currentTimeSeconds + STOKEN_EXPIRATION_TIME_SECS)
                    .signWith(secretKey, Jwts.SIG.HS256)
                    .compact()

                jwtString
            }
    }


}