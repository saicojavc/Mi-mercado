package com.saico.mimercado.core.domain.usecase.household

import com.saico.mimercado.core.domain.repository.HouseholdRepository
import com.saico.mimercado.core.model.HouseholdMember
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveHouseholdMembersUseCase @Inject constructor(
    private val repository: HouseholdRepository
) {
    operator fun invoke(householdId: String): Flow<List<HouseholdMember>> = repository.observeMembers(householdId)
}
