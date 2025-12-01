package com.mamm.mammapps.domain.usecases

import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.data.model.AboutInfo
import com.mamm.mammapps.domain.interfaces.MammRepository
import javax.inject.Inject

class GetAboutInfoUseCase @Inject constructor(
    private val mammRepository: MammRepository,
    private val logger: Logger
){
    companion object {
        private const val TAG = "GetAboutInfoUseCase"
    }
    suspend operator fun invoke(): Result<AboutInfo> {
        return mammRepository.getAboutInfo()
    }
}