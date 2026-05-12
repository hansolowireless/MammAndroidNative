package com.mamm.mammapps.domain.usecases.login

import com.mamm.mammapps.domain.interfaces.LoginRepository
import javax.inject.Inject

class AuthLoginCodeUseCase @Inject constructor(
    private val repository: LoginRepository
) {
    suspend operator fun invoke(code: String): Result<Unit> {
        return repository.authLoginCode(code)
    }
}
