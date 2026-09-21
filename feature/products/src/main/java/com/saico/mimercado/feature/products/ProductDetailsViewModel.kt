package com.saico.mimercado.feature.products

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.saico.mimercado.core.common.ImageCacheManager
import com.saico.mimercado.core.domain.usecase.products.ProductsUseCases
import com.saico.mimercado.core.model.Product
import com.saico.mimercado.core.model.ProductDetails
import com.saico.mimercado.core.ui.navigation.Navigator
import com.saico.mimercado.core.ui.navigation.NavigationCommand
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
    private val useCases: ProductsUseCases,
    val imageCache: ImageCacheManager,
    private val navigator: Navigator
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
            if (route.isCustom) {
                useCases.getCustomProduct(fdcId)
                    .onSuccess { product ->
                        if (product != null) {
                            _uiState.value = ProductDetailsUiState.Success(
                                ProductDetails(
                                    id = product.id,
                                    name = product.nombre,
                                    brand = product.brands,
                                    category = product.categoria,
                                    imageUrl = product.imageUrl,
                                    upc = product.upc,
                                    ingredients = "Producto personalizado",
                                    nutrients = emptyMap()
                                )
                            )
                        } else {
                            _uiState.value = ProductDetailsUiState.Error("Product not found")
                        }
                    }
                    .onFailure { error ->
                        _uiState.value = ProductDetailsUiState.Error(error.message ?: "Unknown error")
                    }
            } else {
                useCases.getProductDetails(fdcId)
                    .onSuccess { details ->
                        _uiState.value = ProductDetailsUiState.Success(details)
                    }
                    .onFailure { error ->
                        _uiState.value = ProductDetailsUiState.Error(error.message ?: "Unknown error")
                    }
            }
        }
    }

    fun deleteCustomProduct() {
        viewModelScope.launch {
            useCases.deleteCustomProduct(fdcId).onSuccess {
                navigator.navigate(NavigationCommand.PopBackstack)
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
