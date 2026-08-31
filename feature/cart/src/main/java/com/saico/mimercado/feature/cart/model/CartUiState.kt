package com.saico.mimercado.feature.cart.model

import com.saico.mimercado.core.model.CartItem

data class CartUiState(
    val items: List<CartItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
