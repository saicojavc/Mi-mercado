package com.saico.mimercado.core.domain.usecase.products

import com.saico.mimercado.core.domain.repository.FavoriteRepository
import com.saico.mimercado.core.model.Product
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val repository: FavoriteRepository
) {
    suspend operator fun invoke(product: Product) = repository.toggleFavorite(product)
}
