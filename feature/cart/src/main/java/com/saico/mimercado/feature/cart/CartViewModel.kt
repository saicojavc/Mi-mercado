package com.saico.mimercado.feature.cart

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saico.mimercado.core.common.UserProvider
import com.saico.mimercado.core.domain.usecase.cart.CartUseCases
import com.saico.mimercado.core.model.CartItem
import com.saico.mimercado.core.model.Product
import com.saico.mimercado.feature.cart.model.CartUiEvent
import com.saico.mimercado.feature.cart.model.CartUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "CartViewModel"

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartUseCases: CartUseCases,
    private val userProvider: UserProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<CartUiEvent>()
    val uiEvent: SharedFlow<CartUiEvent> = _uiEvent.asSharedFlow()

    val errorMessages: SharedFlow<String> = _uiEvent.mapNotNull { event ->
        if (event is CartUiEvent.ShowMessage) event.message else null
    }.shareIn(viewModelScope, SharingStarted.WhileSubscribed(5000))

    init {
        observeCart()
    }

    private fun observeCart() {
        cartUseCases.getCartItems()
            .onEach { items ->
                _uiState.update { it.copy(items = items, isLoading = false) }
            }
            .catch { e ->
                Log.e(TAG, "❌ Error observing cart", e)
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
            .launchIn(viewModelScope)
    }

    fun addToCart(product: Product) {
        val userId = userProvider.getUserId()
        viewModelScope.launch {
            try {
                cartUseCases.addToCart(product, userId)
                _uiEvent.emit(CartUiEvent.ShowMessage("${product.nombre} agregado al carrito"))
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error adding to cart", e)
                _uiEvent.emit(CartUiEvent.ShowMessage("Error al agregar: ${e.message}"))
            }
        }
    }

    fun incrementQuantity(itemId: String) {
        viewModelScope.launch {
            try {
                cartUseCases.updateQuantity.increment(itemId)
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error incrementing", e)
            }
        }
    }

    fun decrementQuantity(itemId: String) {
        viewModelScope.launch {
            try {
                cartUseCases.updateQuantity.decrement(itemId)
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error decrementing", e)
            }
        }
    }

    fun removeFromCart(cartItem: CartItem) {
        viewModelScope.launch {
            try {
                cartUseCases.removeFromCart(cartItem.itemId)
                _uiEvent.emit(CartUiEvent.ShowMessage("${cartItem.nombre} eliminado"))
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error removing", e)
            }
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            try {
                cartUseCases.clearCart()
                _uiEvent.emit(CartUiEvent.ShowMessage("Carrito vaciado"))
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error clearing", e)
            }
        }
    }
}
