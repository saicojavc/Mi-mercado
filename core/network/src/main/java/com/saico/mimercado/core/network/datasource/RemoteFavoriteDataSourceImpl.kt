package com.saico.mimercado.core.network.datasource

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.saico.mimercado.core.domain.datasource.RemoteFavoriteDataSource
import com.saico.mimercado.core.model.Product
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteFavoriteDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : RemoteFavoriteDataSource {

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
    override fun getFavorites(): Flow<List<Product>> = currentHouseholdIdFlow().flatMapLatest { householdId ->
        callbackFlow {
            val subscription = firestore.collection("households")
                .document(householdId)
                .collection("favorites")
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
                            isFavorite = true,
                            isCustom = doc.getBoolean("isCustom") ?: false
                        )
                    } ?: emptyList()
                    trySend(products)
                }
            awaitClose { subscription.remove() }
        }
    }

    override suspend fun toggleFavorite(product: Product) {
        try {
            val householdId = getHouseholdId()
            val docRef = firestore.collection("households")
                .document(householdId)
                .collection("favorites")
                .document(product.id)

            val snapshot = docRef.get().await()
            if (snapshot.exists()) {
                docRef.delete().await()
            } else {
                val data = mapOf(
                    "upc" to product.upc,
                    "nombre" to product.nombre,
                    "categoria" to product.categoria,
                    "imageUrl" to product.imageUrl,
                    "brands" to product.brands
                )
                docRef.set(data).await()
            }
        } catch (e: Exception) {
            Log.e("FirestoreFavorites", "❌ Failed to toggle favorite: ${e.message}", e)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun isFavorite(productId: String): Flow<Boolean> = currentHouseholdIdFlow().flatMapLatest { householdId ->
        callbackFlow {
            val subscription = firestore.collection("households")
                .document(householdId)
                .collection("favorites")
                .document(productId)
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

    override suspend fun saveCustomProduct(product: Product) {
        try {
            val householdId = getHouseholdId()
            val data = mapOf(
                "nombre" to product.nombre,
                "categoria" to product.categoria,
                "imageUrl" to product.imageUrl,
                "brands" to product.brands,
                "isCustom" to true
            )
            firestore.collection("households")
                .document(householdId)
                .collection("favorites")
                .document(product.id)
                .set(data)
                .await()
        } catch (e: Exception) {
            Log.e("FirestoreFavorites", "❌ Failed to save custom product: ${e.message}", e)
        }
    }
}
