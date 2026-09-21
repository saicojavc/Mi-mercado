package com.saico.mimercado.core.domain.repository

import com.saico.mimercado.core.model.AuthUser
import com.saico.mimercado.core.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getOrCreateUserProfile(authUser: AuthUser): Result<UserProfile>
    fun observeUserProfile(uid: String): Flow<UserProfile?>
    suspend fun updateHouseholdId(uid: String, householdId: String): Result<Unit>
}
