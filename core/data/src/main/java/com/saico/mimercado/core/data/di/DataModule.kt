package com.saico.mimercado.core.data.di

import com.saico.mimercado.core.data.repository.FirestoreCartRepository
import com.saico.mimercado.core.data.repository.FirestoreFavoriteRepository
import com.saico.mimercado.core.data.repository.NetworkProductRepository
import com.saico.mimercado.core.domain.repository.CartRepository
import com.saico.mimercado.core.domain.repository.FavoriteRepository
import com.saico.mimercado.core.domain.repository.ProductRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindCartRepository(
        firestoreCartRepository: FirestoreCartRepository
    ): CartRepository

    @Binds
    @Singleton
    abstract fun bindProductRepository(
        networkProductRepository: NetworkProductRepository
    ): ProductRepository

    @Binds
    @Singleton
    abstract fun bindFavoriteRepository(
        firestoreFavoriteRepository: FirestoreFavoriteRepository
    ): FavoriteRepository
}
