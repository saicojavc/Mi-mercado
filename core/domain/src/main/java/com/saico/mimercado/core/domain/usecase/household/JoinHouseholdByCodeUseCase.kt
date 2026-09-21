package com.saico.mimercado.core.domain.usecase.household

import com.saico.mimercado.core.domain.repository.JoinCodeRepository
import com.saico.mimercado.core.domain.repository.UserRepository
import javax.inject.Inject

class JoinHouseholdByCodeUseCase @Inject constructor(
    private val joinCodeRepository: JoinCodeRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(code: String, uid: String): Result<Unit> {
        val sanitizedCode = code.trim().uppercase()
        if (sanitizedCode.length != 6) {
            return Result.failure(IllegalArgumentException("El código debe tener exactamente 6 caracteres"))
        }
        
        return joinCodeRepository.findHouseholdId(sanitizedCode).fold(
            onSuccess = { householdId ->
                if (householdId == null) {
                    Result.failure(Exception("El código de hogar ingresado no existe o es inválido"))
                } else {
                    userRepository.updateHouseholdId(uid, householdId)
                }
            },
            onFailure = { Result.failure(it) }
        )
    }
}
