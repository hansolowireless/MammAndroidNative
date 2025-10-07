package com.mamm.mammapps.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.mamm.mammapps.data.logger.Logger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecurePreferencesManager @Inject constructor(
    private val context: Context,
    private val logger: Logger
) {
    // Usar las MISMAS SharedPreferences que Flutter - nombre "preferences"
    private val sharedPrefs: SharedPreferences by lazy {
        context.getSharedPreferences("preferences", Context.MODE_PRIVATE)
    }

    companion object {
        private const val TAG = "SecurePreferencesManager"
        private const val KEY_USERNAME = "uE"
        private const val KEY_PASSWORD = "uP"
    }

    // Guardar credenciales (MISMO formato que Flutter)
    fun saveCredentials(username: String, password: String) {
        try {
            sharedPrefs.edit {
                putString(KEY_USERNAME, username)
                putString(KEY_PASSWORD, password)
            }
            logger.info(TAG, "Credentials saved successfully")
        } catch (e: Exception) {
            logger.error(TAG, "Failed to save credentials: ${e.message}")
        }
    }

    // Obtener credenciales
    fun getUsername(): String? {
        return try {
            sharedPrefs.getString(KEY_USERNAME, null)
        } catch (e: Exception) {
            logger.error(TAG, "Failed to get username: ${e.message}")
            null
        }
    }

    private fun getPassword(): String? {
        return try {
            sharedPrefs.getString(KEY_PASSWORD, null)
        } catch (e: Exception) {
            logger.error(TAG, "Failed to get password: ${e.message}")
            null
        }
    }

    // Verificar si existen credenciales
    fun hasCredentials(): Boolean {
        return try {
            val username = getUsername()
            val password = getPassword()
            val hasData = !username.isNullOrEmpty() && !password.isNullOrEmpty()
            logger.info(TAG, "Has credentials: $hasData")
            hasData
        } catch (e: Exception) {
            logger.error(TAG, "Failed to check credentials: ${e.message}")
            false
        }
    }

    // Limpiar credenciales
    fun clearCredentials() {
        try {
            sharedPrefs.edit { clear() }
            logger.info(TAG, "Credentials cleared successfully")
        } catch (e: Exception) {
            logger.error(TAG, "Failed to clear credentials: ${e.message}")
        }
    }

    // Obtener ambas como par
    fun getCredentials(): Pair<String?, String?> {
        return try {
            val credentials = Pair(getUsername(), getPassword())
            logger.info(TAG, "Retrieved credentials: username=${credentials.first?.isNotEmpty()}, password=${credentials.second?.isNotEmpty()}")
            credentials
        } catch (e: Exception) {
            logger.error(TAG, "Failed to get credentials: ${e.message}")
            Pair(null, null)
        }
    }

    // Método para debug: ver todas las claves almacenadas
    fun debugAllStoredData() {
        try {
            val allEntries = sharedPrefs.all
            logger.info(TAG, "=== SharedPreferences Debug ===")
            logger.info(TAG, "Total entries: ${allEntries.size}")
            allEntries.forEach { (key, value) ->
                logger.info(TAG, "Key: '$key' -> Value: '$value' (${value?.javaClass?.simpleName})")
            }
            logger.info(TAG, "=== End Debug ===")
        } catch (e: Exception) {
            logger.error(TAG, "Failed to debug stored data: ${e.message}")
        }
    }
}