package com.mamm.mammapps.domain.usecases.diagnostic

import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.domain.model.AboutInfo
import com.mamm.mammapps.domain.interfaces.DiagnosticsRepository
import javax.inject.Inject

class GetAboutInfoUseCase @Inject constructor(
    private val diagRepository: DiagnosticsRepository,
    private val logger: Logger
){
    companion object {
        private const val TAG = "GetAboutInfoUseCase"
    }
    suspend operator fun invoke(): Result<AboutInfo> {
        return diagRepository.getAboutInfo()
    }
}