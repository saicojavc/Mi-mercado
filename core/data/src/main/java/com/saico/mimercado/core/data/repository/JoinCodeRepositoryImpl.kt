package com.saico.mimercado.core.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.saico.mimercado.core.domain.repository.JoinCodeRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JoinCodeRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : JoinCodeRepository {

    override suspend fun findHouseholdId(code: String): Result<String?> = try {
        val snapshot = firestore.collection("joinCodes").document(code).get().await()
        if (snapshot.exists()) {
            Result.success(snapshot.getString("householdId"))
        } else {
            Result.success(null)
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun registerCode(code: String, householdId: String): Result<Unit> = try {
        firestore.collection("joinCodes").document(code)
            .set(mapOf("householdId" to householdId))
            .await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun releaseCode(code: String): Result<Unit> = try {
        firestore.collection("joinCodes").document(code).delete().await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
