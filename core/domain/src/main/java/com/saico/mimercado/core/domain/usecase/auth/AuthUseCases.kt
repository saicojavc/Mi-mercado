package com.saico.mimercado.core.domain.usecase.auth

import javax.inject.Inject

data class AuthUseCases @Inject constructor(
    val signInWithGoogle: SignInWithGoogleUseCase,
    val observeUserProfile: ObserveUserProfileUseCase
)
