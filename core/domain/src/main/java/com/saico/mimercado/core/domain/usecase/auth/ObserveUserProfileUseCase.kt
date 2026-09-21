package com.saico.mimercado.core.domain.usecase.auth

import com.saico.mimercado.core.domain.repository.UserRepository
import com.saico.mimercado.core.model.UserProfile
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveUserProfileUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(uid: String): Flow<UserProfile?> {
        return userRepository.observeUserProfile(uid)
    }
}
