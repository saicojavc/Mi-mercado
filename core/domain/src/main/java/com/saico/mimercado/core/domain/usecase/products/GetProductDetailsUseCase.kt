package com.saico.mimercado.core.domain.usecase.products

import com.saico.mimercado.core.domain.repository.ProductRepository
import com.saico.mimercado.core.model.ProductDetails
import javax.inject.Inject

class GetProductDetailsUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(fdcId: String): Result<ProductDetails> =
        repository.getProductDetails(fdcId)
}
