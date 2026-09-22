package com.saico.mimercado.core.domain.usecase.lists

import com.saico.mimercado.core.domain.repository.ShoppingListRepository
import com.saico.mimercado.core.model.ShoppingList
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveShoppingListUseCase @Inject constructor(
    private val repository: ShoppingListRepository
) {
    operator fun invoke(householdId: String, listId: String): Flow<ShoppingList?> =
        repository.observeList(householdId, listId)
}
