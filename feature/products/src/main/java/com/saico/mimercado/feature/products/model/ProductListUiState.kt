package com.saico.mimercado.feature.products.model

import com.saico.mimercado.core.model.Product

enum class ListMode {
    HABITUAL, DISCOVER
}

data class ProductListUiState(
    val listMode: ListMode = ListMode.HABITUAL,
    val selectedCategory: String = "Todos",
    val selectedStore: String? = null,
    val searchQuery: String = "",
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val isPaginating: Boolean = false,
    val isLastPage: Boolean = false
)
