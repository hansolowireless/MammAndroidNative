package com.mamm.mammapps.domain.usecases.login

import com.mamm.mammapps.domain.interfaces.EPGRepository
import com.mamm.mammapps.domain.interfaces.LoginRepository
import com.mamm.mammapps.domain.model.loginwithcode.LoginCodeStatus
import javax.inject.Inject

class PollLoginCodeStatusUseCase @Inject constructor(
    private val repository: LoginRepository,
    private val epgRepository: EPGRepository
) {
    suspend operator fun invoke(code: String): Result<LoginCodeStatus> {
        return repository.checkLoginCodeStatus(code).onSuccess { status ->
            if (status.data != null) {
                epgRepository.clearCache()
            }
        }
    }
}
