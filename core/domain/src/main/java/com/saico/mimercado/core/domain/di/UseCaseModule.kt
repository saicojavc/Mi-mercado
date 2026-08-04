package com.saico.mimercado.core.domain.di

import com.saico.mimercado.core.domain.repository.FavoriteRepository
import com.saico.mimercado.core.domain.repository.ProductRepository
import com.saico.mimercado.core.domain.usecase.products.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideProductsUseCases(
        productRepository: ProductRepository,
        favoriteRepository: FavoriteRepository
    ): ProductsUseCases {
        return ProductsUseCases(
            getProducts = GetProductsUseCase(productRepository),
            getProductDetails = GetProductDetailsUseCase(productRepository),
            toggleFavorite = ToggleFavoriteUseCase(favoriteRepository),
            getFavorites = GetFavoritesUseCase(favoriteRepository),
            isFavorite = IsFavoriteUseCase(favoriteRepository)
        )
    }
}
