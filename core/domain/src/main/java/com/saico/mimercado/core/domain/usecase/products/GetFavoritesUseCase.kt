package com.saico.mimercado.core.domain.usecase.products

import com.saico.mimercado.core.domain.repository.FavoriteRepository
import com.saico.mimercado.core.model.Product
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFavoritesUseCase @Inject constructor(
    private val repository: FavoriteRepository
) {
    operator fun invoke(): Flow<List<Product>> = repository.getFavorites()
}
