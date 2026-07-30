package com.saico.mimercado.core.domain.repository

import com.saico.mimercado.core.model.CartItem
import com.saico.mimercado.core.model.Product
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    fun getCartItems(): Flow<List<CartItem>>
    suspend fun addToCart(product: Product, userId: String)
    suspend fun incrementQuantity(itemId: String)
    suspend fun decrementQuantity(itemId: String)
    suspend fun removeFromCart(itemId: String)
    suspend fun clearCart()
}
