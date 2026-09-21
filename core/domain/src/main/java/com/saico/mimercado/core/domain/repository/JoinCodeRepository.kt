package com.saico.mimercado.core.domain.repository

interface JoinCodeRepository {
    suspend fun findHouseholdId(code: String): Result<String?>
    suspend fun registerCode(code: String, householdId: String): Result<Unit>
    suspend fun releaseCode(code: String): Result<Unit>
}
