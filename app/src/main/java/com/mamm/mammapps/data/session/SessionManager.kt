package com.mamm.mammapps.data.session

import com.mamm.mammapps.data.model.login.LoginData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    private val sessionStorage: SessionStorage
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

    // Bloque de inicialización para restaurar la sesión
    init {
        val restoredData = sessionStorage.getLoginData()
        if (restoredData != null) {
            processLoginData(restoredData)
        }
    }

    fun startNewSession(data: LoginData) {
        sessionStorage.saveLoginData(data)
        processLoginData(data)
    }

    private fun processLoginData(data: LoginData) {
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

    fun isHoreca(): Boolean {
        val horecaIds = listOf(226, 225, 206, 224)
        return availablePackages.any { it in horecaIds }
    }

    fun clear() {
        sessionStorage.clear() // ¡Importante! Limpiar también el almacenamiento persistente
        loginData = null
        availablePackages = emptyList()
        skinImages.clear()
        channelOrder.clear()
        jsonFile = null
    }

}
