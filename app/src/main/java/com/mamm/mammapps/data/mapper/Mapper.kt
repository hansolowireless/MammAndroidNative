package com.mamm.mammapps.data.mapper

import com.mamm.mammapps.data.model.diagnostic.DiagResponseDto
import com.mamm.mammapps.data.model.login.LoginDataDto
import com.mamm.mammapps.data.model.login.SkinDto
import com.mamm.mammapps.data.model.login.SkinLogoDto
import com.mamm.mammapps.data.model.player.GetTickersResponseDto
import com.mamm.mammapps.data.model.player.TickerDto
import com.mamm.mammapps.data.model.sportsevent.SportsEventDto
import com.mamm.mammapps.data.util.formatNodeUrl
import com.mamm.mammapps.domain.model.about.DiagnosticInfo
import com.mamm.mammapps.domain.model.SportsEvent
import com.mamm.mammapps.domain.model.entity.Featured
import com.mamm.mammapps.domain.model.loginwithcode.LoginData
import com.mamm.mammapps.domain.model.loginwithcode.Skin
import com.mamm.mammapps.domain.model.loginwithcode.SkinLogo
import com.mamm.mammapps.domain.model.player.Ticker
import com.mamm.mammapps.util.parseSportEventDate

fun DiagResponseDto.toDomain(): DiagnosticInfo {
    return DiagnosticInfo(
        node1Url = formatNodeUrl(this.nodeHA.node01),
        node2Url = formatNodeUrl(this.nodeHA.node02),
        node3Url = formatNodeUrl(this.nodeDir.node01),
        node4Url = formatNodeUrl(this.nodeDir.node02)
    )
}

fun SportsEventDto.toDomain() : SportsEvent {
    return SportsEvent(
        title = this.title ?: "",
        description = this.desc ?: "",
        startTime = parseSportEventDate(this.start),
        endTime = parseSportEventDate(this.stop),
        channelId = this.channel ?: "",
        horizontalImage = this.icons?.find { it.contains("_B") }.orEmpty(),
        verticalImage = this.icons?.find { it.contains("_P") }.orEmpty()
    )
}

fun GetTickersResponseDto.toDomain() : List<Featured> {
    return this.operatorFeatured?.mapNotNull { featuredDto ->
        featuredDto.toDomain().takeIf { it.isValidByDate() }
    } ?: emptyList()
}

fun TickerDto.toDomain() : Ticker {
    return Ticker(
        campaignId = this.campaignId,
        tiempoDuracion = this.tiempoDuracion,
        htmlUrl = this.htmlUrl
    )
}

fun LoginDataDto.toDomain() : LoginData {

    fun SkinLogoDto.toDomain() : SkinLogo {
        return SkinLogo(
            type = this.type,
            url = this.url
        )
    }

    fun SkinDto.toDomain() : Skin {
        return Skin(
            operator = this.operator,
            logos = this.logos?.map { it.toDomain() }
        )
    }

    return LoginData(
        token = this.token,
        userId = this.userId,
        jsonFile = this.jsonFile,
        pinparental = this.pinparental,
        jwtoken = this.jwtoken,
        refreshToken = this.refreshToken,
        skin = this.skin?.toDomain(),
        channelOrder = this.channelOrder,
        loginUser = this.loginUser,
        operator = this.operator
    )
}



