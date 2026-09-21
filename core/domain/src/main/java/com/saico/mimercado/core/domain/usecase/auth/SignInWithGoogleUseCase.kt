package com.saico.mimercado.core.domain.usecase.auth

import com.saico.mimercado.core.domain.repository.AuthRepository
import com.saico.mimercado.core.domain.repository.UserRepository
import com.saico.mimercado.core.model.UserProfile
import javax.inject.Inject

class SignInWithGoogleUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(idToken: String): Result<UserProfile> {
        return authRepository.signInWithGoogle(idToken).fold(
            onSuccess = { authUser ->
                userRepository.getOrCreateUserProfile(authUser)
            },
            onFailure = { error ->
                Result.failure(error)
            }
        )
    }
}
