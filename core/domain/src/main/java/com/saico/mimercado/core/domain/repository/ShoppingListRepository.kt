package com.saico.mimercado.core.domain.repository

import com.saico.mimercado.core.model.CartItem
import com.saico.mimercado.core.model.ListCoverTheme
import com.saico.mimercado.core.model.ShoppingList
import com.saico.mimercado.core.model.ShoppingListSummary
import kotlinx.coroutines.flow.Flow

interface ShoppingListRepository {
    fun observeLists(householdId: String): Flow<List<ShoppingListSummary>>
    fun observeList(householdId: String, listId: String): Flow<ShoppingList?>
    suspend fun createList(householdId: String, name: String, coverTheme: ListCoverTheme, createdBy: String): Result<String>
    suspend fun renameList(householdId: String, listId: String, name: String): Result<Unit>
    suspend fun deleteList(householdId: String, listId: String): Result<Unit>
    suspend fun ensureMainListExists(householdId: String, createdBy: String): Result<String>
    
    // Items management within a specific list
    fun observeListItems(householdId: String, listId: String): Flow<List<CartItem>>
    suspend fun addListItem(householdId: String, listId: String, item: CartItem): Result<Unit>
    suspend fun updateListItemQuantity(householdId: String, listId: String, itemId: String, newQuantity: Int): Result<Unit>
    suspend fun removeListItem(householdId: String, listId: String, itemId: String): Result<Unit>
    suspend fun clearListItems(householdId: String, listId: String): Result<Unit>
}
