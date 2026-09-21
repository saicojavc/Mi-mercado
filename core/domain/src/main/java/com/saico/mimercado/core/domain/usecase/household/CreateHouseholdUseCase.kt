package com.saico.mimercado.core.domain.usecase.household

import com.saico.mimercado.core.domain.repository.HouseholdRepository
import com.saico.mimercado.core.domain.repository.UserRepository
import javax.inject.Inject

class CreateHouseholdUseCase @Inject constructor(
    private val householdRepository: HouseholdRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(name: String, ownerUid: String): Result<String> {
        return householdRepository.createHousehold(name, ownerUid).fold(
            onSuccess = { householdId ->
                userRepository.updateHouseholdId(ownerUid, householdId).fold(
                    onSuccess = { Result.success(householdId) },
                    onFailure = { Result.failure(it) }
                )
            },
            onFailure = { Result.failure(it) }
        )
    }
}
