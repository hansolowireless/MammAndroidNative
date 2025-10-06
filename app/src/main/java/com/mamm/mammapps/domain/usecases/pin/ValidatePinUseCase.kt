package com.mamm.mammapps.domain.usecases.pin

import com.mamm.mammapps.domain.interfaces.MammRepository
import javax.inject.Inject

class ValidatePinUseCase @Inject constructor(
    private val repository: MammRepository
) {
    /**
     * Valida el PIN proporcionado. Si es correcto, actualiza la marca de tiempo
     * y devuelve true. De lo contrario, devuelve false.
     * @param pin El PIN introducido por el usuario.
     * @return Boolean - true si el PIN es válido, false si no.
     */
    operator fun invoke(pin: String): Boolean {
        val isPinCorrect = repository.validatePin(pin)
        if (isPinCorrect) {
            repository.savePinSuccessTimestamp()
        }
        return isPinCorrect
    }
}
