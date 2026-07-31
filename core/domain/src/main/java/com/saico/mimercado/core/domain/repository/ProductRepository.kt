package com.saico.mimercado.core.domain.repository

import com.saico.mimercado.core.model.Product
import com.saico.mimercado.core.model.ProductDetails

interface ProductRepository {
    suspend fun getProducts(
        category: String?,
        searchQuery: String?,
        store: String?,
        page: Int
    ): Result<List<Product>>

    suspend fun getProductDetails(fdcId: String): Result<ProductDetails>
}
