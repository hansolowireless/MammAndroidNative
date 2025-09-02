package com.mamm.mammapps.data.repository

import com.mamm.mammapps.data.model.LoginData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionRepository @Inject constructor() {

    // Datos que vienen directamente de la API
    var loginData: LoginData? = null
        private set

    // Datos derivados o procesados
    var availablePackages: List<Int> = emptyList()
        private set
    var skinImages: MutableMap<Int, String> = mutableMapOf()
    var channelOrder: MutableMap<Int, Int> = mutableMapOf()
    var jsonFile: String? = null

    fun assignLoginData(data: LoginData) {
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
        availablePackages = packagesStrings?.map { it.toIntOrNull() ?: 0 } ?: emptyList()

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


    fun clear() {
        loginData = null
        availablePackages = emptyList()
        skinImages.clear()
        channelOrder.clear()
    }

    // Propiedades de conveniencia
    val token: String? get() = loginData?.token
    val userId: String? get() = loginData?.userId?.toString()
    val jwToken: String? get() = loginData?.jwtoken
    val pinParental: String? get() = loginData?.pinparental
}