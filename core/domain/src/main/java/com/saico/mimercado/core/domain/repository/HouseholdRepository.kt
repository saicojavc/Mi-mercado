package com.saico.mimercado.core.domain.repository

import com.saico.mimercado.core.model.Household
import com.saico.mimercado.core.model.HouseholdMember
import com.saico.mimercado.core.model.MemberRole
import kotlinx.coroutines.flow.Flow

interface HouseholdRepository {
    suspend fun createHousehold(name: String, ownerUid: String): Result<String>
    suspend fun updateHouseholdName(householdId: String, name: String): Result<Unit>
    fun observeHousehold(householdId: String): Flow<Household>
    fun observeMembers(householdId: String): Flow<List<HouseholdMember>>
    suspend fun updateMemberRole(householdId: String, uid: String, role: MemberRole): Result<Unit>
    suspend fun updateMemberAvatar(householdId: String, uid: String, avatarIcon: String): Result<Unit>
    suspend fun regenerateJoinCode(householdId: String): Result<String>
}
