package com.saico.mimercado.core.domain.usecase.cart

import com.saico.mimercado.core.domain.repository.CartRepository
import javax.inject.Inject

class UpdateCartQuantityUseCase @Inject constructor(
    private val repository: CartRepository
) {
    suspend fun increment(itemId: String) = repository.incrementQuantity(itemId)
    suspend fun decrement(itemId: String) = repository.decrementQuantity(itemId)
}
