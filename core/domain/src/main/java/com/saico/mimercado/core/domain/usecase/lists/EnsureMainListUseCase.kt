package com.saico.mimercado.core.domain.usecase.lists

import com.saico.mimercado.core.domain.repository.ShoppingListRepository
import javax.inject.Inject

class EnsureMainListUseCase @Inject constructor(
    private val repository: ShoppingListRepository
) {
    suspend operator fun invoke(householdId: String, createdBy: String): Result<String> =
        repository.ensureMainListExists(householdId, createdBy)
}
