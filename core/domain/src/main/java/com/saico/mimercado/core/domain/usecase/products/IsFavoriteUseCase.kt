package com.saico.mimercado.core.domain.usecase.products

import com.saico.mimercado.core.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class IsFavoriteUseCase @Inject constructor(
    private val repository: FavoriteRepository
) {
    operator fun invoke(productId: String): Flow<Boolean> = repository.isFavorite(productId)
}
