package com.example.openstream_flutter_rw.data.security

import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

object AES256Encryptor {
    // 🔑 Datos a cifrar (32 bytes)
    private val keyBytes = byteArrayOf(
        0xCD.toByte(), 0xB1.toByte(), 0x62, 0x53, 0xFD.toByte(), 0xDD.toByte(), 0xE9.toByte(), 0x38,
        0x7F, 0xDE.toByte(), 0x18, 0x5C, 0x21, 0xC0.toByte(), 0x10, 0x62,
        0xA9.toByte(), 0xDB.toByte(), 0x39, 0x10, 0xFD.toByte(), 0x27, 0x68, 0xDA.toByte(),
        0xCA.toByte(), 0xF7.toByte(), 0x76, 0x7C, 0xCE.toByte(), 0xC1.toByte(), 0x21, 0xAC.toByte(),
    )

    private val dataToEncrypt: ByteArray
        get() = keyBytes.map { (it.toInt() xor 0x2A).toByte() }.toByteArray()

    /**
     * Cifra keyBytes usando la clave proporcionada con AES-256 ECB
     * @param encryptionKey String hexadecimal que se usará como clave de cifrado (debe ser de 64 caracteres hex para AES-256)
     * @return String cifrado en Base64
     */
    fun encryptReturnByteArray(encryptionKey: String): ByteArray {
        return try {
            // Validar que la clave tenga 64 caracteres hex (32 bytes = 256 bits)
            if (encryptionKey.length != 64) {
                throw IllegalArgumentException("La clave debe tener exactamente 64 caracteres hexadecimales (32 bytes = 256 bits)")
            }

            val keyForEncryption = hexStringToByteArray(encryptionKey)

            encryptBytes(dataToEncrypt, keyForEncryption)
        } catch (e: Exception) {
            throw Exception("Error cifrando datos: ${e.message}")
        }
    }

    /**
     * Cifra datos usando AES-256 ECB con la clave proporcionada
     * @param data ByteArray a cifrar
     * @param key ByteArray que se usará como clave
     * @return ByteArray cifrado
     */
    private fun encryptBytes(data: ByteArray, key: ByteArray): ByteArray {
        return try {
//                println("🔒 Cifrando con AES-256 ECB")
//                println("🔑 Longitud clave: ${key.size} bytes (${key.size * 8} bits)")
//                println("📦 Datos entrada: ${data.size} bytes")

            val cipher = Cipher.getInstance("AES/ECB/NoPadding")
            val secretKey = SecretKeySpec(key, "AES")
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)

            val encryptedBytes = cipher.doFinal(data)
//                println("📦 Datos cifrados: ${encryptedBytes.size} bytes")
//                println("🔒 Resultado hex: ${encryptedBytes.joinToString("") { "%02x".format(it) }}")

            encryptedBytes

        } catch (e: Exception) {
            throw Exception("Error en cifrado AES-256: ${e.message}")
        }
    }

    /**
     * Convierte un string hexadecimal a ByteArray
     * @param hexString String en formato hexadecimal
     * @return ByteArray correspondiente
     */
    private fun hexStringToByteArray(hexString: String): ByteArray {
        return try {
            val cleanHex = hexString.replace(" ", "").lowercase()
            ByteArray(cleanHex.length / 2) { i ->
                val index = i * 2
                cleanHex.substring(index, index + 2).toInt(16).toByte()
            }
        } catch (e: Exception) {
            throw IllegalArgumentException("String hexadecimal inválido: $hexString")
        }
    }
}