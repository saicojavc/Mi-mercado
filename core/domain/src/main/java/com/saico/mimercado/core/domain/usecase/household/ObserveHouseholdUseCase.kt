package com.saico.mimercado.core.domain.usecase.household

import com.saico.mimercado.core.domain.repository.HouseholdRepository
import com.saico.mimercado.core.model.Household
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveHouseholdUseCase @Inject constructor(
    private val repository: HouseholdRepository
) {
    operator fun invoke(householdId: String): Flow<Household> {
        return repository.observeHousehold(householdId)
    }
}
