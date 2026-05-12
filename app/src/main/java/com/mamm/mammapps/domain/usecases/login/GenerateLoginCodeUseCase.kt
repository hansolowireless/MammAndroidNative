package com.mamm.mammapps.domain.usecases.login

import com.mamm.mammapps.domain.interfaces.LoginRepository
import com.mamm.mammapps.domain.model.loginwithcode.LoginCodeGenerate
import javax.inject.Inject

class GenerateLoginCodeUseCase @Inject constructor(
    private val repository: LoginRepository
) {
    suspend operator fun invoke(): Result<LoginCodeGenerate> {
        return repository.generateLoginCode()
    }
}
