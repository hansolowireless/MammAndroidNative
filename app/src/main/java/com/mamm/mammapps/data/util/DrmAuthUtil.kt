package com.mamm.mammapps.data.util

import android.util.Base64
import java.net.URLEncoder
import java.time.Duration
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.crypto.Cipher
import javax.crypto.Mac
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object DrmAuthUtil {

    private const val DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"
    private const val HMAC_ALGORITHM = "HmacSHA1"
    private const val AES_ALGORITHM = "AES"
    private const val CIPHER_TRANSFORMATION = "AES/CBC/PKCS7Padding"
    private const val HEX_FORMAT = "%02x"
    private const val CHARSET_UTF8 = "UTF-8"

    private const val JSON_KEY_DEVICE_ID = "\"deviceID\""
    private const val JSON_KEY_SIGNATURE = "\"signature\""
    private const val JSON_KEY_EXPIRE = "\"expire\""
    private const val JSON_KEY_CONTENT_TYPE = "\"contentType\""
    private const val JSON_KEY_DEVICE_TYPE = "\"deviceType\""

    fun generateAuthPayload(
        eventType: String,
        deviceTypeStr: String,
        userName: String,
        deviceSerial: String,
        sessionToken: String?,
        drmSecretKey64: ByteArray,
        drmiV64: ByteArray,
        expireDuration: Duration = Duration.ZERO,
        expireInTwoHours: Boolean = false,
        urlEncode: Boolean,
        base64BeforeAes: Boolean
    ): String {
        val dateFormat = DateTimeFormatter.ofPattern(DATE_FORMAT)
        val nowString = dateFormat.format(ZonedDateTime.now())

        val expireString = if (expireInTwoHours) {
            dateFormat.format(ZonedDateTime.now(ZoneOffset.UTC).plusHours(2))
        } else {
            nowString
        }

        val signatureStrToHash = "$userName|$deviceTypeStr|$deviceSerial|$expireString"
        val key = sessionToken?.toByteArray(Charsets.UTF_8)
        val signatureBytes = signatureStrToHash.toByteArray(Charsets.UTF_8)

        val mac = Mac.getInstance(HMAC_ALGORITHM)
        mac.init(SecretKeySpec(key, HMAC_ALGORITHM))
        val digest = mac.doFinal(signatureBytes)
        val digestString = digest.joinToString("") { HEX_FORMAT.format(it) }

        // IMPORTANTE: este formato multilinea debe coincidir exactamente con el original
        // de PlaybackRepositoryImpl para que el AES produzca el mismo ciphertext
        val authString = "{\n" +
                "                $JSON_KEY_DEVICE_ID:\"$deviceSerial\",\n" +
                "                $JSON_KEY_SIGNATURE:\"$digestString\",\n" +
                "                $JSON_KEY_EXPIRE:\"$expireString\",\n" +
                "                $JSON_KEY_CONTENT_TYPE:\"$eventType\",\n" +
                "                $JSON_KEY_DEVICE_TYPE:\"$deviceTypeStr\"\n" +
                "                }"

        val bytesToEncrypt = if (base64BeforeAes) {
            val authBase64 = Base64.encodeToString(authString.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
            authBase64.toByteArray(Charsets.UTF_8)
        } else {
            authString.toByteArray(Charsets.UTF_8)
        }

        val cipher = Cipher.getInstance(CIPHER_TRANSFORMATION)
        val secretKeySpec = SecretKeySpec(drmSecretKey64, AES_ALGORITHM)
        val ivSpec = IvParameterSpec(drmiV64)
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivSpec)

        val authEncrypted = cipher.doFinal(bytesToEncrypt)
        var finalAuthenticationString = Base64.encodeToString(authEncrypted, Base64.NO_WRAP)

        if (urlEncode) {
            finalAuthenticationString = URLEncoder.encode(finalAuthenticationString, CHARSET_UTF8)
        }

        return finalAuthenticationString
    }
}
