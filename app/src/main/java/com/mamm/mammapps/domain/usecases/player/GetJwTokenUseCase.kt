package com.mamm.mammapps.domain.usecases.player

import com.mamm.mammapps.domain.interfaces.TokenRepository
import com.mamm.mammapps.ui.model.player.ContentToPlayUI
import javax.inject.Inject

class GetJwTokenUseCase @Inject constructor(
    private val tokenRepository: TokenRepository
) {
    suspend operator fun invoke(
        content: ContentToPlayUI?,
        chromecast: Boolean = false
    ): Result<String> {
        require(content != null) {"GetJwTokenUseCase requires content to be not null"}
        val streamID = content.epgEventInfo?.fatherChannelId ?: content.identifier.getIdValue()
        return tokenRepository.generateJwtToken(
            contentID = streamID.toString(),
            eventType = content.getDRMString(),
            chromecast = chromecast
        )
    }
}