package com.saico.mimercado.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.database.ValueEventListener
import com.saico.mimercado.model.CartItem
import com.saico.mimercado.model.Product
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "CartViewModel"

class CartViewModel : ViewModel() {
    private val database = FirebaseDatabase.getInstance("https://when-babe-default-rtdb.firebaseio.com/")
    private val cartRef = database.getReference("households/familia_valdes/cart")

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _errorMessages = MutableSharedFlow<String>()
    val errorMessages: SharedFlow<String> = _errorMessages.asSharedFlow()

    init {
        Log.d(TAG, "Initializing CartViewModel with URL: https://when-babe-default-rtdb.firebaseio.com/")
        cartRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d(TAG, "onDataChange: Received snapshot with ${snapshot.childrenCount} children")
                val items = snapshot.children.mapNotNull { 
                    val item = it.getValue(CartItem::class.java)
                    Log.d(TAG, "Parsed item: $item")
                    item
                }
                _cartItems.value = items
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Error listening to cart updates: ${error.message}", error.toException())
                viewModelScope.launch {
                    _errorMessages.emit("Error de conexión: ${error.message}")
                }
            }
        })
    }

    fun addToCart(product: Product) {
        Log.d(TAG, "addToCart: Attempting to add product ${product.nombre} (${product.id})")
        cartRef.child(product.id).runTransaction(object : Transaction.Handler {
            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                Log.d(TAG, "doTransaction: Current data is ${mutableData.value}")
                var cartItem = mutableData.getValue(CartItem::class.java)
                if (cartItem == null) {
                    Log.d(TAG, "doTransaction: Creating new cart item")
                    cartItem = CartItem(
                        productId = product.id,
                        nombre = product.nombre,
                        emoji = product.emoji,
                        categoria = product.categoria,
                        cantidad = 1,
                        timestamp = System.currentTimeMillis()
                    )
                } else {
                    Log.d(TAG, "doTransaction: Incrementing existing item quantity")
                    cartItem = cartItem.copy(
                        cantidad = cartItem.cantidad + 1,
                        timestamp = System.currentTimeMillis()
                    )
                }
                mutableData.setValue(cartItem)
                return Transaction.success(mutableData)
            }

            override fun onComplete(error: DatabaseError?, committed: Boolean, snapshot: DataSnapshot?) {
                viewModelScope.launch {
                    if (error != null) {
                        Log.e(TAG, "Error adding product to cart", error.toException())
                        _errorMessages.emit("Error al agregar al carrito: ${error.message}")
                    } else if (!committed) {
                        Log.w(TAG, "Transaction not committed")
                        _errorMessages.emit("La operación no se pudo completar. Inténtalo de nuevo.")
                    } else {
                        Log.d(TAG, "Product added to cart successfully")
                        _errorMessages.emit("${product.nombre} agregado al carrito")
                    }
                }
            }
        })
    }

    fun removeFromCart(cartItem: CartItem) {
        Log.d(TAG, "removeFromCart: Attempting to remove item ${cartItem.nombre}")
        cartRef.child(cartItem.productId).removeValue()
            .addOnSuccessListener {
                Log.d(TAG, "Item removed from cart successfully")
                viewModelScope.launch {
                    _errorMessages.emit("${cartItem.nombre} eliminado del carrito")
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error removing item from cart", e)
                viewModelScope.launch {
                    _errorMessages.emit("Error al eliminar del carrito: ${e.localizedMessage}")
                }
            }
    }

    fun clearCart() {
        Log.d(TAG, "clearCart: Attempting to clear entire cart")
        cartRef.removeValue()
            .addOnSuccessListener {
                Log.d(TAG, "Cart cleared successfully")
                viewModelScope.launch {
                    _errorMessages.emit("Carrito vaciado")
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error clearing cart", e)
                viewModelScope.launch {
                    _errorMessages.emit("Error al vaciar el carrito: ${e.localizedMessage}")
                }
            }
    }
}
