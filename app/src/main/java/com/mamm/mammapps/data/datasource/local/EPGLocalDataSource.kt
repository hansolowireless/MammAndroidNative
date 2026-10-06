package com.mamm.mammapps.data.datasource.local

import com.mamm.mammapps.data.model.epg.EPGChannelContentDto
import java.time.LocalDate

/**
 * Parrillas guardadas en disco, un fichero por día.
 *
 * Va aparte de [LocalDataSource] porque toda su superficie es suspend: es la única
 * parte del almacenamiento local que no se resuelve en memoria.
 */
interface EPGLocalDataSource {

    suspend fun getEPG(date: LocalDate): List<EPGChannelContentDto>?

    suspend fun setEPG(date: LocalDate, channels: List<EPGChannelContentDto>)

    /** Borra las parrillas anteriores a [oldest], y los restos de escrituras cortadas. */
    suspend fun purgeOlderThan(oldest: LocalDate)

    /** Vacía el disco si quien inicia sesión no es el mismo que dejó los ficheros. */
    suspend fun clearIfUserChanged(userKey: String?)
}
