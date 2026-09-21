package com.saico.mimercado.core.domain.usecase.products

import com.saico.mimercado.core.domain.repository.FavoriteRepository
import javax.inject.Inject

class DeleteCustomProductUseCase @Inject constructor(
    private val repository: FavoriteRepository
) {
    suspend operator fun invoke(productId: String) = repository.deleteCustomProduct(productId)
}
