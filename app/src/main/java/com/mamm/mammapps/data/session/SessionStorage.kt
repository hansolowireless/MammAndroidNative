package com.mamm.mammapps.data.session

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.mamm.mammapps.data.model.login.LoginData
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionStorage @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson
) {
    private val PREFS_NAME = "session_prefs"
    private val KEY_LOGIN_DATA = "login_data_json"

    // Usamos SharedPreferences normales (compatibles desde SDK 1)
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveLoginData(data: LoginData) {
        val jsonString = gson.toJson(data)
        sharedPreferences.edit {
            putString(KEY_LOGIN_DATA, jsonString)
        }
    }

    fun getLoginData(): LoginData? {
        val jsonString = sharedPreferences.getString(KEY_LOGIN_DATA, null)
        return jsonString?.let {
            try {
                gson.fromJson(it, LoginData::class.java)
            } catch (e: Exception) {
                null
            }
        }
    }

    fun clear() {
        sharedPreferences.edit {
            remove(KEY_LOGIN_DATA)
        }
    }
}
