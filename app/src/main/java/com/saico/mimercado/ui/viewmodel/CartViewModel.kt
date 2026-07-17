package com.saico.mimercado.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.saico.mimercado.model.CartItem
import com.saico.mimercado.model.Product
import com.saico.mimercado.util.SharedPreferencesUtil
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "CartViewModel"

class CartViewModel(application: Application) : AndroidViewModel(application) {
    private val firestore = FirebaseFirestore.getInstance()
    private val cartCollection = firestore.collection("households")
        .document("familia_valdes")
        .collection("cart")

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _errorMessages = MutableSharedFlow<String>()
    val errorMessages: SharedFlow<String> = _errorMessages.asSharedFlow()

    private var cartListener: ListenerRegistration? = null

    init {
        listenToCartChanges()
    }

    private fun listenToCartChanges() {
        Log.d(TAG, "✅ Initializing CartViewModel with Firestore listener")
        cartListener = cartCollection.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.e(TAG, "❌ Error listening to cart updates: ${e.message}", e)
                viewModelScope.launch {
                    _errorMessages.emit("Error de conexión: ${e.message}")
                }
                return@addSnapshotListener
            }

            if (snapshot != null) {
                Log.d(TAG, "✅ onEvent: Received snapshot with ${snapshot.size()} documents")
                val items = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(CartItem::class.java)?.apply {
                        itemId = doc.id
                    }
                }
                _cartItems.value = items
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        cartListener?.remove()
    }

    fun addToCart(product: Product) {
        val userId = SharedPreferencesUtil.getUserId(getApplication())
        Log.d(TAG, "🔍 addToCart START: ${product.nombre} by $userId")

        // Buscamos ítems del usuario actual que empiecen con el ID del producto
        val productIdPrefix = "${product.id}_"
        
        cartCollection
            .whereEqualTo("addedBy", userId)
            .get()
            .addOnSuccessListener { snapshot ->
                // Filtramos localmente por el prefijo del itemId (documentId)
                val existingDoc = snapshot.documents.find { it.id.startsWith(productIdPrefix) }
                
                if (existingDoc != null) {
                    Log.d(TAG, "✅ Product already exists for this user (matched by prefix), incrementing quantity")
                    incrementQuantity(existingDoc.id)
                    viewModelScope.launch {
                        _errorMessages.emit("${product.nombre} actualizado en el carrito")
                    }
                } else {
                    Log.d(TAG, "✅ New product for this user, creating unique itemId")
                    val newItemId = "${product.id}_${System.currentTimeMillis()}"
                    val cartItem = CartItem(
                        itemId = newItemId,
                        nombre = product.nombre,
                        emoji = product.emoji,
                        categoria = product.categoria,
                        cantidad = 1,
                        timestamp = System.currentTimeMillis(),
                        addedBy = userId
                    )
                    cartCollection.document(newItemId).set(cartItem)
                        .addOnSuccessListener {
                            Log.d(TAG, "✅ New CartItem created: $newItemId")
                            viewModelScope.launch {
                                _errorMessages.emit("${product.nombre} agregado al carrito")
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e(TAG, "❌ Error creating CartItem", e)
                            viewModelScope.launch {
                                _errorMessages.emit("Error al agregar al carrito: ${e.message}")
                            }
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "❌ Error checking existing product", e)
                viewModelScope.launch {
                    _errorMessages.emit("Error al buscar producto: ${e.message}")
                }
            }
    }

    fun incrementQuantity(itemId: String) {
        Log.d(TAG, "✅ incrementQuantity: Incrementing item $itemId")
        val ref = cartCollection.document(itemId)
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(ref)
            val currentQty = snapshot.getLong("cantidad") ?: 0L
            transaction.update(ref, "cantidad", currentQty + 1)
            transaction.update(ref, "timestamp", System.currentTimeMillis())
        }.addOnFailureListener { e ->
            Log.e(TAG, "❌ Error incrementing quantity", e)
        }
    }

    fun decrementQuantity(itemId: String) {
        Log.d(TAG, "✅ decrementQuantity: Decrementing item $itemId")
        val ref = cartCollection.document(itemId)
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(ref)
            val currentQty = snapshot.getLong("cantidad") ?: 1L
            if (currentQty > 1) {
                transaction.update(ref, "cantidad", currentQty - 1)
                transaction.update(ref, "timestamp", System.currentTimeMillis())
            } else {
                transaction.delete(ref)
            }
        }.addOnFailureListener { e ->
            Log.e(TAG, "❌ Error decrementing quantity", e)
        }
    }

    fun removeFromCart(cartItem: CartItem) {
        Log.d(TAG, "✅ removeFromCart: Removing item ${cartItem.nombre}")
        if (cartItem.itemId.isEmpty()) {
            Log.e(TAG, "❌ Cannot remove item: itemId is empty")
            return
        }
        cartCollection.document(cartItem.itemId).delete()
            .addOnSuccessListener {
                Log.d(TAG, "✅ Item removed from cart successfully")
                viewModelScope.launch {
                    _errorMessages.emit("${cartItem.nombre} eliminado del carrito")
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "❌ Error removing item from cart", e)
                viewModelScope.launch {
                    _errorMessages.emit("Error al eliminar del carrito: ${e.localizedMessage}")
                }
            }
    }

    fun clearCart() {
        Log.d(TAG, "✅ clearCart: Clearing entire cart")
        cartCollection.get().addOnSuccessListener { snapshot ->
            if (snapshot.isEmpty) return@addOnSuccessListener
            
            val batch = firestore.batch()
            for (doc in snapshot.documents) {
                batch.delete(doc.reference)
            }
            batch.commit().addOnSuccessListener {
                Log.d(TAG, "✅ Cart cleared successfully")
                viewModelScope.launch {
                    _errorMessages.emit("Carrito vaciado")
                }
            }.addOnFailureListener { e ->
                Log.e(TAG, "❌ Error clearing cart batch", e)
                viewModelScope.launch {
                    _errorMessages.emit("Error al vaciar el carrito: ${e.localizedMessage}")
                }
            }
        }.addOnFailureListener { e ->
            Log.e(TAG, "❌ Error fetching cart for clearing", e)
        }
    }
}
