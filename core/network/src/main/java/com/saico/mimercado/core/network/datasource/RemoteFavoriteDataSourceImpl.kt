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
import kotlinx.coroutines.flow.flowOf
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
            trySend("")
            close()
            return@callbackFlow
        }
        val subscription = firestore.collection("users").document(uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend("")
                    return@addSnapshotListener
                }
                val householdId = snapshot?.getString("householdId") ?: ""
                trySend(householdId)
            }
        awaitClose { subscription.remove() }
    }

    private suspend fun getHouseholdId(): String {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return ""
        return try {
            val snapshot = firestore.collection("users").document(uid).get().await()
            snapshot.getString("householdId") ?: ""
        } catch (e: Exception) {
            ""
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getFavorites(): Flow<List<Product>> = currentHouseholdIdFlow().flatMapLatest { householdId ->
        if (householdId.isBlank()) {
            flowOf(emptyList())
        } else {
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
    }

    override suspend fun toggleFavorite(product: Product) {
        val householdId = getHouseholdId()
        if (householdId.isBlank()) return
        val favRef = firestore.collection("households")
            .document(householdId)
            .collection("favorites")
            .document(product.id)

        val doc = favRef.get().await()
        if (doc.exists()) {
            favRef.delete().await()
        } else {
            val data = mapOf(
                "id" to product.id,
                "upc" to product.upc,
                "nombre" to product.nombre,
                "categoria" to product.categoria,
                "imageUrl" to product.imageUrl,
                "brands" to product.brands,
                "isCustom" to product.isCustom
            )
            favRef.set(data).await()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun isFavorite(productId: String): Flow<Boolean> = currentHouseholdIdFlow().flatMapLatest { householdId ->
        if (householdId.isBlank()) {
            flowOf(false)
        } else {
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
                        trySend(snapshot?.exists() == true)
                    }
                awaitClose { subscription.remove() }
            }
        }
    }

    override suspend fun saveCustomProduct(product: Product) {
        val householdId = getHouseholdId()
        if (householdId.isBlank()) return
        val data = mapOf(
            "id" to product.id,
            "upc" to product.upc,
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
    }

    override suspend fun getCustomProduct(productId: String): Result<Product?> = try {
        val householdId = getHouseholdId()
        if (householdId.isBlank()) {
            Result.success(null)
        } else {
            val doc = firestore.collection("households")
                .document(householdId)
                .collection("favorites")
                .document(productId)
                .get()
                .await()

            if (doc.exists()) {
                val product = Product(
                    id = doc.id,
                    upc = doc.getString("upc") ?: "",
                    nombre = doc.getString("nombre") ?: "",
                    categoria = doc.getString("categoria") ?: "",
                    imageUrl = doc.getString("imageUrl") ?: "",
                    brands = doc.getString("brands") ?: "",
                    isFavorite = true,
                    isCustom = doc.getBoolean("isCustom") ?: true
                )
                Result.success(product)
            } else {
                Result.success(null)
            }
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun deleteCustomProduct(productId: String): Result<Unit> = try {
        val householdId = getHouseholdId()
        if (householdId.isNotBlank()) {
            firestore.collection("households")
                .document(householdId)
                .collection("favorites")
                .document(productId)
                .delete()
                .await()
        }
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
