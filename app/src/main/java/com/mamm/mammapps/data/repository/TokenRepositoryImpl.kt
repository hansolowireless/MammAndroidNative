package com.mamm.mammapps.data.repository

import android.util.Base64
import androidx.core.net.toUri
import com.example.openstream_flutter_rw.data.security.AES128KeyDecryptor
import com.example.openstream_flutter_rw.data.security.AES256Encryptor
import com.mamm.mammapps.data.config.Config
import com.mamm.mammapps.data.datasource.local.LocalDataSource
import com.mamm.mammapps.data.datasource.remote.RemoteDatasource
import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.data.model.player.STokenData
import com.mamm.mammapps.data.model.player.streamvx.StreamVxTokenRequest
import com.mamm.mammapps.data.session.SessionManager
import com.mamm.mammapps.data.util.DrmAuthUtil
import com.mamm.mammapps.domain.interfaces.TokenRepository
import com.mamm.mammapps.util.AppConstants.Companion.STOKEN_PARAM_NAME
import com.mamm.mammapps.util.cleanUrl
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.time.Duration
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject

class TokenRepositoryImpl @Inject constructor(
    private val remoteDatasource: RemoteDatasource,
    private val localDataSource: LocalDataSource,
    private val sessionManager: SessionManager,
    private val logger: Logger
) : TokenRepository {

    companion object {
        private const val TAG = "TokenRepositoryImpl"
        const val JWTOKEN_EXPIRATION_TIME_SECS = 12 * 60 * 60 // 12 horas de vigencia
        const val STOKEN_EXPIRATION_TIME_SECS = 20
        const val IP_CACHE_DURATION_MS = 12 * 60 * 5 * 60 * 1000L // 12 horas de cache

        // JWT Header & Crypto
        private const val JWT_HEADER_ALG = "alg"
        private const val JWT_ALG_HS256 = "HS256"
        private const val HMAC_SHA256 = "HmacSHA256"
        private const val JWT_TOKEN_VALIDITY_SECONDS = 120 // 2 minutos

        // JWT Claims
        private const val CLAIM_USER_ID = "userID"
        private const val CLAIM_STREAM_NAME = "streamName"
        private const val CLAIM_DEVICE_ID = "deviceID"
        private const val CLAIM_DEVICE_TYPE = "deviceType"
        private const val CLAIM_OPERATOR = "operator"
        private const val CLAIM_IAT = "iat"
        private const val CLAIM_EXP = "exp"
        private const val CLAIM_UIP = "uip"
        private const val CLAIM_URL = "url"

        //JWT request json
        private const val JSON_KEY_USER_ID = "\"UserID\""
        private const val JSON_KEY_STREAM_NAME = "\"StreamName\""
        private const val JSON_KEY_OPERATOR = "\"Operator\""
        private const val JSON_KEY_AUTH_STRING = "\"AuthenticationString\""
    }

    // Cache de IP con timestamp para renovación
    @Volatile
    private var cachedIp: String? = null

    @Volatile
    private var ipCacheTime: Long = 0

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
            val uri = url.toUri()
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

    override suspend fun generateJwtToken(contentID: String, eventType: String, chromecast: Boolean): Result<String> {
        return runCatching {
            var finalToken: String? = null
            var retryCount = 0
            var lastException: Throwable? = null

            while (retryCount < 3 && finalToken == null) {
                try {
                    val userName = localDataSource.getUserCredentials().first ?: ""
                    val userID = sessionManager.loginData?.userId ?: 0
                    val operatorName = Config.operatorNameDRM
                    val deviceTypeInt = (if (chromecast) localDataSource.getChromecastDeviceType().toIntOrNull() else localDataSource.getDeviceType().toIntOrNull()) ?: 0
                    val deviceSerial = localDataSource.getDeviceSerial()

                    // 1. Obtener AuthenticationString (el AES del JSON de auth)
                    val authenticationString = DrmAuthUtil.generateAuthPayload(
                        eventType = eventType,
                        deviceTypeStr = deviceTypeInt.toString(),
                        userName = userName,
                        deviceSerial = deviceSerial,
                        sessionToken = sessionManager.loginData?.token,
                        drmSecretKey64 = localDataSource.getDrmSecretKey64(),
                        drmiV64 = localDataSource.getDrmiV64(),
                        expireDuration = Duration.ofHours(2),
                        expireInTwoHours = true,
                        urlEncode = true,
                        base64BeforeAes = true
                    )

                    val currentTimeSeconds = System.currentTimeMillis() / 1000
                    val jwtSecret = localDataSource.getDrmJwtSecretKey()

                    // 4. Firma JWT (Signature)
                    val signature = Jwts.builder()
                        .setHeaderParam(JWT_HEADER_ALG, JWT_ALG_HS256)
                        .claim(CLAIM_USER_ID, userID)
                        .claim(CLAIM_STREAM_NAME, contentID)
                        .claim(CLAIM_DEVICE_ID, deviceSerial)
                        .claim(CLAIM_DEVICE_TYPE, deviceTypeInt)
                        .claim(CLAIM_OPERATOR, operatorName)
                        .claim(CLAIM_IAT, currentTimeSeconds)
                        .claim(CLAIM_EXP, currentTimeSeconds + JWT_TOKEN_VALIDITY_SECONDS)
                        .signWith(SecretKeySpec(jwtSecret, HMAC_SHA256), SignatureAlgorithm.HS256)
                        .compact()

                    val requestJson = "{\n" +
                            "                $JSON_KEY_USER_ID:\"$userID\",\n" +
                            "                $JSON_KEY_STREAM_NAME:\"$contentID\",\n" +
                            "                $JSON_KEY_OPERATOR:\"$operatorName\",\n" +
                            "                $JSON_KEY_AUTH_STRING:\"$authenticationString\"\n" +
                            "                }"

                    val requestVerification = Base64.encodeToString(requestJson.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)

                    val streamVxTokenRequest = StreamVxTokenRequest(
                        requestVerification = requestVerification,
                        signature = signature
                    )

                    logger.info(TAG, "generateJwtToken: enviando request, $streamVxTokenRequest")

                    val response = remoteDatasource.getxToken(streamVxTokenRequest)


                    finalToken = response.data?.token ?: throw IllegalStateException("Token is null in StreamVxTokenResponse")

                } catch (e: Exception) {
                    lastException = e
                    retryCount++
                    if (retryCount < 3) {
                        logger.warn(TAG, "generateJwtToken: Error getting token, retrying ($retryCount/3)...")
                        delay(300)
                    }
                }
            }
            
            finalToken ?: throw (lastException ?: java.lang.IllegalStateException("Failed to generate token"))
        }
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

                val secretKey = SecretKeySpec(keyByteArray, HMAC_SHA256)

                val jwtString = Jwts.builder()
                    .claim(CLAIM_UIP, tokenData.uip)
                    .claim(CLAIM_URL, tokenData.url)
                    .claim(CLAIM_EXP, currentTimeSeconds + STOKEN_EXPIRATION_TIME_SECS)
                    .signWith(secretKey, Jwts.SIG.HS256)
                    .compact()

                jwtString
            }
    }


}
