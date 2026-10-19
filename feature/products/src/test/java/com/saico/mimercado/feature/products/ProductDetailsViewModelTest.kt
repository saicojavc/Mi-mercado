package com.saico.mimercado.feature.products

import com.saico.mimercado.core.model.ProductDetails
import com.saico.mimercado.feature.products.model.ProductDetailsUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ProductDetailsViewModelTest {

    @Test
    fun loadingState_isInitialState() {
        val state: ProductDetailsUiState = ProductDetailsUiState.Loading
        assertTrue(state is ProductDetailsUiState.Loading)
    }

    @Test
    fun successState_containsProductDetails() {
        val details = ProductDetails(
            id = "100",
            name = "Aceite de Oliva Extra Virgen",
            brand = "Borges",
            category = "Despensa",
            imageUrl = "https://example.com/oil.jpg",
            upc = "779123456789",
            ingredients = "Aceite de oliva virgen",
            nutrients = mapOf("Grasas totales" to 14.0)
        )
        val state = ProductDetailsUiState.Success(details)
        assertEquals("Aceite de Oliva Extra Virgen", state.details.name)
        assertEquals("Borges", state.details.brand)
        assertEquals(1, state.details.nutrients.size)
    }

    @Test
    fun errorState_containsErrorMessage() {
        val state = ProductDetailsUiState.Error("Producto no encontrado")
        assertEquals("Producto no encontrado", state.message)
    }
}
