package com.saico.mimercado.core.domain.usecase.household

import com.saico.mimercado.core.domain.repository.HouseholdRepository
import javax.inject.Inject

class UpdateHouseholdNameUseCase @Inject constructor(
    private val repository: HouseholdRepository
) {
    suspend operator fun invoke(householdId: String, name: String): Result<Unit> =
        repository.updateHouseholdName(householdId, name)
}
