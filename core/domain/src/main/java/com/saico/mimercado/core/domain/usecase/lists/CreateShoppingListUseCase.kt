package com.saico.mimercado.core.domain.usecase.lists

import com.saico.mimercado.core.domain.repository.ShoppingListRepository
import com.saico.mimercado.core.model.ListCoverTheme
import javax.inject.Inject

class CreateShoppingListUseCase @Inject constructor(
    private val repository: ShoppingListRepository
) {
    suspend operator fun invoke(
        householdId: String,
        name: String,
        coverTheme: ListCoverTheme,
        createdBy: String
    ): Result<String> = repository.createList(householdId, name, coverTheme, createdBy)
}
