package com.mamm.mammapps.domain.usecases.player

import com.mamm.mammapps.domain.interfaces.TokenRepository
import com.mamm.mammapps.ui.model.player.ContentToPlayUI
import javax.inject.Inject

class GetJwTokenUseCase @Inject constructor(
    private val tokenRepository: TokenRepository
) {
    operator fun invoke(
        content: ContentToPlayUI?,
        chromecast: Boolean = false
    ): Result<String> {
        require(content != null) {"GetJwTokenUseCase requires content to be not null"}
        return runCatching {
            tokenRepository.generateJwtToken(
                contentID = content.identifier.getIdValue().toString(),
                eventType = content.getDRMString(),
                chromecast = chromecast
            )
        }
    }
}