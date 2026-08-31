package com.saico.mimercado.core.domain.usecase.cart

import javax.inject.Inject

data class CartUseCases @Inject constructor(
    val getCartItems: GetCartItemsUseCase,
    val addToCart: AddToCartUseCase,
    val updateQuantity: UpdateCartQuantityUseCase,
    val removeFromCart: RemoveFromCartUseCase,
    val clearCart: ClearCartUseCase
)
