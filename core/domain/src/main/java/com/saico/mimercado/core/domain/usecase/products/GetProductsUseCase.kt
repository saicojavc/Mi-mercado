package com.saico.mimercado.core.domain.usecase.products

import com.saico.mimercado.core.domain.repository.ProductRepository
import com.saico.mimercado.core.model.Product
import javax.inject.Inject

class GetProductsUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(
        category: String?,
        searchQuery: String?,
        store: String?,
        page: Int
    ): Result<List<Product>> = repository.getProducts(category, searchQuery, store, page)
}
