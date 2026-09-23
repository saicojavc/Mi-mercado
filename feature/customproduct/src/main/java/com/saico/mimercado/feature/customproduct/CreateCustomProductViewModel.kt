package com.saico.mimercado.feature.customproduct

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.saico.mimercado.core.common.UsdaImageResolver
import com.saico.mimercado.core.domain.usecase.products.ProductsUseCases
import com.saico.mimercado.core.ui.navigation.routes.products.CreateCustomProductRoute
import com.saico.mimercado.feature.customproduct.model.CreateCustomProductUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateCustomProductViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val useCases: ProductsUseCases
) : ViewModel() {

    private val route: CreateCustomProductRoute = savedStateHandle.toRoute()

    private val _uiState = MutableStateFlow(CreateCustomProductUiState())
    val uiState = _uiState.asStateFlow()

    init {
        _uiState.update { it.copy(
            name = route.prefillName ?: "",
            isEditing = route.editingProductId != null
        ) }

        if (route.editingProductId != null) {
            loadExistingProduct(route.editingProductId!!)
        }
    }

    private fun loadExistingProduct(productId: String) {
        viewModelScope.launch {
            useCases.getCustomProduct(productId).onSuccess { product ->
                product?.let { p ->
                    _uiState.update { it.copy(
                        name = p.nombre,
                        category = p.categoria,
                        selectedImageUrl = p.imageUrl
                    ) }
                }
            }
        }
    }

    fun onNameChanged(name: String) {
        val quickSuggestion = UsdaImageResolver.getSearchThumbnailUrl("Personalizado", name)
        _uiState.update { it.copy(
            name = name,
            selectedImageUrl = if (it.selectedImageUrl == null || it.selectedImageUrl.startsWith("https://tse")) quickSuggestion else it.selectedImageUrl
        ) }
    }

    fun onCategoryChanged(category: String) {
        _uiState.update { it.copy(category = category) }
    }

    fun searchImages() {
        val query = _uiState.value.name
        if (query.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSearchingImages = true, error = null) }
            useCases.searchProductImages(query).fold(
                onSuccess = { results ->
                    _uiState.update { it.copy(imageResults = results, isSearchingImages = false) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(error = error.message, isSearchingImages = false) }
                }
            )
        }
    }

    fun onImageSelected(imageUrl: String) {
        _uiState.update { it.copy(selectedImageUrl = imageUrl) }
    }

    fun save() {
        val state = _uiState.value
        if (state.name.isBlank()) {
            _uiState.update { it.copy(error = "El nombre es obligatorio") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }

            useCases.createCustomProduct(
                nombre = state.name,
                categoria = state.category,
                imageUrl = state.selectedImageUrl ?: "",
                id = route.editingProductId
            ).fold(
                onSuccess = {
                    _uiState.update { it.copy(isSaving = false, isSuccess = true) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isSaving = false, error = error.message) }
                }
            )
        }
    }
}
