package com.mamm.mammapps.domain.interfaces

import com.mamm.mammapps.domain.model.epg.EPGChannelContent
import com.mamm.mammapps.domain.model.entity.Event
import java.time.LocalDate

interface EPGRepository {
    suspend fun getEPG(date: LocalDate) : Result<List<EPGChannelContent>>

    /**
     * Programa en emisión ahora mismo en el canal. Si el fichero que lo contiene no está en
     * memoria (por ejemplo, porque ha cambiado el día), lo carga antes de responder.
     */
    suspend fun getLiveEventForChannel(channelId: Int) : Event?

    fun findContent(channelId: Int, eventId: Int, date: LocalDate) : Event
    fun clearCache()

    /**
     * Tras iniciar sesión: vacía la parrilla en memoria y, si el usuario ha cambiado,
     * también la de disco.
     */
    suspend fun onUserLoggedIn()
}
