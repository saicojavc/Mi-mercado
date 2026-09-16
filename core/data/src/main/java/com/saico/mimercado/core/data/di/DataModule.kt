package com.saico.mimercado.core.data.di

import com.saico.mimercado.core.data.repository.AuthRepositoryImpl
import com.saico.mimercado.core.data.repository.FavoriteRepositoryImpl
import com.saico.mimercado.core.data.repository.FirestoreCartRepository
import com.saico.mimercado.core.data.repository.HouseholdRepositoryImpl
import com.saico.mimercado.core.data.repository.ImageSearchRepositoryImpl
import com.saico.mimercado.core.data.repository.ProductRepositoryImpl
import com.saico.mimercado.core.data.repository.UserRepositoryImpl
import com.saico.mimercado.core.data.repository.JoinCodeRepositoryImpl
import com.saico.mimercado.core.domain.repository.*
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
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindHouseholdRepository(
        householdRepositoryImpl: HouseholdRepositoryImpl
    ): HouseholdRepository

    @Binds
    @Singleton
    abstract fun bindJoinCodeRepository(
        joinCodeRepositoryImpl: JoinCodeRepositoryImpl
    ): JoinCodeRepository

    @Binds
    @Singleton
    abstract fun bindImageSearchRepository(
        imageSearchRepositoryImpl: ImageSearchRepositoryImpl
    ): ImageSearchRepository

    @Binds
    @Singleton
    abstract fun bindCartRepository(
        firestoreCartRepository: FirestoreCartRepository
    ): CartRepository

    @Binds
    @Singleton
    abstract fun bindProductRepository(
        productRepositoryImpl: ProductRepositoryImpl
    ): ProductRepository

    @Binds
    @Singleton
    abstract fun bindFavoriteRepository(
        favoriteRepositoryImpl: FavoriteRepositoryImpl
    ): FavoriteRepository
}
