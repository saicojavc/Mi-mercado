package com.saico.mimercado.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saico.mimercado.core.common.UserProvider
import com.saico.mimercado.core.domain.usecase.cart.AddToCartUseCase
import com.saico.mimercado.core.domain.usecase.products.GetProductsUseCase
import com.saico.mimercado.core.model.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    private val addToCartUseCase: AddToCartUseCase,
    private val userProvider: UserProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val searchQuery = MutableStateFlow("")

    init {
        observeSearchQuery()
    }

    @OptIn(FlowPreview::class)
    private fun observeSearchQuery() {
        searchQuery
            .debounce(500)
            .distinctUntilChanged()
            .onEach { query ->
                if (query.length >= 3 || query.isEmpty()) {
                    performSearch(query, _uiState.value.selectedCategory)
                }
            }
            .launchIn(viewModelScope)
    }

    fun onEvent(event: SearchUiEvent) {
        when (event) {
            is SearchUiEvent.QueryChanged -> {
                _uiState.update { it.copy(searchQuery = event.query) }
                searchQuery.value = event.query
            }
            is SearchUiEvent.CategorySelected -> {
                _uiState.update { it.copy(selectedCategory = event.category) }
                performSearch(_uiState.value.searchQuery, event.category)
            }
            is SearchUiEvent.AddToCart -> {
                addToCart(event.product)
            }
            is SearchUiEvent.ShowMessage -> {
                // Podría manejarse con un canal de efectos, por ahora simplificamos
            }
        }
    }

    private fun performSearch(query: String, category: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            val mappedCategory = if (category == "Todos") null else category
            val result = getProductsUseCase(
                category = mappedCategory,
                searchQuery = query.ifEmpty { null },
                store = null,
                page = 1
            )

            result.fold(
                onSuccess = { products ->
                    _uiState.update { it.copy(products = products, isLoading = false) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(error = error.message, isLoading = false) }
                }
            )
        }
    }

    private fun addToCart(product: Product) {
        viewModelScope.launch {
            val userId = userProvider.getUserId()
            addToCartUseCase(product, userId)
            // Aquí se podría emitir un evento de éxito si fuera necesario
        }
    }
}
