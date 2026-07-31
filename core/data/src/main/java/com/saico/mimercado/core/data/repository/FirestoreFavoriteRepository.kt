package com.saico.mimercado.core.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.saico.mimercado.core.common.UserProvider
import com.saico.mimercado.core.domain.repository.FavoriteRepository
import com.saico.mimercado.core.model.Product
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreFavoriteRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val userProvider: UserProvider
) : FavoriteRepository {

    // Match the path structure defined in Firestore rules
    private val favoritesCollection = firestore.collection("households")
        .document("familia_valdes")
        .collection("favorites")

    override fun getFavorites(): Flow<List<Product>> = callbackFlow {
        val subscription = favoritesCollection
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("FirestoreFavorites", "❌ Access Denied or Firestore Error: ${error.message}")
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val products = snapshot?.documents?.mapNotNull { doc ->
                    Product(
                        id = doc.id,
                        upc = doc.getString("upc") ?: "",
                        nombre = doc.getString("nombre") ?: "",
                        categoria = doc.getString("categoria") ?: "",
                        imageUrl = doc.getString("imageUrl") ?: "",
                        brands = doc.getString("brands") ?: "",
                        isFavorite = true
                    )
                } ?: emptyList()
                trySend(products)
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun toggleFavorite(product: Product) {
        try {
            val docRef = favoritesCollection.document(product.id)
            val snapshot = docRef.get().await()
            if (snapshot.exists()) {
                docRef.delete().await()
                Log.d("FirestoreFavorites", "🗑️ Removed ${product.nombre} from favorites")
            } else {
                val data = mapOf(
                    "upc" to product.upc,
                    "nombre" to product.nombre,
                    "categoria" to product.categoria,
                    "imageUrl" to product.imageUrl,
                    "brands" to product.brands
                )
                docRef.set(data).await()
                Log.d("FirestoreFavorites", "🧡 Added ${product.nombre} to favorites")
            }
        } catch (e: Exception) {
            Log.e("FirestoreFavorites", "❌ Failed to toggle favorite: ${e.message}", e)
        }
    }

    override fun isFavorite(productId: String): Flow<Boolean> = callbackFlow {
        val subscription = favoritesCollection.document(productId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(false)
                    return@addSnapshotListener
                }
                trySend(snapshot?.exists() ?: false)
            }
        awaitClose { subscription.remove() }
    }
}
