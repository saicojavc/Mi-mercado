package com.saico.mimercado.feature.products

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.saico.mimercado.core.domain.usecase.products.ProductsUseCases
import com.saico.mimercado.core.model.Product
import com.saico.mimercado.core.model.ProductDetails
import com.saico.mimercado.core.ui.navigation.routes.products.ProductDetailsRoute
import com.saico.mimercado.feature.products.model.ProductDetailsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val useCases: ProductsUseCases
) : ViewModel() {

    private val route: ProductDetailsRoute = savedStateHandle.toRoute()
    private val fdcId = route.fdcId

    private val _uiState = MutableStateFlow<ProductDetailsUiState>(ProductDetailsUiState.Loading)
    val uiState: StateFlow<ProductDetailsUiState> = _uiState.asStateFlow()

    val isFavorite = useCases.isFavorite(fdcId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    init {
        loadDetails()
    }

    private fun loadDetails() {
        viewModelScope.launch {
            _uiState.value = ProductDetailsUiState.Loading
            useCases.getProductDetails(fdcId)
                .onSuccess { details ->
                    _uiState.value = ProductDetailsUiState.Success(details)
                }
                .onFailure { error ->
                    _uiState.value = ProductDetailsUiState.Error(error.message ?: "Unknown error")
                }
        }
    }

    fun toggleFavorite(details: ProductDetails) {
        viewModelScope.launch {
            useCases.toggleFavorite(
                Product(
                    id = details.id,
                    upc = details.upc,
                    nombre = details.name,
                    categoria = details.category,
                    imageUrl = details.imageUrl,
                    brands = details.brand,
                    isFavorite = true
                )
            )
        }
    }
}
