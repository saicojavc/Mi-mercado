package com.saico.mimercado.feature.products

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.saico.mimercado.core.common.ImageCacheManager
import com.saico.mimercado.core.domain.repository.AuthRepository
import com.saico.mimercado.core.domain.usecase.auth.ObserveUserProfileUseCase
import com.saico.mimercado.core.domain.usecase.lists.ShoppingListUseCases
import com.saico.mimercado.core.domain.usecase.products.ProductsUseCases
import com.saico.mimercado.core.model.CartItem
import com.saico.mimercado.core.model.ListCoverTheme
import com.saico.mimercado.core.model.Product
import com.saico.mimercado.core.model.ProductDetails
import com.saico.mimercado.core.model.ShoppingListSummary
import com.saico.mimercado.core.model.ShoppingListType
import com.saico.mimercado.core.ui.navigation.NavigationCommand
import com.saico.mimercado.core.ui.navigation.Navigator
import com.saico.mimercado.core.ui.navigation.routes.products.ProductDetailsRoute
import com.saico.mimercado.feature.products.model.ProductDetailsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val useCases: ProductsUseCases,
    private val authRepository: AuthRepository,
    private val observeUserProfileUseCase: ObserveUserProfileUseCase,
    private val shoppingListUseCases: ShoppingListUseCases,
    val imageCache: ImageCacheManager,
    private val navigator: Navigator
) : ViewModel() {

    private val route: ProductDetailsRoute = savedStateHandle.toRoute()
    private val fdcId = route.fdcId

    private val _uiState = MutableStateFlow<ProductDetailsUiState>(ProductDetailsUiState.Loading)
    val uiState: StateFlow<ProductDetailsUiState> = _uiState.asStateFlow()

    val availableLists = MutableStateFlow<List<ShoppingListSummary>>(emptyList())
    val targetProductForAdd = MutableStateFlow<Product?>(null)
    val toastMessage = MutableStateFlow<String?>(null)

    private var householdId: String = ""
    private var currentUserUid: String = ""
    private var currentUserDisplayName: String = ""
    private var currentUserAvatar: String? = null

    val isFavorite = useCases.isFavorite(fdcId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    init {
        loadDetails()
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
                availableLists.value = lists
            }
        }
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

    fun onAddProductClicked(details: ProductDetails) {
        val product = Product(
            id = details.id,
            upc = details.upc,
            nombre = details.name,
            categoria = details.category,
            imageUrl = details.imageUrl,
            brands = details.brand
        )
        val lists = availableLists.value
        if (lists.size <= 1) {
            val targetList = lists.firstOrNull()
            val listName = targetList?.name ?: "Principal"
            addProductToMultipleLists(product, listOf(targetList ?: ShoppingListSummary("main", listName, ShoppingListType.MAIN,
                ListCoverTheme(), 0)))
        } else {
            targetProductForAdd.value = product
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
                targetProductForAdd.value = null
                toastMessage.value = toastText
            }
        }
    }

    fun dismissSelectListDialog() {
        targetProductForAdd.value = null
    }

    fun clearToast() {
        toastMessage.value = null
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
