package com.mamm.mammapps.data.datasource.session

import com.mamm.mammapps.data.local.SharedPreferencesManager
import com.mamm.mammapps.data.model.login.LoginData
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.log

@Singleton
class SessionDatasource @Inject constructor(
    private val sharedPreferencesManager: SharedPreferencesManager
) {
    // Datos que vienen directamente de la API
    var loginData: LoginData? = null
        private set

    // Datos derivados o procesados
    var availablePackages: List<Int> = emptyList()
        private set
    var skinImages: MutableMap<Int, String> = mutableMapOf()
    var channelOrder: MutableMap<Int, Int> = mutableMapOf()
    var jsonFile: String? = null

    // Propiedades de conveniencia (no cambian)
    val token: String? get() = loginData?.token
    val userId: String? get() = loginData?.userId?.toString()
    val jwToken: String? get() = loginData?.jwtoken
    val pinParental: String? get() = loginData?.pinparental
    val operatorLogoUrl : String? get() = skinImages[5001] ?: skinImages[1501]

    init {
        val restoredData = sharedPreferencesManager.getLoginData()
        if (restoredData != null) {
            processLoginData(restoredData)
        }
    }

    private fun startNewSession(data: LoginData) {
        sharedPreferencesManager.setLoginData(data)
        processLoginData(data)
    }

    private fun processLoginData(data: LoginData) {
        //Asignar el login data
        loginData = data

        // Procesar availablePackages desde jsonFile
        jsonFile = data.jsonFile ?: ""

        val packagesStrings = jsonFile?.split("/")?.last()?.split("_")?.toMutableList()
        packagesStrings?.remove("svod")
        packagesStrings?.let { list ->
            if (list.isNotEmpty()) {
                list[list.lastIndex] = list.last().replace(".json", "")
            }
        }
        availablePackages = packagesStrings?.mapNotNull { it.toIntOrNull() } ?: emptyList()

        // Procesar skinImages
        skinImages.clear()
        data.skin?.logos?.forEach { logo ->
            val type = logo.type?.toIntOrNull()
            val url = logo.url
            if (type != null && url != null) skinImages[type] = url
        }

        // Procesar channelOrder
        channelOrder.clear()
        val orderStr = data.channelOrder
        if (!orderStr.isNullOrEmpty() && orderStr != "[]") {
            val auxOrderVar = orderStr.replace("[", "").replace("]", "")
                .split(",").mapNotNull { it.toIntOrNull() }
            auxOrderVar.forEachIndexed { index, value -> channelOrder[value] = index + 1 }
        }
    }

    fun saveUserCredentials(
        username: String,
        password: String,
        loginData: LoginData
    ) {
        startNewSession(loginData)
        sharedPreferencesManager.saveCredentials(
            username = username,
            password = password,
            loginData = loginData
        )
    }

    fun getUserCredentials(): Pair<String?, String?> {
        return sharedPreferencesManager.getCredentials()
    }

    /*
    * Actualiza el token de sesión (refresh) y el access
    */
    fun updateToken(newRefresh: String, newAccess: String) {
        val currentData = loginData ?: return
        currentData.copy(
            refreshToken = newRefresh,
            jwtoken = newAccess
        ).let { updatedData ->
            startNewSession(updatedData)
        }
    }

    fun isHoreca(): Boolean {
        val horecaIds = listOf(226, 225, 206, 224)
        return availablePackages.any { it in horecaIds }
    }

    fun clear() {
        sharedPreferencesManager.clearCredentials()
        loginData = null
        availablePackages = emptyList()
        skinImages.clear()
        channelOrder.clear()
        jsonFile = null
    }

}
