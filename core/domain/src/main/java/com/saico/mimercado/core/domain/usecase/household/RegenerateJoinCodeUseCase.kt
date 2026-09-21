package com.saico.mimercado.core.domain.usecase.household

import com.saico.mimercado.core.domain.repository.HouseholdRepository
import javax.inject.Inject

class RegenerateJoinCodeUseCase @Inject constructor(
    private val householdRepository: HouseholdRepository
) {
    suspend operator fun invoke(householdId: String): Result<String> {
        return householdRepository.regenerateJoinCode(householdId)
    }
}
