package com.saico.mimercado.feature.products.model

import com.saico.mimercado.core.model.ProductDetails

sealed interface ProductDetailsUiState {
    object Loading : ProductDetailsUiState
    data class Success(val details: ProductDetails) : ProductDetailsUiState
    data class Error(val message: String) : ProductDetailsUiState
}
