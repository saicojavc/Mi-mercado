package com.saico.mimercado.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.saico.mimercado.model.CartItem
import com.saico.mimercado.model.Product
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val HOUSEHOLD_ID = "familia_valdes"
private const val TAG = "CartViewModel"

class CartViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val cartCollection = db.collection("households").document(HOUSEHOLD_ID).collection("cart")

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _errorMessages = MutableSharedFlow<String>()
    val errorMessages: SharedFlow<String> = _errorMessages.asSharedFlow()

    init {
        cartCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e(TAG, "Error listening to cart updates", error)
                viewModelScope.launch {
                    _errorMessages.emit("Error de conexión: ${error.localizedMessage}. Verifica las reglas de Firestore.")
                }
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val items = snapshot.toObjects(CartItem::class.java)
                _cartItems.value = items
            }
        }
    }

    fun addToCart(product: Product) {
        viewModelScope.launch {
            val docRef = cartCollection.document(product.id)
            db.runTransaction { transaction ->
                val snapshot = transaction.get(docRef)
                if (snapshot.exists()) {
                    val currentCantidad = snapshot.getLong("cantidad") ?: 0
                    transaction.update(docRef, "cantidad", currentCantidad + 1)
                    transaction.update(docRef, "timestamp", System.currentTimeMillis())
                } else {
                    val newItem = CartItem(
                        productId = product.id,
                        nombre = product.nombre,
                        imagenUrl = product.imagenUrl,
                        categoria = product.categoria,
                        cantidad = 1,
                        timestamp = System.currentTimeMillis()
                    )
                    transaction.set(docRef, newItem)
                }
            }.addOnSuccessListener {
                Log.d(TAG, "Product added to cart successfully")
            }.addOnFailureListener { e ->
                Log.e(TAG, "Error adding product to cart", e)
                viewModelScope.launch {
                    _errorMessages.emit("Error al agregar al carrito: ${e.localizedMessage}")
                }
            }
        }
    }

    fun removeFromCart(cartItem: CartItem) {
        viewModelScope.launch {
            cartCollection.document(cartItem.productId).delete()
                .addOnSuccessListener {
                    Log.d(TAG, "Item removed from cart successfully")
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error removing item from cart", e)
                    viewModelScope.launch {
                        _errorMessages.emit("Error al eliminar del carrito: ${e.localizedMessage}")
                    }
                }
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            cartCollection.get()
                .addOnSuccessListener { snapshot ->
                    val batch = db.batch()
                    snapshot.documents.forEach { doc ->
                        batch.delete(doc.reference)
                    }
                    batch.commit()
                        .addOnSuccessListener {
                            Log.d(TAG, "Cart cleared successfully")
                        }
                        .addOnFailureListener { e ->
                            Log.e(TAG, "Error committing clear cart batch", e)
                            viewModelScope.launch {
                                _errorMessages.emit("Error al vaciar el carrito: ${e.localizedMessage}")
                            }
                        }
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error getting cart for clearing", e)
                    viewModelScope.launch {
                        _errorMessages.emit("Error al obtener el carrito: ${e.localizedMessage}")
                    }
                }
        }
    }
}

