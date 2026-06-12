package com.mamm.mammapps.ui.viewmodel

import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.domain.usecases.FindLiveEventOnChannelUseCase
import com.mamm.mammapps.domain.usecases.content.GetChannelsUseCase
import com.mamm.mammapps.ui.mapper.toContentEntityUI
import com.mamm.mammapps.ui.mapper.toContentListUI
import com.mamm.mammapps.ui.mapper.toContentToPlayUI
import com.mamm.mammapps.ui.model.ContentEntityUI
import com.mamm.mammapps.ui.model.ContentIdentifier
import com.mamm.mammapps.ui.model.ContentListUI
import com.mamm.mammapps.ui.model.player.ContentToPlayUI
import com.mamm.mammapps.ui.model.player.ZappingInfoUI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class ZappingController @Inject constructor(
    private val getChannelsUseCase: GetChannelsUseCase,
    private val getLiveEventInfoUseCase: FindLiveEventOnChannelUseCase,
    private val logger: Logger
) {
    companion object {
        private const val TAG = "ZappingController"
    }

    private val _showZappingLayer = MutableStateFlow<Boolean>(false)
    val showZappingLayer = _showZappingLayer.asStateFlow()

    private val _zappingInfo = MutableStateFlow<List<ZappingInfoUI>>(emptyList())
    val zappingInfo = _zappingInfo.asStateFlow()

    private val _zappingNumberDisplay = MutableStateFlow<String>("")
    val zappingNumberDisplay = _zappingNumberDisplay.asStateFlow()

    /**
     * Muestra la capa de zapping si el contenido actual en reproducción es un Canal de TV.
     */
    fun showZappingLayer(currentContent: ContentToPlayUI) {
        if (currentContent.identifier is ContentIdentifier.Channel) {
            _showZappingLayer.update { true }
        }
    }

    /**
     * Oculta la capa de zapping.
     */
    fun hideZappingLayer() {
        _showZappingLayer.update { false }
    }

    /**
     * Agrega un nuevo dígito al display visual del canal seleccionado por el usuario en el mando.
     */
    fun showZappingNumberDisplay(newDigit: String) {
        logger.debug(TAG, "showZappingNumberDisplay")
        if (_zappingNumberDisplay.value.length < 3) {
            _zappingNumberDisplay.update { _zappingNumberDisplay.value + newDigit }
        }
    }

    /**
     * Actualiza la lista de canales para el zapping filtrando según si es porno o no.
     */
    fun updateChannelList(currentContent: ContentToPlayUI, scope: CoroutineScope) {
        logger.debug(TAG, "updateChannelList")
        if (currentContent.identifier is ContentIdentifier.Channel) {
            scope.launch(Dispatchers.IO) {
                getChannelsUseCase().onSuccess { channels ->
                    val currentIsPorn = channels.find { it.id == currentContent.identifier.id }?.isPornChannel ?: false

                    _zappingInfo.update {
                        channels.filter { it.isPornChannel == currentIsPorn }.map { channel ->
                            ZappingInfoUI(
                                channel = channel.toContentEntityUI(),
                                liveEvent = getLiveEventInfoUseCase(channelId = channel.id)?.toContentListUI()
                                    ?: ContentListUI(
                                        identifier = ContentIdentifier.Event(0),
                                        title = "",
                                        imageUrl = ""
                                    )
                            )
                        }
                    }
                }.onFailure {
                    logger.error(TAG, "updateChannelList Error getting channels for Zapping List")
                }
            }
        }
    }

    /**
     * Busca y reproduce el canal seleccionado.
     */
    fun findAndPlayChannel(content: ContentEntityUI, scope: CoroutineScope, onPlayChannel: (ContentToPlayUI) -> Unit) {
        scope.launch(Dispatchers.IO) {
            getChannelsUseCase().onSuccess { channels ->
                val channel = channels.find { it.id == content.identifier.id }
                channel?.let {
                    onPlayChannel(it.toContentToPlayUI())
                } ?: logger.error(TAG, "findAndPlayChannel - Channel not found")
            }
        }
    }

    /**
     * Navega al canal indicado mediante el número introducido en el mando.
     */
    fun navigateToChannel(number: String, scope: CoroutineScope, onPlayChannel: (ContentToPlayUI) -> Unit) {
        runCatching {
            _zappingNumberDisplay.update { "" }
            val index = number.toInt() - 1
            if (index in _zappingInfo.value.indices) {
                findAndPlayChannel(content = _zappingInfo.value[index].channel, scope = scope, onPlayChannel = onPlayChannel)
            } else {
                logger.error(TAG, "navigateToChannel - Index $index out of bounds for zapping list")
            }
        }.onFailure {
            logger.error(TAG, "navigateToChannel - Error navigating to channel: ${it.message}")
        }
    }

    /**
     * Navega al siguiente canal de la lista.
     */
    fun navigateToNextChannel(currentContent: ContentToPlayUI, scope: CoroutineScope, onPlayChannel: (ContentToPlayUI) -> Unit) {
        if (currentContent.identifier is ContentIdentifier.Channel) {
            runCatching {
                val currentList = _zappingInfo.value
                if (currentList.isNotEmpty()) {
                    val currentIndex = currentList.indexOfFirst { it.channel.identifier.id == currentContent.identifier.id }
                    if (currentIndex != -1) {
                        val nextIndex = (currentIndex + 1) % currentList.size
                        val nextChannel = currentList[nextIndex].channel
                        findAndPlayChannel(content = nextChannel, scope = scope, onPlayChannel = onPlayChannel)
                    }
                }
            }.onFailure {
                logger.error(TAG, "navigateToNextChannel - Error navigating to next channel: ${it.message}")
            }
        } else {
            logger.error(TAG, "navigateToNextChannel - Content is not a channel")
        }
    }

    /**
     * Navega al canal anterior de la lista.
     */
    fun navigateToPreviousChannel(currentContent: ContentToPlayUI, scope: CoroutineScope, onPlayChannel: (ContentToPlayUI) -> Unit) {
        if (currentContent.identifier is ContentIdentifier.Channel) {
            runCatching {
                val currentList = _zappingInfo.value
                if (currentList.isNotEmpty()) {
                    val currentIndex = currentList.indexOfFirst { it.channel.identifier.id == currentContent.identifier.id }
                    if (currentIndex != -1) {
                        val prevIndex = if (currentIndex <= 0) currentList.size - 1 else currentIndex - 1
                        val prevChannel = currentList[prevIndex].channel
                        findAndPlayChannel(content = prevChannel, scope = scope, onPlayChannel = onPlayChannel)
                    }
                }
            }.onFailure {
                logger.error(TAG, "navigateToPreviousChannel - Error navigating to previous channel: ${it.message}")
            }
        } else {
            logger.error(TAG, "navigateToPreviousChannel - Content is not a channel")
        }
    }
}
