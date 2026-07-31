package com.saico.mimercado.core.domain.repository

import com.saico.mimercado.core.model.Product

interface ProductRepository {
    suspend fun getProducts(
        category: String?,
        searchQuery: String?,
        page: Int
    ): Result<List<Product>>
}
