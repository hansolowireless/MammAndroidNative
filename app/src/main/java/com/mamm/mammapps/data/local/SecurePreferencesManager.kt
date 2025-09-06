package com.mamm.mammapps.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit


@Singleton
class SecurePreferencesManager @Inject constructor(
    private val context: Context
) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val encryptedPrefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "preferences",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    companion object {
        private const val KEY_USERNAME = "uE"
        private const val KEY_PASSWORD = "uP"
    }

    // Guardar credenciales
    fun saveCredentials(username: String, password: String) {
        encryptedPrefs.edit {
            putString(KEY_USERNAME, username)
                .putString(KEY_PASSWORD, password)
        }
    }

    // Obtener credenciales
    private fun getUsername(): String? = encryptedPrefs.getString(KEY_USERNAME, null)
    private fun getPassword(): String? = encryptedPrefs.getString(KEY_PASSWORD, null)

    // Verificar si existen credenciales
    fun hasCredentials(): Boolean =
        !getUsername().isNullOrEmpty() && !getPassword().isNullOrEmpty()

    // Limpiar credenciales
    fun clearCredentials() {
        encryptedPrefs.edit().clear().apply()
    }

    // Obtener ambas como par
    fun getCredentials(): Pair<String?, String?> =
        Pair(getUsername(), getPassword())
}