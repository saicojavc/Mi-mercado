package com.saico.mimercado.core.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.saico.mimercado.core.domain.repository.UserRepository
import com.saico.mimercado.core.model.AuthUser
import com.saico.mimercado.core.model.MemberRole
import com.saico.mimercado.core.model.UserProfile
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : UserRepository {

    private fun generateRandomJoinCode(): String {
        val alphabet = "ABCDEFGHJKMNPQRSTUVWXYZ23456789" // Excluye 0, O, 1, I, L para legibilidad
        return (1..6).map { alphabet.random() }.joinToString("")
    }

    override suspend fun getOrCreateUserProfile(authUser: AuthUser): Result<UserProfile> = try {
        val userRef = firestore.collection("users").document(authUser.uid)
        
        // Ejecutamos una transacción atómica para garantizar la creación coherente del perfil + hogar en el primer login
        val resultProfile = firestore.runTransaction { transaction ->
            val snapshot = transaction.get(userRef)
            
            if (snapshot.exists()) {
                snapshot.toObject(UserProfile::class.java)!!
            } else {
                // Generar ID único para el hogar y su código corto de 6 caracteres
                val householdRef = firestore.collection("households").document()
                val householdId = householdRef.id
                
                val joinCode = generateRandomJoinCode()
                val codeRef = firestore.collection("joinCodes").document(joinCode)
                // Nota: En producción extrema se haría un bucle de verificación de colisión,
                // pero por probabilidad base y simplificación transaccional cliente-servidor escribimos directo.
                
                val newProfile = UserProfile(
                    uid = authUser.uid,
                    displayName = authUser.displayName,
                    email = authUser.email,
                    photoUrl = authUser.photoUrl,
                    householdId = householdId
                )
                
                // 1. Crear usuario
                transaction.set(userRef, newProfile)
                
                // 2. Crear hogar
                val householdData = mapOf(
                    "id" to householdId,
                    "name" to "Hogar de ${authUser.displayName}",
                    "ownerUid" to authUser.uid,
                    "joinCode" to joinCode,
                    "createdAt" to Timestamp.now()
                )
                transaction.set(householdRef, householdData)
                
                // 3. Agregar fundador como miembro ADULTO
                val memberRef = householdRef.collection("members").document(authUser.uid)
                val memberData = mapOf(
                    "uid" to authUser.uid,
                    "displayName" to authUser.displayName,
                    "photoUrl" to authUser.photoUrl,
                    "avatarIcon" to "fox",
                    "role" to MemberRole.ADULT.name,
                    "joinedAt" to Timestamp.now()
                )
                transaction.set(memberRef, memberData)
                
                // 4. Registrar índice inverso de código de acceso
                transaction.set(codeRef, mapOf("householdId" to householdId))
                
                newProfile
            }
        }.await()
        
        Result.success(resultProfile)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override fun observeUserProfile(uid: String): Flow<UserProfile?> = callbackFlow {
        val subscription = firestore.collection("users").document(uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.toObject(UserProfile::class.java))
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun updateHouseholdId(uid: String, householdId: String): Result<Unit> = try {
        val userRef = firestore.collection("users").document(uid)
        
        firestore.runTransaction { transaction ->
            val userSnapshot = transaction.get(userRef)
            val previousHouseholdId = userSnapshot.getString("householdId") ?: ""
            
            // 1. Actualizar el puntero del usuario
            transaction.update(userRef, "householdId", householdId)
            
            // 2. Insertarlo como miembro en el nuevo hogar familiar
            val displayName = userSnapshot.getString("displayName") ?: "Miembro"
            val photoUrl = userSnapshot.getString("photoUrl")
            val newMemberRef = firestore.collection("households").document(householdId)
                .collection("members").document(uid)
            
            val memberData = mapOf(
                "uid" to uid,
                "displayName" to displayName,
                "photoUrl" to photoUrl,
                "avatarIcon" to "fox",
                "role" to MemberRole.ADULT.name,
                "joinedAt" to Timestamp.now()
            )
            transaction.set(newMemberRef, memberData)
            
            // 3. Eliminarlo limpiamente del hogar familiar anterior si existía
            if (previousHouseholdId.isNotBlank() && previousHouseholdId != householdId) {
                val oldMemberRef = firestore.collection("households").document(previousHouseholdId)
                    .collection("members").document(uid)
                transaction.delete(oldMemberRef)
            }
        }.await()
        
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
