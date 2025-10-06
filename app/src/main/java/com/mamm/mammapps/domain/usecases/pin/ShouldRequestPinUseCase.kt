package com.mamm.mammapps.domain.usecases.pin

import com.mamm.mammapps.domain.interfaces.MammRepository
import javax.inject.Inject

class ShouldRequestPinUseCase @Inject constructor(
    private val repository: MammRepository
) {
    operator fun invoke(): Boolean {
        return repository.shouldRequestPin()
    }
}
