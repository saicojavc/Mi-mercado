package com.saico.mimercado.feature.products

import com.saico.mimercado.core.model.DecoratedProduct
import com.saico.mimercado.core.model.Product
import com.saico.mimercado.feature.products.model.ListMode
import com.saico.mimercado.feature.products.model.ProductListUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ProductListViewModelTest {

    @Test
    fun initialState_defaultsToDiscoverModeAndEmptySearch() {
        val state = ProductListUiState()
        assertEquals(ListMode.DISCOVER, state.listMode)
        assertEquals("Todos", state.selectedCategory)
        assertNull(state.selectedStore)
        assertEquals("", state.searchQuery)
        assertNull(state.toastMessage)
        assertNull(state.targetProductForAdd)
    }

    @Test
    fun searchQuery_updatesUiState() {
        val state = ProductListUiState().copy(searchQuery = "Manzana")
        assertEquals("Manzana", state.searchQuery)
    }

    @Test
    fun targetProductForAdd_setsProductForDialog() {
        val product = Product(id = "1", upc = "12345", nombre = "Leche Almendras", categoria = "Lácteos")
        val state = ProductListUiState().copy(targetProductForAdd = product)
        assertEquals("Leche Almendras", state.targetProductForAdd?.nombre)
    }

    @Test
    fun decoratedProduct_calculatesAdditionalBrandsCount() {
        val product = Product(id = "1", upc = "12345", nombre = "Pan Integral", brands = "Bimbo")
        val decorated = DecoratedProduct(product = product, additionalBrandsCount = 2)
        assertEquals("Pan Integral", decorated.product.nombre)
        assertEquals(2, decorated.additionalBrandsCount)
    }
}
