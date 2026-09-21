package com.saico.mimercado.core.data.repository

import com.saico.mimercado.core.domain.datasource.RemoteFavoriteDataSource
import com.saico.mimercado.core.domain.repository.FavoriteRepository
import com.saico.mimercado.core.model.Product
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FavoriteRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteFavoriteDataSource
) : FavoriteRepository {

    override fun getFavorites(): Flow<List<Product>> = remoteDataSource.getFavorites()

    override suspend fun toggleFavorite(product: Product) = remoteDataSource.toggleFavorite(product)

    override fun isFavorite(productId: String): Flow<Boolean> = remoteDataSource.isFavorite(productId)

    override suspend fun saveCustomProduct(product: Product) = remoteDataSource.saveCustomProduct(product)

    override suspend fun getCustomProduct(productId: String) = remoteDataSource.getCustomProduct(productId)

    override suspend fun deleteCustomProduct(productId: String) = remoteDataSource.deleteCustomProduct(productId)
}
