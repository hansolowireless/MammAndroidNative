package com.mamm.mammapps.data.mapper

import com.mamm.mammapps.data.model.loginwithcode.LoginCodeGenerateResponseDto
import com.mamm.mammapps.data.model.loginwithcode.LoginCodeStatusResponseDto
import com.mamm.mammapps.domain.model.loginwithcode.LoginCodeGenerate
import com.mamm.mammapps.domain.model.loginwithcode.LoginCodeStatus

fun LoginCodeGenerateResponseDto.toDomain(): LoginCodeGenerate {
    return LoginCodeGenerate(
        code = this.code,
        expiresIn = this.expiresIn,
        pollingInterval = this.pollingInterval
    )
}

fun LoginCodeStatusResponseDto.toDomain(): LoginCodeStatus {
    return LoginCodeStatus(
        result = this.result,
        message = this.message,
        data = this.data?.toDomain()
    )
}


