package com.saico.mimercado.feature.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saico.mimercado.core.common.CategoryMapper
import com.saico.mimercado.core.common.ImageCacheManager
import com.saico.mimercado.core.domain.repository.AuthRepository
import com.saico.mimercado.core.domain.usecase.auth.ObserveUserProfileUseCase
import com.saico.mimercado.core.domain.usecase.lists.ShoppingListUseCases
import com.saico.mimercado.core.domain.usecase.products.ProductsUseCases
import com.saico.mimercado.core.model.CartItem
import com.saico.mimercado.core.model.DecoratedProduct
import com.saico.mimercado.core.model.ListCoverTheme
import com.saico.mimercado.core.model.Product
import com.saico.mimercado.core.model.ShoppingListSummary
import com.saico.mimercado.core.model.ShoppingListType
import com.saico.mimercado.core.ui.navigation.NavigationCommand
import com.saico.mimercado.core.ui.navigation.Navigator
import com.saico.mimercado.core.ui.navigation.routes.cart.CartRoute
import com.saico.mimercado.feature.products.model.ListMode
import com.saico.mimercado.feature.products.model.ProductListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val navigator: Navigator,
    private val useCases: ProductsUseCases,
    private val authRepository: AuthRepository,
    private val observeUserProfileUseCase: ObserveUserProfileUseCase,
    private val shoppingListUseCases: ShoppingListUseCases,
    val imageCache: ImageCacheManager
) : ViewModel() {
    val categories = listOf("Todos", "Lácteos", "Panadería", "Carnes", "Frutas y verduras", "Despensa", "Limpieza", "Bebidas")
    val stores = listOf("Walmart", "Costco", "Publix", "Target", "Kroger", "BJ's", "Fresco y Más", "Martins", "Whole Foods", "Safeway", "ALDI")

    private val _uiState = MutableStateFlow(ProductListUiState())
    val uiState: StateFlow<ProductListUiState> = _uiState.asStateFlow()

    private val _discoverProducts = MutableStateFlow<List<Product>>(emptyList())
    private var householdId: String = ""
    private var currentUserUid: String = ""
    private var currentUserDisplayName: String = ""
    private var currentUserAvatar: String? = null

    val filteredProducts: StateFlow<List<DecoratedProduct>> = combine(
        _uiState,
        _discoverProducts,
        useCases.getFavorites()
    ) { state, discover, favorites ->
        val baseList = if (state.listMode == ListMode.HABITUAL) favorites else discover

        val filtered = baseList.filter { product ->
            val matchesCategory = state.selectedCategory == "Todos" || CategoryMapper.matchesSmart(product.categoria, product.nombre, state.selectedCategory)
            val matchesStore = state.selectedStore == null || product.brands.contains(state.selectedStore, ignoreCase = true)
            val matchesQuery = state.searchQuery.isBlank() ||
                              product.nombre.contains(state.searchQuery, ignoreCase = true) ||
                              product.brands.contains(state.searchQuery, ignoreCase = true) ||
                              product.upc == state.searchQuery
            matchesCategory && matchesStore && matchesQuery
        }

        if (state.listMode == ListMode.DISCOVER) {
            filtered.groupBy { it.nombre.lowercase().trim() }
                .map { (_, group) ->
                    val first = group.first()
                    val distinctBrands = group.map { it.brands.lowercase().trim() }.distinct()
                    DecoratedProduct(
                        product = first,
                        additionalBrandsCount = if (distinctBrands.size > 1) distinctBrands.size - 1 else 0
                    )
                }
        } else {
            filtered.map { DecoratedProduct(it) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private var currentPage = 1
    private var searchJob: Job? = null

    init {
        loadProducts(reset = true)
        observeLists()
    }

    private fun observeLists() {
        viewModelScope.launch {
            authRepository.observeAuthState().flatMapLatest { authUser ->
                currentUserUid = authUser?.uid ?: ""
                currentUserDisplayName = authUser?.displayName ?: "Usuario"
                if (currentUserUid.isBlank()) {
                    flowOf(emptyList())
                } else {
                    observeUserProfileUseCase(currentUserUid).flatMapLatest { profile ->
                        householdId = profile?.householdId ?: ""
                        if (householdId.isBlank()) flowOf(emptyList())
                        else shoppingListUseCases.observeLists(householdId)
                    }
                }
            }.collect { lists ->
                _uiState.update { it.copy(availableLists = lists) }
            }
        }
    }

    fun onAddProductClicked(product: Product) {
        val lists = uiState.value.availableLists
        if (lists.size <= 1) {
            val targetList = lists.firstOrNull()
            val listId = targetList?.id ?: "main"
            val listName = targetList?.name ?: "Principal"
            addProductToMultipleLists(product, listOf(targetList ?: ShoppingListSummary("main", "Principal", ShoppingListType.MAIN,
                ListCoverTheme(), 0)))
        } else {
            _uiState.update { it.copy(targetProductForAdd = product) }
        }
    }

    fun addProductToMultipleLists(product: Product, selectedLists: List<ShoppingListSummary>) {
        viewModelScope.launch {
            if (householdId.isBlank() || selectedLists.isEmpty()) return@launch

            var successCount = 0
            for (targetList in selectedLists) {
                val newItem = CartItem(
                    itemId = "${product.id}_${System.currentTimeMillis()}",
                    upc = product.upc,
                    nombre = product.nombre,
                    brands = product.brands,
                    imageUrl = product.imageUrl,
                    categoria = product.categoria,
                    cantidad = 1,
                    addedBy = currentUserUid,
                    addedByAvatar = currentUserAvatar,
                    addedByDisplayName = currentUserDisplayName
                )

                shoppingListUseCases.addListItem(householdId, targetList.id, newItem).onSuccess {
                    successCount++
                }
            }

            if (successCount > 0) {
                val toastText = if (selectedLists.size == 1) {
                    "¡'${product.nombre}' agregado a ${selectedLists.first().name}!"
                } else {
                    "¡'${product.nombre}' agregado a ${selectedLists.size} listas!"
                }
                _uiState.update {
                    it.copy(
                        targetProductForAdd = null,
                        toastMessage = toastText
                    )
                }
            }
        }
    }

    fun dismissSelectListDialog() {
        _uiState.update { it.copy(targetProductForAdd = null) }
    }

    fun setListMode(mode: ListMode) {
        _uiState.update { it.copy(listMode = mode) }
        if (mode == ListMode.DISCOVER && _discoverProducts.value.isEmpty()) {
            loadProducts(reset = true)
        }
    }

    fun toggleCatalogExpanded() {
        _uiState.update { it.copy(isCatalogExpanded = !it.isCatalogExpanded) }
    }

    fun onSearchQueryChanged(query: String, isScan: Boolean = false) {
        _uiState.update {
            it.copy(
                searchQuery = query,
                isCatalogExpanded = if (query.isNotBlank()) true else it.isCatalogExpanded
            )
        }

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
        _uiState.update {
            it.copy(
                selectedCategory = category,
                isCatalogExpanded = if (category != "Todos") true else it.isCatalogExpanded
            )
        }
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

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }

    fun navigateToCart() {
        navigator.navigate(NavigationCommand.NavigateTo(CartRoute))
    }
}
