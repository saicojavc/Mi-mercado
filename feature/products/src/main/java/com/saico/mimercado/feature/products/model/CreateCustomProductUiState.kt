package com.saico.mimercado.feature.products.model

import com.saico.mimercado.core.domain.repository.ProductImageResult

data class CreateCustomProductUiState(
    val name: String = "",
    val category: String = "Despensa",
    val imageResults: List<ProductImageResult> = emptyList(),
    val selectedImageUrl: String? = null,
    val isSearchingImages: Boolean = false,
    val isSaving: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val isEditing: Boolean = false
)
