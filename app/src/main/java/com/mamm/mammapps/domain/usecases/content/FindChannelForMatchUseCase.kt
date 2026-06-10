package com.mamm.mammapps.domain.usecases.content

import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.domain.interfaces.EPGRepository
import com.mamm.mammapps.domain.model.entity.Channel
import java.time.LocalDate
import javax.inject.Inject

class FindChannelForMatchUseCase @Inject constructor(
    private val epgRepository: EPGRepository,
    private val logger: Logger
) {
    companion object {
        private const val TAG = "FindChannelForMatchUseCase"
        private val DELIMITERS = listOf(" vs ", " - ")
    }

    suspend operator fun invoke(eventTitle: String, channelContext: String? = null): Result<Channel> {
        return epgRepository.getEPG(LocalDate.now()).mapCatching { epgContents ->
            
            // Extract the currently live event for each channel, paired with its Channel object
            val liveChannels = epgContents.mapNotNull { content ->
                val liveEvent = content.events.find { it.isLive() }
                if (liveEvent != null) Pair(content.channel, liveEvent) else null
            }
            
            // 1. Direct exact match (e.g popup channels fully named after the match, or EPG event exact match)
            val exactMatch = liveChannels.find { it.second.title.equals(eventTitle, ignoreCase = true) }
            if (exactMatch != null) {
                logger.debug(TAG, "Found exact live event match for: $eventTitle")
                return@mapCatching exactMatch.first
            }

            // 2. Try to split logic by known delimiters ("vs", etc)
            for (delimiter in DELIMITERS) {
                if (eventTitle.contains(delimiter, ignoreCase = true)) {
                    val teams = eventTitle.split(Regex(delimiter, RegexOption.IGNORE_CASE)).map { it.trim() }
                    if (teams.size >= 2) {
                        val teamA = teams[0]
                        val teamB = teams[1]

                        // Find any live event that contains AT LEAST ONE team name in its title
                        val matchByTeams = liveChannels.find { pair ->
                            val liveEventTitle = pair.second.title
                            liveEventTitle.contains(teamA, ignoreCase = true) || 
                            liveEventTitle.contains(teamB, ignoreCase = true)
                        }
                        
                        if (matchByTeams != null) {
                            logger.debug(TAG, "Found live event match by teams ($teamA, $teamB) for event: $eventTitle")
                            return@mapCatching matchByTeams.first
                        }
                    }
                }
            }

            // 3. Last fallback: try matching by the general competition context if supplied against the channel name directly
            if (!channelContext.isNullOrBlank()) {
                val formattedContext = channelContext.replace(".es", "").replace("_", " ")
                val matchByContext = epgContents.map { it.channel }.find { it.name.equals(formattedContext, ignoreCase = true) }
                if (matchByContext != null) {
                    logger.debug(TAG, "Found channel match by context '$formattedContext' for event: $eventTitle")
                    return@mapCatching matchByContext
                }
            }

            throw Exception("No playable channel found broadcast for event: $eventTitle")
        }.onFailure { e ->
            logger.error(TAG, "Error finding channel for event '$eventTitle': ${e.message}")
        }
    }
}
