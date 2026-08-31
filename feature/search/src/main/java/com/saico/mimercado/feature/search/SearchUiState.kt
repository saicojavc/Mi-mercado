package com.saico.mimercado.feature.search

import com.saico.mimercado.core.model.Product

data class SearchUiState(
    val searchQuery: String = "",
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val categories: List<String> = listOf("Todos", "Frutas", "Verduras", "Lácteos", "Panadería", "Carnes", "Bebidas"),
    val selectedCategory: String = "Todos"
)
