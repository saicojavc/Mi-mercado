package com.saico.mimercado.core.domain.usecase.cart

import com.saico.mimercado.core.domain.repository.CartRepository
import javax.inject.Inject

class RemoveFromCartUseCase @Inject constructor(
    private val repository: CartRepository
) {
    suspend operator fun invoke(itemId: String) = repository.removeFromCart(itemId)
}
