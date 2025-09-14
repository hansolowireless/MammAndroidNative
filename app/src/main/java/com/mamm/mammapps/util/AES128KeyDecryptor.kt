package com.example.openstream_flutter_rw.data.security

import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

object AES128KeyDecryptor {
    private val K3keybytes = byteArrayOf(
        0x6B.toByte(), 0xEB.toByte(), 0x50.toByte(), 0x02.toByte(),
        0x72.toByte(), 0x75.toByte(), 0x8F.toByte(), 0x24.toByte(),
        0xBD.toByte(), 0xB3.toByte(), 0xC2.toByte(), 0x59.toByte(),
        0x80.toByte(), 0x70.toByte(), 0x79.toByte(), 0x46.toByte()
    )

    private val aes128KeyBytes: ByteArray
        get() = K3keybytes.map { (it.toInt() xor 0x2A).toByte() }.toByteArray()

    fun decryptServerKey(encryptedKey: String): String {
        return try {
            val keyBytes = aes128KeyBytes
            val encryptedBytes = hexStringToByteArray(encryptedKey)

//                println("🔧 Descifrando K2 con K3 (AES128 ECB)")
//                println("🔑 K3: ${keyBytes.joinToString("") { "%02x".format(it) }}")
//                println("📦 Token: ${encryptedBytes.size} bytes")

            val result = decryptECB(keyBytes, encryptedBytes)
            result

        } catch (e: Exception) {
            throw Exception("Error descifrando K2: ${e.message}")
        }
    }

    private fun decryptECB(key: ByteArray, data: ByteArray): String {
        /*            println("🔧 Descifrado manual ECB:")
                    println("   Procesando ${data.size} bytes en bloques de 16")*/

        val cipher = Cipher.getInstance("AES/ECB/NoPadding")
        val secretKey = SecretKeySpec(key, "AES")
        cipher.init(Cipher.DECRYPT_MODE, secretKey)

        val allDecrypted = mutableListOf<Byte>()

        // Procesa cada bloque de 16 bytes
        for (i in data.indices step 16) {
            val blockEnd = minOf(i + 16, data.size)
            val block = data.sliceArray(i until blockEnd)

//                println("   Bloque ${i / 16}: ${block.joinToString("") { "%02x".format(it) }}")

            val decryptedBlock = cipher.doFinal(block)
            allDecrypted.addAll(decryptedBlock.toList())
//                println("decryptECB ✅ Descifrado: ${decryptedBlock.joinToString("") { "%02x".format(it) }}")
        }

        return extractK2(allDecrypted.toByteArray())
    }

    private fun extractK2(data: ByteArray): String {
//            println("🔍 ANÁLISIS COMPLETO:")
//            println("   Total descifrado: ${data.size} bytes")
//            println("   Como hex completo: ${data.joinToString("") { "%02x".format(it) }}")

        // Convierte todo a string y limpia
        val asString = String(data).replace(Regex("[\u0000-\u001F\u007F]+"), "")
//            println("   Como string limpio: \"$asString\" (${asString.length} chars)")

        // Si tiene exactamente 64 caracteres alfanuméricos
        if (asString.length == 64) {
            println("✅ K2 de 64 chars encontrada: $asString")
            return asString
        }

        // Si el hex completo tiene múltiplos que sumen 64
        val hexComplete = data.joinToString("") { "%02x".format(it) }
        if (hexComplete.length >= 64) {
            val k2Hex = hexComplete.substring(0, 64)
//                println("✅ K2 como hex (64 chars): $k2Hex")
            return k2Hex
        }

        // Debug: muestra bloques de 16 bytes
        for (i in data.indices step 16) {
            val blockEnd = minOf(i + 16, data.size)
            val block = data.sliceArray(i until blockEnd)
            val blockHex = block.joinToString("") { "%02x".format(it) }
            val blockStr = String(block).replace(Regex("[\u0000-\u001F\u007F]"), "·")
//                println("   Bloque ${i / 16}: $blockHex = \"$blockStr\"")
        }

        return asString.ifEmpty { hexComplete }
    }

    private fun hexStringToByteArray(hex: String): ByteArray {
        val cleanHex = hex.replace(" ", "").uppercase()
        require(cleanHex.length % 2 == 0) { "La cadena hex debe tener longitud par" }

        return cleanHex.chunked(2)
            .map { it.toInt(16).toByte() }
            .toByteArray()
    }
}