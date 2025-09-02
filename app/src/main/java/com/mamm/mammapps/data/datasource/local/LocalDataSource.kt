package com.mamm.mammapps.data.datasource.local

interface LocalDataSource {
    suspend fun saveUserCredentials(username: String, password: String)
    suspend fun getUserCredentials(): Pair<String?, String?>
}