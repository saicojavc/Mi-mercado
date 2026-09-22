package com.saico.mimercado.core.domain.usecase.lists

import com.saico.mimercado.core.domain.repository.ShoppingListRepository
import com.saico.mimercado.core.model.ShoppingListSummary
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveShoppingListsUseCase @Inject constructor(
    private val repository: ShoppingListRepository
) {
    operator fun invoke(householdId: String): Flow<List<ShoppingListSummary>> =
        repository.observeLists(householdId)
}
