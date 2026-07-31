package com.saico.mimercado.feature.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saico.mimercado.core.domain.repository.FavoriteRepository
import com.saico.mimercado.core.domain.repository.ProductRepository
import com.saico.mimercado.core.model.Product
import com.saico.mimercado.core.ui.navigation.NavigationCommand
import com.saico.mimercado.core.ui.navigation.Navigator
import com.saico.mimercado.core.ui.navigation.routes.cart.CartRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class ListMode {
    HABITUAL, DISCOVER
}

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val navigator: Navigator,
    private val productRepository: ProductRepository,
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {
    val categories = listOf("Todos", "Lácteos", "Panadería", "Carnes", "Frutas y verduras", "Despensa", "Limpieza", "Bebidas")
    val stores = listOf("Walmart", "Costco", "Publix", "Target", "Kroger", "BJ's", "Fresco y Más", "Martins", "Whole Foods", "Safeway", "ALDI")
    
    private val _listMode = MutableStateFlow(ListMode.HABITUAL)
    val listMode: StateFlow<ListMode> = _listMode.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Todos")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _selectedStore = MutableStateFlow<String?>(null)
    val selectedStore: StateFlow<String?> = _selectedStore.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _discoverProducts = MutableStateFlow<List<Product>>(emptyList())
    
    // Combining UI filters into a single state
    private val filterState = combine(
        _selectedCategory,
        _selectedStore,
        _searchQuery
    ) { category, store, query -> Triple(category, store, query) }

    val filteredProducts: StateFlow<List<Product>> = combine(
        _listMode,
        _discoverProducts,
        favoriteRepository.getFavorites(),
        filterState
    ) { mode, discover, favorites, filters ->
        val (category, store, query) = filters
        val baseList = if (mode == ListMode.HABITUAL) favorites else discover
        
        baseList.filter { product ->
            val matchesCategory = category == "Todos" || product.categoria.contains(category, ignoreCase = true)
            val matchesStore = store == null || product.brands.contains(store, ignoreCase = true)
            val matchesQuery = query.isBlank() || product.nombre.contains(query, ignoreCase = true) || product.brands.contains(query, ignoreCase = true) || product.upc == query
            matchesCategory && matchesStore && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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

    fun setListMode(mode: ListMode) {
        _listMode.value = mode
        if (mode == ListMode.DISCOVER && _discoverProducts.value.isEmpty()) {
            loadProducts(reset = true)
        }
    }

    fun onSearchQueryChanged(query: String, isScan: Boolean = false) {
        _searchQuery.value = query
        
        // If it's a barcode scan, we ALWAYS want to search globally and switch to Discover mode
        if (isScan) {
            _listMode.value = ListMode.DISCOVER
            searchJob?.cancel()
            loadProducts(reset = true)
            return
        }

        if (_listMode.value == ListMode.DISCOVER) {
            searchJob?.cancel()
            searchJob = viewModelScope.launch {
                delay(500) // Debounce
                loadProducts(reset = true)
            }
        }
    }

    fun selectCategory(category: String) {
        if (_selectedCategory.value == category) return
        _selectedCategory.value = category
        if (_listMode.value == ListMode.DISCOVER) {
            loadProducts(reset = true)
        }
    }

    fun onStoreSelected(store: String?) {
        _selectedStore.value = if (_selectedStore.value == store) null else store
        if (_listMode.value == ListMode.DISCOVER) {
            loadProducts(reset = true)
        }
    }

    fun loadNextPage() {
        if (_listMode.value == ListMode.HABITUAL) return
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
                store = _selectedStore.value,
                page = currentPage
            )

            result.onSuccess { newProducts ->
                if (reset) {
                    _discoverProducts.value = newProducts
                } else {
                    _discoverProducts.value = _discoverProducts.value + newProducts
                }
                
                isLastPage = newProducts.isEmpty()
                if (!isLastPage) currentPage++
                
            }.onFailure {
                if (reset) _discoverProducts.value = emptyList()
            }

            if (reset) _isLoading.value = false else _isPaginating.value = false
        }
    }

    fun navigateToCart() {
        navigator.navigate(NavigationCommand.NavigateTo(CartRoute))
    }
}
