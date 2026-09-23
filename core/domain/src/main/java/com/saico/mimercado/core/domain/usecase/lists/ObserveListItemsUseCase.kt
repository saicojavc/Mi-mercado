package com.saico.mimercado.core.domain.usecase.lists

import com.saico.mimercado.core.domain.repository.ShoppingListRepository
import com.saico.mimercado.core.model.CartItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveListItemsUseCase @Inject constructor(
    private val repository: ShoppingListRepository
) {
    operator fun invoke(householdId: String, listId: String): Flow<List<CartItem>> =
        repository.observeListItems(householdId, listId)
}
