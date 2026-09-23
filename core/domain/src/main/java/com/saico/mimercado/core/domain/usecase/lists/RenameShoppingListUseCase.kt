package com.saico.mimercado.core.domain.usecase.lists

import com.saico.mimercado.core.domain.repository.ShoppingListRepository
import javax.inject.Inject

class RenameShoppingListUseCase @Inject constructor(
    private val repository: ShoppingListRepository
) {
    suspend operator fun invoke(householdId: String, listId: String, name: String): Result<Unit> =
        repository.renameList(householdId, listId, name)
}
