package com.saico.mimercado.feature.products.model

import com.saico.mimercado.core.model.Product
import com.saico.mimercado.core.model.ShoppingListSummary

enum class ListMode {
    HABITUAL, DISCOVER
}

data class ProductListUiState(
    val listMode: ListMode = ListMode.DISCOVER,
    val selectedCategory: String = "Todos",
    val selectedStore: String? = null,
    val searchQuery: String = "",
    val products: List<Product> = emptyList(),
    val availableLists: List<ShoppingListSummary> = emptyList(),
    val targetProductForAdd: Product? = null,
    val toastMessage: String? = null,
    val isLoading: Boolean = false,
    val isPaginating: Boolean = false,
    val isLastPage: Boolean = false,
    val isCatalogExpanded: Boolean = true
)
