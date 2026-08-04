package com.saico.mimercado.core.domain.datasource

import com.saico.mimercado.core.model.Product
import com.saico.mimercado.core.model.ProductDetails

interface RemoteProductDataSource {
    suspend fun getProducts(
        query: String,
        page: Int
    ): Result<List<Product>>

    suspend fun getProductDetails(fdcId: String): Result<ProductDetails>
}
