package com.saico.mimercado.core.domain.repository

import com.saico.mimercado.core.model.AuthUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun observeAuthState(): Flow<AuthUser?>
    suspend fun signInWithGoogle(idToken: String): Result<AuthUser>
    suspend fun signOut(): Result<Unit>
}
