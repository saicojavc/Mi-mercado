package com.saico.mimercado.feature.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saico.mimercado.core.common.CategoryMapper
import com.saico.mimercado.core.domain.usecase.products.ProductsUseCases
import com.saico.mimercado.core.model.Product
import com.saico.mimercado.core.ui.navigation.NavigationCommand
import com.saico.mimercado.core.ui.navigation.Navigator
import com.saico.mimercado.core.ui.navigation.routes.cart.CartRoute
import com.saico.mimercado.feature.products.model.ListMode
import com.saico.mimercado.feature.products.model.ProductListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val navigator: Navigator,
    private val useCases: ProductsUseCases
) : ViewModel() {
    val categories = listOf("Todos", "Lácteos", "Panadería", "Carnes", "Frutas y verduras", "Despensa", "Limpieza", "Bebidas")
    val stores = listOf("Walmart", "Costco", "Publix", "Target", "Kroger", "BJ's", "Fresco y Más", "Martins", "Whole Foods", "Safeway", "ALDI")
    
    private val _uiState = MutableStateFlow(ProductListUiState())
    val uiState: StateFlow<ProductListUiState> = _uiState.asStateFlow()

    private val _discoverProducts = MutableStateFlow<List<Product>>(emptyList())
    
    val filteredProducts: StateFlow<List<Product>> = combine(
        _uiState,
        _discoverProducts,
        useCases.getFavorites()
    ) { state, discover, favorites ->
        val baseList = if (state.listMode == ListMode.HABITUAL) favorites else discover
        
        baseList.filter { product ->
            val matchesCategory = state.selectedCategory == "Todos" || CategoryMapper.matches(product.categoria, state.selectedCategory)
            val matchesStore = state.selectedStore == null || product.brands.contains(state.selectedStore, ignoreCase = true)
            val matchesQuery = state.searchQuery.isBlank() || 
                              product.nombre.contains(state.searchQuery, ignoreCase = true) || 
                              product.brands.contains(state.searchQuery, ignoreCase = true) || 
                              product.upc == state.searchQuery
            matchesCategory && matchesStore && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private var currentPage = 1
    private var searchJob: Job? = null

    init {
        loadProducts(reset = true)
    }

    fun setListMode(mode: ListMode) {
        _uiState.update { it.copy(listMode = mode) }
        if (mode == ListMode.DISCOVER && _discoverProducts.value.isEmpty()) {
            loadProducts(reset = true)
        }
    }

    fun onSearchQueryChanged(query: String, isScan: Boolean = false) {
        _uiState.update { it.copy(searchQuery = query) }
        
        if (isScan) {
            _uiState.update { it.copy(listMode = ListMode.DISCOVER) }
            searchJob?.cancel()
            loadProducts(reset = true)
            return
        }

        if (_uiState.value.listMode == ListMode.DISCOVER) {
            searchJob?.cancel()
            searchJob = viewModelScope.launch {
                delay(500)
                loadProducts(reset = true)
            }
        }
    }

    fun selectCategory(category: String) {
        if (_uiState.value.selectedCategory == category) return
        _uiState.update { it.copy(selectedCategory = category) }
        if (_uiState.value.listMode == ListMode.DISCOVER) {
            loadProducts(reset = true)
        }
    }

    fun onStoreSelected(store: String?) {
        _uiState.update { it.copy(selectedStore = if (it.selectedStore == store) null else store) }
        if (_uiState.value.listMode == ListMode.DISCOVER) {
            loadProducts(reset = true)
        }
    }

    fun loadNextPage() {
        if (_uiState.value.listMode == ListMode.HABITUAL) return
        if (_uiState.value.isLoading || _uiState.value.isPaginating || _uiState.value.isLastPage) return
        loadProducts(reset = false)
    }

    private fun loadProducts(reset: Boolean) {
        if (reset) {
            currentPage = 1
            _uiState.update { it.copy(isLastPage = false) }
        }

        viewModelScope.launch {
            if (reset) _uiState.update { it.copy(isLoading = true) } 
            else _uiState.update { it.copy(isPaginating = true) }
            
            val result = useCases.getProducts(
                category = if (_uiState.value.selectedCategory == "Todos") null else _uiState.value.selectedCategory,
                searchQuery = if (_uiState.value.searchQuery.isBlank()) null else _uiState.value.searchQuery,
                store = _uiState.value.selectedStore,
                page = currentPage
            )

            result.onSuccess { newProducts ->
                if (reset) {
                    _discoverProducts.value = newProducts
                } else {
                    _discoverProducts.value = _discoverProducts.value + newProducts
                }
                
                val isLast = newProducts.isEmpty()
                if (!isLast) currentPage++
                _uiState.update { it.copy(isLastPage = isLast) }
                
            }.onFailure {
                if (reset) _discoverProducts.value = emptyList()
            }

            if (reset) _uiState.update { it.copy(isLoading = false) } 
            else _uiState.update { it.copy(isPaginating = false) }
        }
    }

    fun navigateToCart() {
        navigator.navigate(NavigationCommand.NavigateTo(CartRoute))
    }
}
