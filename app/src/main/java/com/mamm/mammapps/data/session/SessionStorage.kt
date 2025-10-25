package com.mamm.mammapps.data.session

import android.content.Context
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.google.gson.Gson
import com.mamm.mammapps.data.model.login.LoginData
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionStorage @Inject constructor(
    @ApplicationContext context: Context,
    private val gson: Gson
) {
    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    private val sharedPreferences = EncryptedSharedPreferences.create(
        "session_prefs",
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    companion object {
        private const val KEY_LOGIN_DATA = "login_data_json"
    }

    fun saveLoginData(data: LoginData) {
        val jsonString = gson.toJson(data)
        sharedPreferences.edit { putString(KEY_LOGIN_DATA, jsonString) }
    }

    fun getLoginData(): LoginData? {
        val jsonString = sharedPreferences.getString(KEY_LOGIN_DATA, null)
        return if (jsonString != null) {
            gson.fromJson(jsonString, LoginData::class.java)
        } else {
            null
        }
    }

    fun clear() {
        sharedPreferences.edit { remove(KEY_LOGIN_DATA) }
    }
}
