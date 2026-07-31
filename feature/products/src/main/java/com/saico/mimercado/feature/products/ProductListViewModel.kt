package com.saico.mimercado.feature.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saico.mimercado.core.domain.repository.ProductRepository
import com.saico.mimercado.core.model.Product
import com.saico.mimercado.core.ui.navigation.NavigationCommand
import com.saico.mimercado.core.ui.navigation.Navigator
import com.saico.mimercado.core.ui.navigation.routes.cart.CartRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val navigator: Navigator,
    private val productRepository: ProductRepository
) : ViewModel() {
    val categories = listOf("Todos", "Lácteos", "Panadería", "Carnes", "Frutas y verduras", "Despensa", "Limpieza", "Bebidas")
    
    private val _selectedCategory = MutableStateFlow("Todos")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filteredProducts = MutableStateFlow<List<Product>>(emptyList())
    val filteredProducts: StateFlow<List<Product>> = _filteredProducts.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isPaginating = MutableStateFlow(false)
    val isPaginating: StateFlow<Boolean> = _isPaginating.asStateFlow()

    private var currentPage = 1
    private var isLastPage = false
    private var searchJob: Job? = null

    init {
        loadProducts(reset = true)
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500) // Debounce
            loadProducts(reset = true)
        }
    }

    fun selectCategory(category: String) {
        if (_selectedCategory.value == category) return
        _selectedCategory.value = category
        loadProducts(reset = true)
    }

    fun loadNextPage() {
        if (_isLoading.value || _isPaginating.value || isLastPage) return
        loadProducts(reset = false)
    }

    private fun loadProducts(reset: Boolean) {
        if (reset) {
            currentPage = 1
            isLastPage = false
        }

        viewModelScope.launch {
            if (reset) _isLoading.value = true else _isPaginating.value = true
            
            val result = productRepository.getProducts(
                category = if (_selectedCategory.value == "Todos") null else _selectedCategory.value,
                searchQuery = if (_searchQuery.value.isBlank()) null else _searchQuery.value,
                page = currentPage
            )

            result.onSuccess { newProducts ->
                if (reset) {
                    _filteredProducts.value = newProducts
                } else {
                    _filteredProducts.value = _filteredProducts.value + newProducts
                }
                
                isLastPage = newProducts.isEmpty()
                if (!isLastPage) currentPage++
                
            }.onFailure {
                if (reset) _filteredProducts.value = emptyList()
            }

            if (reset) _isLoading.value = false else _isPaginating.value = false
        }
    }

    fun navigateToCart() {
        navigator.navigate(NavigationCommand.NavigateTo(CartRoute))
    }
}
