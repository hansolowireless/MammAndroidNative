package com.mamm.mammapps.domain.interfaces

import com.mamm.mammapps.data.model.player.GetTickersResponseDto
import com.mamm.mammapps.data.model.player.QosData
import com.mamm.mammapps.domain.model.player.TickerInfo
import com.mamm.mammapps.ui.model.player.ContentToPlayUI
import kotlinx.coroutines.flow.Flow

interface PlaybackRepository {
    suspend fun getVideoUrlFromCLM(
        deliveryURL: String,
        typeOfContentString: String,
        chromecast: Boolean = false
    ) : Result<String>

    suspend fun getDRMUrl(
        content: ContentToPlayUI
    ) : Result<Pair<String, String>>

    suspend fun getTickers () : Result<TickerInfo>

    suspend fun getTickerQoSData(contentId: Int) : Result<QosData>

    suspend fun sendHeartBeat () : Result<Unit>

    suspend fun sendQosData (qosData: QosData) : Result<Unit>

    suspend fun setBookmark (content: ContentToPlayUI, time: Long)

    /**
     * Guarda el progreso actual de un contenido en la memoria volátil (Caché).
     * @param contentId Identificador único (ej: "VOD_123" o generado por ContentIdentifier)
     * @param progress Posición en milisegundos.
     */
    fun saveContentProgress(contentId: String, progress: Long)

    /**
     * Recupera el progreso guardado de un contenido.
     * @param contentId Identificador único del contenido.
     * @return El progreso en milisegundos o 0L si no existe o la caché fue limpiada.
     */
    fun getContentProgress(contentId: String): Long

    fun getContentProgressFlow(): Flow<Map<String, Long>>

    fun clearContentProgress()

}