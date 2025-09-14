package com.mamm.mammapps.domain.usecases.player

import com.mamm.mammapps.domain.interfaces.PlaybackRepository
import com.mamm.mammapps.domain.interfaces.TokenRepository
import javax.inject.Inject

class GetPlayableUrlUseCase @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val playbackRepository: PlaybackRepository
) {

    suspend operator fun invoke(deliveryURL: String, typeOfContentString: String): Result<String> =
        runCatching {
            val playableURL = playbackRepository.getVideoUrlFromCLM(deliveryURL = deliveryURL, typeOfContentString = typeOfContentString).getOrThrow().let { url ->
                tokenRepository.refreshIp().onFailure { exception ->
                    throw exception
                }
                tokenRepository.storeK1KeyEncrypted(url).onFailure { exception ->
                    throw exception
                }
                url // Return playableURL after successful operations
            }
            playableURL
        }.onSuccess {
            Result.success(it) // On success, return the result
        }.onFailure {
            Result.success(it) // On failure still return the result
        }
}