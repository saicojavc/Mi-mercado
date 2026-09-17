package com.saico.mimercado.core.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.saico.mimercado.core.domain.repository.CartRepository
import com.saico.mimercado.core.model.CartItem
import com.saico.mimercado.core.model.Product
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreCartRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) : CartRepository {

    private fun currentHouseholdIdFlow(): Flow<String> = callbackFlow {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            trySend("familia_valdes")
            close()
            return@callbackFlow
        }
        val subscription = firestore.collection("users").document(uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend("familia_valdes")
                    return@addSnapshotListener
                }
                val householdId = snapshot?.getString("householdId") ?: "familia_valdes"
                trySend(householdId)
            }
        awaitClose { subscription.remove() }
    }

    private suspend fun getHouseholdId(): String {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return "familia_valdes"
        return try {
            val snapshot = firestore.collection("users").document(uid).get().await()
            snapshot.getString("householdId") ?: "familia_valdes"
        } catch (e: Exception) {
            "familia_valdes"
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getCartItems(): Flow<List<CartItem>> = currentHouseholdIdFlow().flatMapLatest { householdId ->
        callbackFlow {
            val listener = firestore.collection("households")
                .document(householdId)
                .collection("cart")
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        trySend(emptyList()) // Evitar que el flujo se cuelgue ante errores de permisos
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val items = snapshot.documents.mapNotNull { doc ->
                            doc.toObject(CartItem::class.java)?.apply {
                                itemId = doc.id
                            }
                        }
                        trySend(items)
                    }
                }
            awaitClose { listener.remove() }
        }
    }

    override suspend fun addToCart(product: Product, userId: String) {
        val householdId = getHouseholdId()
        val cartCollection = firestore.collection("households")
            .document(householdId)
            .collection("cart")

        // Obtener info del miembro actual para persistirla en el item del carrito
        var memberAvatar = "fox"
        var memberName = "Miembro"
        
        try {
            val memberSnapshot = firestore.collection("households")
                .document(householdId)
                .collection("members")
                .document(userId)
                .get()
                .await()

            if (memberSnapshot.exists()) {
                memberAvatar = memberSnapshot.getString("avatarIcon") ?: "fox"
                memberName = memberSnapshot.getString("displayName") ?: "Miembro"
            }
        } catch (e: Exception) {
            // Fallback silencioso a valores por defecto si falla la lectura del miembro
        }

        val productIdPrefix = "${product.id}_"
        val snapshot = cartCollection
            .whereEqualTo("addedBy", userId)
            .get()
            .await()

        val existingDoc = snapshot.documents.find { it.id.startsWith(productIdPrefix) }

        if (existingDoc != null) {
            incrementQuantity(existingDoc.id)
        } else {
            val newItemId = "${product.id}_${System.currentTimeMillis()}"
            val cartItem = CartItem(
                itemId = newItemId,
                nombre = product.nombre,
                brands = product.brands,
                imageUrl = product.imageUrl,
                categoria = product.categoria,
                cantidad = 1,
                timestamp = System.currentTimeMillis(),
                addedBy = userId,
                addedByAvatar = memberAvatar,
                addedByDisplayName = memberName
            )
            cartCollection.document(newItemId).set(cartItem).await()
        }
    }

    override suspend fun incrementQuantity(itemId: String) {
        val householdId = getHouseholdId()
        val ref = firestore.collection("households")
            .document(householdId)
            .collection("cart")
            .document(itemId)

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(ref)
            val currentQty = snapshot.getLong("cantidad") ?: 0L
            transaction.update(ref, "cantidad", currentQty + 1)
            transaction.update(ref, "timestamp", System.currentTimeMillis())
        }.await()
    }

    override suspend fun decrementQuantity(itemId: String) {
        val householdId = getHouseholdId()
        val ref = firestore.collection("households")
            .document(householdId)
            .collection("cart")
            .document(itemId)

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(ref)
            val currentQty = snapshot.getLong("cantidad") ?: 1L
            if (currentQty > 1) {
                transaction.update(ref, "cantidad", currentQty - 1)
                transaction.update(ref, "timestamp", System.currentTimeMillis())
            } else {
                transaction.delete(ref)
            }
        }.await()
    }

    override suspend fun removeFromCart(itemId: String) {
        val householdId = getHouseholdId()
        firestore.collection("households")
            .document(householdId)
            .collection("cart")
            .document(itemId)
            .delete()
            .await()
    }

    override suspend fun clearCart() {
        val householdId = getHouseholdId()
        val cartCollection = firestore.collection("households")
            .document(householdId)
            .collection("cart")

        val snapshot = cartCollection.get().await()
        if (snapshot.isEmpty) return
        
        val batch = firestore.batch()
        for (doc in snapshot.documents) {
            batch.delete(doc.reference)
        }
        batch.commit().await()
    }
}
