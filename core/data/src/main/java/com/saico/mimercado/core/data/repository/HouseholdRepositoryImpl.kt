package com.saico.mimercado.core.data.repository

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.saico.mimercado.core.domain.repository.HouseholdRepository
import com.saico.mimercado.core.model.Household
import com.saico.mimercado.core.model.HouseholdMember
import com.saico.mimercado.core.model.MemberRole
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HouseholdRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : HouseholdRepository {

    private fun generateRandomJoinCode(): String {
        val alphabet = "ABCDEFGHJKMNPQRSTUVWXYZ23456789"
        return (1..6).map { alphabet.random() }.joinToString("")
    }

    override suspend fun createHousehold(name: String, ownerUid: String): Result<String> = try {
        val householdRef = firestore.collection("households").document()
        val householdId = householdRef.id
        val joinCode = generateRandomJoinCode()

        val householdData = mapOf(
            "id" to householdId,
            "name" to name,
            "ownerUid" to ownerUid,
            "joinCode" to joinCode,
            "createdAt" to Timestamp.now()
        )
        householdRef.set(householdData).await()

        val userSnapshot = firestore.collection("users").document(ownerUid).get().await()
        val displayName = userSnapshot.getString("displayName") ?: "Propietario"
        val photoUrl = userSnapshot.getString("photoUrl")

        val memberData = mapOf(
            "uid" to ownerUid,
            "displayName" to displayName,
            "photoUrl" to photoUrl,
            "role" to MemberRole.ADULT.name,
            "joinedAt" to Timestamp.now()
        )
        householdRef.collection("members").document(ownerUid).set(memberData).await()
        firestore.collection("joinCodes").document(joinCode).set(mapOf("householdId" to householdId)).await()

        Result.success(householdId)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override fun observeHousehold(householdId: String): Flow<Household> = callbackFlow {
        val subscription = firestore.collection("households").document(householdId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Emitimos un objeto con ID vacío para indicar que no se pudo cargar (ej: por permisos)
                    trySend(Household(id = "", name = "Error de acceso", ownerUid = "", joinCode = ""))
                    return@addSnapshotListener
                }
                if (snapshot == null || !snapshot.exists()) {
                    // Si el hogar fue eliminado o no existe, emitimos un objeto marcador vacío
                    trySend(Household(id = "", name = "", ownerUid = "", joinCode = ""))
                    return@addSnapshotListener
                }
                val household = Household(
                    id = snapshot.id,
                    name = snapshot.getString("name") ?: "Hogar",
                    ownerUid = snapshot.getString("ownerUid") ?: "",
                    joinCode = snapshot.getString("joinCode") ?: ""
                )
                trySend(household)
            }
        awaitClose { subscription.remove() }
    }

    override fun observeMembers(householdId: String): Flow<List<HouseholdMember>> = callbackFlow {
        val subscription = firestore.collection("households")
            .document(householdId)
            .collection("members")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Si falla la lectura de miembros (ej: por permisos), emitimos lista vacía para no bloquear la UI
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val members = snapshot?.documents?.mapNotNull { doc ->
                    val roleStr = doc.getString("role") ?: "ADULT"
                    HouseholdMember(
                        uid = doc.id,
                        displayName = doc.getString("displayName") ?: "Miembro",
                        avatarIcon = doc.getString("avatarIcon"),
                        photoUrl = doc.getString("photoUrl"),
                        role = if (roleStr == "CHILD") MemberRole.CHILD else MemberRole.ADULT
                    )
                } ?: emptyList()
                trySend(members)
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun updateMemberRole(householdId: String, uid: String, role: MemberRole): Result<Unit> = try {
        firestore.collection("households")
            .document(householdId)
            .collection("members")
            .document(uid)
            .update("role", role.name)
            .await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun updateMemberAvatar(householdId: String, uid: String, avatarIcon: String): Result<Unit> = try {
        firestore.collection("households")
            .document(householdId)
            .collection("members")
            .document(uid)
            .update("avatarIcon", avatarIcon)
            .await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun regenerateJoinCode(householdId: String): Result<String> = try {
        val newCode = generateRandomJoinCode()
        val householdRef = firestore.collection("households").document(householdId)
        
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(householdRef)
            val oldCode = snapshot.getString("joinCode") ?: ""
            
            // 1. Actualizar el código en el hogar
            transaction.update(householdRef, "joinCode", newCode)
            
            // 2. Dar de alta el nuevo código raíz
            transaction.set(firestore.collection("joinCodes").document(newCode), mapOf("householdId" to householdId))
            
            // 3. Remover el anterior código huérfano viejo
            if (oldCode.isNotBlank()) {
                transaction.delete(firestore.collection("joinCodes").document(oldCode))
            }
        }.await()
        
        Result.success(newCode)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
