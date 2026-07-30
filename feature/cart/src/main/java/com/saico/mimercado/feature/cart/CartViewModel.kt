package com.saico.mimercado.feature.cart

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saico.mimercado.core.common.UserProvider
import com.saico.mimercado.core.domain.repository.CartRepository
import com.saico.mimercado.core.model.CartItem
import com.saico.mimercado.core.model.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "CartViewModel"

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val userProvider: UserProvider
) : ViewModel() {

    val cartItems: StateFlow<List<CartItem>> = cartRepository.getCartItems()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _errorMessages = MutableSharedFlow<String>()
    val errorMessages: SharedFlow<String> = _errorMessages.asSharedFlow()

    fun addToCart(product: Product) {
        val userId = userProvider.getUserId()
        viewModelScope.launch {
            try {
                cartRepository.addToCart(product, userId)
                _errorMessages.emit("${product.nombre} agregado al carrito")
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error adding to cart", e)
                _errorMessages.emit("Error al agregar: ${e.message}")
            }
        }
    }

    fun incrementQuantity(itemId: String) {
        viewModelScope.launch {
            try {
                cartRepository.incrementQuantity(itemId)
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error incrementing", e)
            }
        }
    }

    fun decrementQuantity(itemId: String) {
        viewModelScope.launch {
            try {
                cartRepository.decrementQuantity(itemId)
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error decrementing", e)
            }
        }
    }

    fun removeFromCart(cartItem: CartItem) {
        viewModelScope.launch {
            try {
                cartRepository.removeFromCart(cartItem.itemId)
                _errorMessages.emit("${cartItem.nombre} eliminado")
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error removing", e)
            }
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            try {
                cartRepository.clearCart()
                _errorMessages.emit("Carrito vaciado")
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error clearing", e)
            }
        }
    }
}
