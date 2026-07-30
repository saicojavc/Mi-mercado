package com.saico.mimercado.core.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.saico.mimercado.core.domain.repository.CartRepository
import com.saico.mimercado.core.model.CartItem
import com.saico.mimercado.core.model.Product
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreCartRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) : CartRepository {

    private val cartCollection = firestore.collection("households")
        .document("familia_valdes")
        .collection("cart")

    override fun getCartItems(): Flow<List<CartItem>> = callbackFlow {
        val listener = cartCollection.addSnapshotListener { snapshot, e ->
            if (e != null) {
                close(e)
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

    override suspend fun addToCart(product: Product, userId: String) {
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
                emoji = product.emoji,
                categoria = product.categoria,
                cantidad = 1,
                timestamp = System.currentTimeMillis(),
                addedBy = userId
            )
            cartCollection.document(newItemId).set(cartItem).await()
        }
    }

    override suspend fun incrementQuantity(itemId: String) {
        val ref = cartCollection.document(itemId)
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(ref)
            val currentQty = snapshot.getLong("cantidad") ?: 0L
            transaction.update(ref, "cantidad", currentQty + 1)
            transaction.update(ref, "timestamp", System.currentTimeMillis())
        }.await()
    }

    override suspend fun decrementQuantity(itemId: String) {
        val ref = cartCollection.document(itemId)
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
        cartCollection.document(itemId).delete().await()
    }

    override suspend fun clearCart() {
        val snapshot = cartCollection.get().await()
        if (snapshot.isEmpty) return
        
        val batch = firestore.batch()
        for (doc in snapshot.documents) {
            batch.delete(doc.reference)
        }
        batch.commit().await()
    }
}
