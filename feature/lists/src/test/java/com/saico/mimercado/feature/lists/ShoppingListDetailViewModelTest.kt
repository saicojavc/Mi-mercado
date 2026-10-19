package com.saico.mimercado.feature.lists

import com.saico.mimercado.core.model.CartItem
import com.saico.mimercado.core.model.ShoppingList
import com.saico.mimercado.core.model.ShoppingListType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ShoppingListDetailViewModelTest {

    @Test
    fun initialState_defaultsToFavoritesTabAndEmptyQuery() {
        val state = ShoppingListDetailUiState()
        assertEquals(ExplorationTab.FAVORITES, state.activeTab)
        assertEquals("", state.catalogSearchQuery)
        assertNull(state.selectedFavoriteCategory)
        assertNull(state.selectedCatalogCategory)
    }

    @Test
    fun catalogSearchQuery_updatesStateCorrectly() {
        val state = ShoppingListDetailUiState().copy(catalogSearchQuery = "Leche")
        assertEquals("Leche", state.catalogSearchQuery)
    }

    @Test
    fun activeTabSwitch_updatesTabInState() {
        val state = ShoppingListDetailUiState().copy(activeTab = ExplorationTab.CATALOG)
        assertEquals(ExplorationTab.CATALOG, state.activeTab)
    }

    @Test
    fun itemsSum_calculatesTotalQuantity() {
        val items = listOf(
            CartItem(itemId = "1", nombre = "Leche", cantidad = 2),
            CartItem(itemId = "2", nombre = "Pan", cantidad = 3)
        )
        val state = ShoppingListDetailUiState(items = items)
        assertEquals(5, state.items.sumOf { it.cantidad })
    }
}
