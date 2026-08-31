package com.saico.mimercado.core.domain.usecase.cart

import com.saico.mimercado.core.domain.repository.CartRepository
import com.saico.mimercado.core.model.Product
import javax.inject.Inject

class AddToCartUseCase @Inject constructor(
    private val repository: CartRepository
) {
    suspend operator fun invoke(product: Product, userId: String) = 
        repository.addToCart(product, userId)
}
