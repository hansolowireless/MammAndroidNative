package com.mamm.mammapps.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.data.model.login.LoginDataDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPreferencesManager @Inject constructor(
    private val context: Context,
    private val gson: Gson,
    private val logger: Logger
) {
    private val sharedPrefs: SharedPreferences by lazy {
        context.getSharedPreferences("preferences", Context.MODE_PRIVATE)
    }

    companion object {
        private const val TAG = "SharedPreferencesManager"
        private const val KEY_USERNAME = "uE"
        private const val KEY_PASSWORD = "uP"
        private const val KEY_REFRESH_TOKEN = "uRT"
        private val KEY_LOGIN_DATA = "login_data_json"
    }

    // Guardar credenciales (MISMO formato que Flutter)
    fun saveCredentials(
        username: String,
        password: String?,
        loginData: LoginDataDto
    ) {
        try {
            sharedPrefs.edit {
                putString(KEY_USERNAME, username)
                putString(KEY_PASSWORD, password)
                setLoginData(loginData)
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

    fun setLoginData(data: LoginDataDto) {
        val jsonString = gson.toJson(data)
        sharedPrefs.edit {
            putString(KEY_LOGIN_DATA, jsonString)
        }
    }

    fun getLoginData(): LoginDataDto? {
        val jsonString = sharedPrefs.getString(KEY_LOGIN_DATA, null)
        return jsonString?.let {
            try {
                gson.fromJson(it, LoginDataDto::class.java)
            } catch (e: Exception) {
                null
            }
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

}