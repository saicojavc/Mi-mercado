package com.saico.mimercado.core.domain.di

import com.saico.mimercado.core.domain.repository.AuthRepository
import com.saico.mimercado.core.domain.repository.FavoriteRepository
import com.saico.mimercado.core.domain.repository.HouseholdRepository
import com.saico.mimercado.core.domain.repository.ImageSearchRepository
import com.saico.mimercado.core.domain.repository.ProductRepository
import com.saico.mimercado.core.domain.repository.UserRepository
import com.saico.mimercado.core.domain.repository.InvitationRepository
import com.saico.mimercado.core.domain.usecase.auth.*
import com.saico.mimercado.core.domain.usecase.household.*
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
        favoriteRepository: FavoriteRepository,
        imageSearchRepository: ImageSearchRepository
    ): ProductsUseCases {
        return ProductsUseCases(
            getProducts = GetProductsUseCase(productRepository),
            getProductDetails = GetProductDetailsUseCase(productRepository),
            toggleFavorite = ToggleFavoriteUseCase(favoriteRepository),
            getFavorites = GetFavoritesUseCase(favoriteRepository),
            isFavorite = IsFavoriteUseCase(favoriteRepository),
            createCustomProduct = CreateCustomProductUseCase(favoriteRepository),
            searchProductImages = SearchProductImagesUseCase(imageSearchRepository)
        )
    }

    @Provides
    @Singleton
    fun provideAuthUseCases(
        authRepository: AuthRepository,
        userRepository: UserRepository
    ): AuthUseCases {
        return AuthUseCases(
            signInWithGoogle = SignInWithGoogleUseCase(authRepository, userRepository),
            observeUserProfile = ObserveUserProfileUseCase(userRepository)
        )
    }

    @Provides
    @Singleton
    fun provideHouseholdUseCases(
        householdRepository: HouseholdRepository,
        userRepository: UserRepository,
        invitationRepository: InvitationRepository
    ): HouseholdUseCases {
        return HouseholdUseCases(
            createHousehold = CreateHouseholdUseCase(householdRepository, userRepository),
            observeMembers = ObserveHouseholdMembersUseCase(householdRepository),
            updateMemberRole = UpdateMemberRoleUseCase(householdRepository),
            updateMemberAvatar = UpdateMemberAvatarUseCase(householdRepository),
            sendInvitation = SendHouseholdInvitationUseCase(invitationRepository),
            observePendingInvitations = ObservePendingInvitationsUseCase(invitationRepository),
            acceptInvitation = AcceptHouseholdInvitationUseCase(invitationRepository, userRepository)
        )
    }
}
