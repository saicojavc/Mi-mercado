package com.saico.mimercado.core.domain.usecase.lists

import com.saico.mimercado.core.domain.repository.ShoppingListRepository
import com.saico.mimercado.core.model.CartItem
import javax.inject.Inject

class AddListItemUseCase @Inject constructor(
    private val repository: ShoppingListRepository
) {
    suspend operator fun invoke(householdId: String, listId: String, item: CartItem): Result<Unit> =
        repository.addListItem(householdId, listId, item)
}
