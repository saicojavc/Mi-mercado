package com.saico.mimercado.core.domain.usecase.products

import javax.inject.Inject

data class ProductsUseCases @Inject constructor(
    val getProducts: GetProductsUseCase,
    val getProductDetails: GetProductDetailsUseCase,
    val toggleFavorite: ToggleFavoriteUseCase,
    val getFavorites: GetFavoritesUseCase,
    val isFavorite: IsFavoriteUseCase
)
