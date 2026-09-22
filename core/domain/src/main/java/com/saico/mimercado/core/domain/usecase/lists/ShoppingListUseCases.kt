package com.saico.mimercado.core.domain.usecase.lists

import javax.inject.Inject

data class ShoppingListUseCases @Inject constructor(
    val observeLists: ObserveShoppingListsUseCase,
    val observeList: ObserveShoppingListUseCase,
    val observeListItems: ObserveListItemsUseCase,
    val createList: CreateShoppingListUseCase,
    val renameList: RenameShoppingListUseCase,
    val deleteList: DeleteShoppingListUseCase,
    val ensureMainList: EnsureMainListUseCase,
    val addListItem: AddListItemUseCase,
    val updateListItemQuantity: UpdateListItemQuantityUseCase,
    val removeListItem: RemoveListItemUseCase
)
