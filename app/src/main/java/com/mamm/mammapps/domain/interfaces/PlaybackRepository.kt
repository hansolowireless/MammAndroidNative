package com.mamm.mammapps.domain.interfaces

import com.mamm.mammapps.data.model.player.GetTickersResponse
import com.mamm.mammapps.data.model.player.QosData
import com.mamm.mammapps.ui.model.player.ContentToPlayUI

interface PlaybackRepository {
    suspend fun getVideoUrlFromCLM(
        deliveryURL: String,
        typeOfContentString: String,
        chromecast: Boolean = false
    ) : Result<String>

    suspend fun getDRMUrl(
        content: ContentToPlayUI
    ) : Result<Pair<String, String>>

    suspend fun getTickers () : Result<GetTickersResponse>

    suspend fun sendHeartBeat () : Result<Unit>

    suspend fun sendQosData (qosData: QosData) : Result<Unit>

    suspend fun setBookmark (content: ContentToPlayUI, time: Long)

}