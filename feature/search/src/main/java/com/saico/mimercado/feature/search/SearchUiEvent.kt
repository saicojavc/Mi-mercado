package com.saico.mimercado.feature.search

import com.saico.mimercado.core.model.Product

sealed interface SearchUiEvent {
    data class QueryChanged(val query: String) : SearchUiEvent
    data class CategorySelected(val category: String) : SearchUiEvent
    data class AddToCart(val product: Product) : SearchUiEvent
    data class ShowMessage(val message: String) : SearchUiEvent
}
