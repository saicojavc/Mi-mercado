package com.saico.mimercado.core.domain.usecase.lists

import com.saico.mimercado.core.domain.repository.ShoppingListRepository
import javax.inject.Inject

class UpdateListItemQuantityUseCase @Inject constructor(
    private val repository: ShoppingListRepository
) {
    suspend operator fun invoke(householdId: String, listId: String, itemId: String, newQuantity: Int): Result<Unit> =
        repository.updateListItemQuantity(householdId, listId, itemId, newQuantity)
}
