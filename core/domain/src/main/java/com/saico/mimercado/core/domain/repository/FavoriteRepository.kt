package com.saico.mimercado.core.domain.repository

import com.saico.mimercado.core.model.Product
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    fun getFavorites(): Flow<List<Product>>
    suspend fun toggleFavorite(product: Product)
    fun isFavorite(productId: String): Flow<Boolean>
    suspend fun saveCustomProduct(product: Product)
    suspend fun getCustomProduct(productId: String): Result<Product?>
    suspend fun deleteCustomProduct(productId: String): Result<Unit>
}
