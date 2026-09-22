package com.saico.mimercado.feature.lists

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saico.mimercado.core.domain.repository.AuthRepository
import com.saico.mimercado.core.domain.usecase.auth.ObserveUserProfileUseCase
import com.saico.mimercado.core.domain.usecase.household.ObserveHouseholdMembersUseCase
import com.saico.mimercado.core.domain.usecase.lists.ShoppingListUseCases
import com.saico.mimercado.core.domain.usecase.products.ProductsUseCases
import com.saico.mimercado.core.model.CartItem
import com.saico.mimercado.core.model.HouseholdMember
import com.saico.mimercado.core.model.Product
import com.saico.mimercado.core.model.ShoppingList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class ExplorationTab { FAVORITES, CATALOG }

val favoriteCategoryFilters = listOf(
    "Todos",
    "Lácteos",
    "Panadería",
    "Carnes",
    "Frutas y Verduras",
    "Despensa",
    "Bebidas",
    "Limpieza"
)

val catalogCategories = listOf(
    "Lácteos",
    "Panadería",
    "Carnes",
    "Frutas y Verduras",
    "Despensa",
    "Bebidas",
    "Limpieza"
)

data class ShoppingListDetailUiState(
    val list: ShoppingList? = null,
    val items: List<CartItem> = emptyList(),
    val members: List<HouseholdMember> = emptyList(),
    val favorites: List<Product> = emptyList(),
    val activeTab: ExplorationTab = ExplorationTab.FAVORITES,
    val selectedFavoriteCategory: String? = null,
    val selectedCatalogCategory: String? = null,
    val categoryProductsMap: Map<String, List<Product>> = emptyMap(),
    val isLoadingCategoryProducts: Boolean = false,
    val quickAddText: String = "",
    val toastMessage: String? = null,
    val currentUserUid: String = "",
    val currentUserDisplayName: String = "",
    val currentUserAvatar: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ShoppingListDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val authRepository: AuthRepository,
    private val observeUserProfileUseCase: ObserveUserProfileUseCase,
    private val observeHouseholdMembersUseCase: ObserveHouseholdMembersUseCase,
    private val shoppingListUseCases: ShoppingListUseCases,
    private val productsUseCases: ProductsUseCases
) : ViewModel() {

    private val listId: String = checkNotNull(savedStateHandle["listId"])

    private val _uiState = MutableStateFlow(ShoppingListDetailUiState(isLoading = true))
    val uiState: StateFlow<ShoppingListDetailUiState> = _uiState.asStateFlow()

    init {
        loadListDetails()
    }

    private fun loadListDetails() {
        viewModelScope.launch {
            authRepository.observeAuthState().flatMapLatest { authUser ->
                val uid = authUser?.uid ?: ""
                if (uid.isBlank()) {
                    flowOf(ShoppingListDetailUiState(isLoading = false, error = "Usuario no autenticado"))
                } else {
                    observeUserProfileUseCase(uid).flatMapLatest { userProfile ->
                        val householdId = userProfile?.householdId ?: ""
                        if (householdId.isBlank()) {
                            flowOf(ShoppingListDetailUiState(isLoading = false, error = "Sin familia asignada"))
                        } else {
                            combine(
                                shoppingListUseCases.observeList(householdId, listId),
                                shoppingListUseCases.observeListItems(householdId, listId),
                                observeHouseholdMembersUseCase(householdId),
                                productsUseCases.getFavorites()
                            ) { list, items, members, favorites ->
                                val currentMember = members.find { it.uid == uid }

                                ShoppingListDetailUiState(
                                    list = list,
                                    items = items,
                                    members = members,
                                    favorites = favorites,
                                    activeTab = _uiState.value.activeTab,
                                    selectedFavoriteCategory = _uiState.value.selectedFavoriteCategory,
                                    selectedCatalogCategory = _uiState.value.selectedCatalogCategory,
                                    categoryProductsMap = _uiState.value.categoryProductsMap,
                                    isLoadingCategoryProducts = _uiState.value.isLoadingCategoryProducts,
                                    quickAddText = _uiState.value.quickAddText,
                                    toastMessage = _uiState.value.toastMessage,
                                    currentUserUid = uid,
                                    currentUserDisplayName = currentMember?.displayName ?: authUser?.displayName ?: "Usuario",
                                    currentUserAvatar = currentMember?.avatarIcon,
                                    isLoading = false,
                                    error = null
                                )
                            }
                        }
                    }
                }
            }.collect { state ->
                _uiState.value = state
                val selectedCat = state.selectedCatalogCategory
                if (state.activeTab == ExplorationTab.CATALOG && selectedCat != null && !state.categoryProductsMap.containsKey(selectedCat)) {
                    loadCategoryProducts(selectedCat)
                }
            }
        }
    }

    fun selectExplorationTab(tab: ExplorationTab) {
        _uiState.update { it.copy(activeTab = tab) }
        val selectedCat = uiState.value.selectedCatalogCategory
        if (tab == ExplorationTab.CATALOG && selectedCat != null && !uiState.value.categoryProductsMap.containsKey(selectedCat)) {
            loadCategoryProducts(selectedCat)
        }
    }

    fun selectFavoriteCategory(category: String) {
        _uiState.update {
            val next = if (it.selectedFavoriteCategory == category) null else category
            it.copy(selectedFavoriteCategory = next)
        }
    }

    fun selectCatalogCategory(category: String) {
        val next = if (uiState.value.selectedCatalogCategory == category) null else category
        _uiState.update { it.copy(selectedCatalogCategory = next) }
        if (next != null && !uiState.value.categoryProductsMap.containsKey(next)) {
            loadCategoryProducts(next)
        }
    }

    fun toggleFavorite(product: Product) {
        viewModelScope.launch {
            productsUseCases.toggleFavorite(product)
        }
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }

    fun onQuickAddTextChanged(text: String) {
        _uiState.update { it.copy(quickAddText = text) }
    }

    fun confirmQuickAdd() {
        val text = uiState.value.quickAddText.trim()
        if (text.isBlank()) return

        val state = uiState.value
        val list = state.list ?: return

        val itemId = "${text.lowercase().replace(" ", "_")}_${System.currentTimeMillis()}"
        val newItem = CartItem(
            itemId = itemId,
            nombre = text,
            brands = "Agregado rápido",
            categoria = "Despensa",
            cantidad = 1,
            addedBy = state.currentUserUid,
            addedByAvatar = state.currentUserAvatar,
            addedByDisplayName = state.currentUserDisplayName
        )

        viewModelScope.launch {
            shoppingListUseCases.addListItem(list.householdId, list.id, newItem)
            _uiState.update { it.copy(quickAddText = "", toastMessage = "¡'${text}' agregado a la lista!") }
        }
    }

    fun addProductToList(product: Product) {
        val state = uiState.value
        val list = state.list ?: return

        val existing = state.items.find { it.itemId.startsWith(product.id) || it.nombre.equals(product.nombre, ignoreCase = true) }
        viewModelScope.launch {
            if (existing != null) {
                shoppingListUseCases.updateListItemQuantity(list.householdId, list.id, existing.itemId, existing.cantidad + 1)
            } else {
                val newItem = CartItem(
                    itemId = "${product.id}_${System.currentTimeMillis()}",
                    upc = product.upc,
                    nombre = product.nombre,
                    brands = product.brands,
                    imageUrl = product.imageUrl,
                    categoria = product.categoria,
                    cantidad = 1,
                    addedBy = state.currentUserUid,
                    addedByAvatar = state.currentUserAvatar,
                    addedByDisplayName = state.currentUserDisplayName
                )
                shoppingListUseCases.addListItem(list.householdId, list.id, newItem)
            }
            _uiState.update { it.copy(toastMessage = "¡'${product.nombre}' agregado a la lista!") }
        }
    }

    fun updateQuantity(itemId: String, newQuantity: Int) {
        val state = uiState.value
        val list = state.list ?: return
        viewModelScope.launch {
            shoppingListUseCases.updateListItemQuantity(list.householdId, list.id, itemId, newQuantity)
        }
    }

    fun removeItem(itemId: String) {
        val state = uiState.value
        val list = state.list ?: return
        viewModelScope.launch {
            shoppingListUseCases.removeListItem(list.householdId, list.id, itemId)
        }
    }

    fun renameList(newName: String) {
        val name = newName.trim()
        if (name.isBlank()) return
        val list = uiState.value.list ?: return
        viewModelScope.launch {
            shoppingListUseCases.renameList(list.householdId, list.id, name)
        }
    }

    fun deleteList(onDeleted: () -> Unit) {
        val list = uiState.value.list ?: return
        viewModelScope.launch {
            shoppingListUseCases.deleteList(list.householdId, list.id).onSuccess {
                onDeleted()
            }.onFailure { err ->
                _uiState.update { it.copy(error = err.message) }
            }
        }
    }

    private fun loadCategoryProducts(category: String) {
        _uiState.update { it.copy(isLoadingCategoryProducts = true) }
        viewModelScope.launch {
            val result = productsUseCases.getProducts(category = category, searchQuery = null, store = null, page = 1)
            result.onSuccess { products ->
                _uiState.update { state ->
                    val updatedMap = state.categoryProductsMap.toMutableMap()
                    updatedMap[category] = products
                    state.copy(categoryProductsMap = updatedMap, isLoadingCategoryProducts = false)
                }
            }.onFailure {
                _uiState.update { it.copy(isLoadingCategoryProducts = false) }
            }
        }
    }
}
