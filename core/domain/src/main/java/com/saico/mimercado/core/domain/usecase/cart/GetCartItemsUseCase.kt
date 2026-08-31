package com.saico.mimercado.core.domain.usecase.cart

import com.saico.mimercado.core.domain.repository.CartRepository
import com.saico.mimercado.core.model.CartItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCartItemsUseCase @Inject constructor(
    private val repository: CartRepository
) {
    operator fun invoke(): Flow<List<CartItem>> = repository.getCartItems()
}
