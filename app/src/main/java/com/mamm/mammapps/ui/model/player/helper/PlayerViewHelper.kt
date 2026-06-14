package com.mamm.mammapps.ui.model.player.helper

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.mamm.mammapps.R
import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.data.logger.SimpleLogger
import com.mamm.mammapps.ui.component.player.custompreviewbar.CustomPreviewBar
import com.mamm.mammapps.ui.component.player.dialogs.TrackSelectionDialog
import com.mamm.mammapps.ui.constant.PlayerConstant.MILLISECONDS_TIMEBAR_KEYTIME_INCREMENT
import com.mamm.mammapps.ui.extension.setHourText
import com.mamm.mammapps.ui.extension.toDate
import com.mamm.mammapps.ui.model.player.ContentToPlayUI
import com.mamm.mammapps.ui.model.player.LiveEventInfoUI

private const val TAG = "PlayerViewHelper"
private val logger: Logger = SimpleLogger()

/**
 * Configura la visibilidad y el estado de los controles de reproducción nativos (XML/legacy)
 * incrustados dentro del reproductor basado en Compose.
 *
 * Esta función desacopla al ViewModel de la manipulación directa de vistas de Android (View, TextView, etc.),
 * abstrayendo la lógica visual en base al estado del contenido actual y del evento en vivo.
 *
 * @param playerView La vista del reproductor [StyledPlayerView] cuyos controles se van a configurar.
 * @param content El modelo UI del contenido actual en reproducción [com.mamm.mammapps.ui.model.player.ContentToPlayUI].
 * @param liveEventInfo La información del evento en vivo actual [com.mamm.mammapps.ui.model.player.LiveEventInfoUI], o nulo si no aplica.
 */
fun setControlVisibility(
    playerView: StyledPlayerView,
    content: ContentToPlayUI,
    liveEventInfo: LiveEventInfoUI?
) {
    val positionView: View = playerView.findViewById(R.id.exo_position)
    val tstvHourBeginView: TextView = playerView.findViewById(R.id.tstv_hourbegin)
    val tstvHourEndView: TextView = playerView.findViewById(R.id.tstv_hourend)
    val exoDuration: TextView = playerView.findViewById(com.google.android.exoplayer2.ui.R.id.exo_duration)
    val liveLabel: View = playerView.findViewById(R.id.live_indicator)

    val goToLiveButton: AppCompatImageButton = playerView.findViewById(R.id.go_live_button)
    val startOverButton: View = playerView.findViewById(R.id.go_beginning_button)

    val jump10sback =
        playerView.findViewById<AppCompatImageButton>(R.id.jump_10s_back)
    val jump10sforward =
        playerView.findViewById<AppCompatImageButton>(R.id.jump_10s_forward)

    playerView.setShowNextButton(false)
    playerView.setShowPreviousButton(false)

    val previewBar = playerView.findViewById<CustomPreviewBar>(R.id.exo_progress)

    configureTimeBar(previewBar, content, liveEventInfo)

    if (content.isTimeshift) {
        jump10sback.visibility = View.GONE
        jump10sforward.visibility = View.GONE
        playerView.setShowRewindButton(false)
        playerView.setShowFastForwardButton(false)

        if (liveEventInfo != null) {
            tstvHourBeginView.visibility = View.VISIBLE
            tstvHourEndView.visibility = View.VISIBLE
            exoDuration.visibility = View.INVISIBLE
            tstvHourBeginView.setHourText(liveEventInfo.eventStart)
            tstvHourEndView.setHourText(liveEventInfo.eventEnd)

            positionView.visibility = View.INVISIBLE
            startOverButton.visibility = View.VISIBLE

            if (previewBar?.isTstvMode == true) {
                liveLabel.visibility = View.GONE
                goToLiveButton.visibility = View.VISIBLE
            } else {
                liveLabel.visibility = View.VISIBLE
                goToLiveButton.visibility = View.GONE
            }

        } else {
            positionView.visibility = View.GONE
            tstvHourBeginView.visibility = View.GONE
            tstvHourEndView.visibility = View.GONE
            liveLabel.visibility = View.VISIBLE
            startOverButton.visibility = View.GONE
        }
    } else {
        goToLiveButton.visibility = View.GONE
        startOverButton.visibility = View.GONE

        if (!content.isLive) {
            positionView.visibility = View.VISIBLE
            tstvHourBeginView.visibility = View.GONE
            tstvHourEndView.visibility = View.GONE
            exoDuration.visibility = View.VISIBLE
            liveLabel.visibility = View.GONE
            jump10sback.visibility = View.VISIBLE
            jump10sforward.visibility = View.VISIBLE
            playerView.setShowRewindButton(true)
            playerView.setShowFastForwardButton(true)
        } else {
            positionView.visibility = View.GONE
            tstvHourBeginView.visibility = View.GONE
            tstvHourEndView.visibility = View.GONE
            exoDuration.visibility = View.INVISIBLE
            liveLabel.visibility = View.VISIBLE
            jump10sback.visibility = View.GONE
            jump10sforward.visibility = View.GONE
            playerView.setShowRewindButton(false)
            playerView.setShowFastForwardButton(false)
        }
    }

    val titleLabel: TextView = playerView.findViewById(R.id.channel_or_title_label)
    val liveEventTitleLabel: TextView = playerView.findViewById(R.id.live_event_title_Label)
    val contentImageView: ImageView = playerView.findViewById(R.id.contentImageView)

    titleLabel.text = content.title
    liveEventTitleLabel.text = liveEventInfo?.title
    Glide.with(playerView)
        .load(content.imageUrl)
        .into(contentImageView)
}

/**
 * Configura el comportamiento, visibilidad y límites de tiempo de la barra de progreso (TimeBar/PreviewBar).
 *
 * @param previewBar El componente de barra de progreso [CustomPreviewBar].
 * @param content El modelo UI del contenido actual en reproducción [ContentToPlayUI].
 * @param liveEventInfo La información del evento en vivo actual [LiveEventInfoUI], o nulo si no aplica.
 */
fun configureTimeBar(
    previewBar: CustomPreviewBar?,
    content: ContentToPlayUI,
    liveEventInfo: LiveEventInfoUI?
) {
    previewBar?.setKeyTimeIncrement(MILLISECONDS_TIMEBAR_KEYTIME_INCREMENT)
    if (content.isLive) {
        if (liveEventInfo != null && content.isTimeshift) {
            previewBar?.setEventHourEnd(liveEventInfo.eventEnd?.toDate())
            previewBar?.setEventHourBegin(liveEventInfo.eventStart?.toDate())
            previewBar?.setIsTimeshift(content.isTimeshift)
            previewBar?.visibility = View.VISIBLE
            logger.debug(TAG, "configureTimeBar - previewBar visibility: VISIBLE")
        } else {
            previewBar?.setIsTimeshift(false)
            previewBar?.visibility = View.GONE
            logger.debug(TAG, "configureTimeBar - previewBar visibility: GONE")
        }
    } else {
        previewBar?.visibility = View.VISIBLE
        logger.debug(TAG, "configureTimeBar - previewBar visibility: VISIBLE")
    }
}

/**
 * Evalúa y configura la visibilidad de los botones de selección de pistas de Audio y Subtítulos (CC).
 *
 * Muestra u oculta los botones correspondientes dependiendo de si el reproductor actual tiene
 * pistas de audio o subtítulos disponibles en el flujo de medios seleccionado.
 *
 * @param player La instancia activa de [Player] de ExoPlayer.
 * @param ccTracksButton El botón visual de subtítulos [AppCompatImageButton].
 * @param audioTracksButton El botón visual de pistas de audio [AppCompatImageButton].
 */
fun setDialogButtonVisibility(
    player: Player?,
    ccTracksButton: AppCompatImageButton?,
    audioTracksButton: AppCompatImageButton?
) {
    runCatching {
        if (TrackSelectionDialog.willHaveCCContent(player)) {
            ccTracksButton?.visibility = View.VISIBLE
        } else {
            ccTracksButton?.visibility = View.GONE
        }

        if (TrackSelectionDialog.willHaveAudioContent(player)) {
            audioTracksButton?.visibility = View.VISIBLE
        } else {
            audioTracksButton?.visibility = View.GONE
        }
    }.onFailure {
        logger.error(TAG, "setDialogButtonVisibility - Error setting button visibility: ${it.message}")
    }
}
