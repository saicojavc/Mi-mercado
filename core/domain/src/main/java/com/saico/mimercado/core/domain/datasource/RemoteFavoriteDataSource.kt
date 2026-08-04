package com.saico.mimercado.core.domain.datasource

import com.saico.mimercado.core.model.Product
import kotlinx.coroutines.flow.Flow

interface RemoteFavoriteDataSource {
    fun getFavorites(): Flow<List<Product>>
    suspend fun toggleFavorite(product: Product)
    fun isFavorite(productId: String): Flow<Boolean>
}
