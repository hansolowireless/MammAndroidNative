package com.mamm.mammapps.domain.interfaces

import com.mamm.mammapps.ui.model.player.ContentToPlayUI

interface PlaybackRepository {
    suspend fun getVideoUrlFromCLM(deliveryURL: String, typeOfContentString: String) : Result<String>
    suspend fun getDRMUrl (content: ContentToPlayUI) : Result<Pair<String, String>>
}