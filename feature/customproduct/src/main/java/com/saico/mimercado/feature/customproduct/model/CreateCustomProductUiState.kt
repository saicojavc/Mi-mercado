package com.saico.mimercado.feature.customproduct.model

import com.saico.mimercado.core.domain.repository.ProductImageResult

data class CreateCustomProductUiState(
    val name: String = "",
    val category: String = "Despensa",
    val isEditing: Boolean = false,
    val selectedImageUrl: String? = null,
    val imageResults: List<ProductImageResult> = emptyList(),
    val isSearchingImages: Boolean = false,
    val isSaving: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)
