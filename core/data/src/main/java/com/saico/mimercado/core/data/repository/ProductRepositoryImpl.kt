package com.saico.mimercado.core.data.repository

import com.saico.mimercado.core.common.CategoryMapper
import com.saico.mimercado.core.domain.datasource.RemoteProductDataSource
import com.saico.mimercado.core.domain.repository.ProductRepository
import com.saico.mimercado.core.model.Product
import com.saico.mimercado.core.model.ProductDetails
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteProductDataSource
) : ProductRepository {

    override suspend fun getProducts(
        category: String?,
        searchQuery: String?,
        store: String?,
        page: Int
    ): Result<List<Product>> {
        val isUpc = searchQuery != null && searchQuery.all { it.isDigit() } && searchQuery.length >= 8
        
        val baseQuery = when {
            !searchQuery.isNullOrBlank() -> searchQuery
            !category.isNullOrBlank() && category != "Todos" -> CategoryMapper.mapToEnglish(category)
            else -> "food"
        }
        
        val finalQuery = if (!store.isNullOrBlank() && !isUpc) "$baseQuery $store" else baseQuery
        
        return remoteDataSource.getProducts(finalQuery, page)
    }

    override suspend fun getProductDetails(fdcId: String): Result<ProductDetails> =
        remoteDataSource.getProductDetails(fdcId)
}
