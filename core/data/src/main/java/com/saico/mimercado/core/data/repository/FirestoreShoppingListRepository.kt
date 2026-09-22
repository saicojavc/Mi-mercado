package com.saico.mimercado.core.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.saico.mimercado.core.domain.repository.ShoppingListRepository
import com.saico.mimercado.core.model.CartItem
import com.saico.mimercado.core.model.ListCoverTheme
import com.saico.mimercado.core.model.ShoppingList
import com.saico.mimercado.core.model.ShoppingListSummary
import com.saico.mimercado.core.model.ShoppingListType
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreShoppingListRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) : ShoppingListRepository {

    override fun observeLists(householdId: String): Flow<List<ShoppingListSummary>> = callbackFlow {
        if (householdId.isBlank()) {
            trySend(emptyList())
            awaitClose { }
            return@callbackFlow
        }

        val itemListeners = mutableListOf<ListenerRegistration>()
        val itemCountMap = mutableMapOf<String, Int>()

        val subscription = firestore.collection("households")
            .document(householdId)
            .collection("lists")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                val docs = snapshot?.documents ?: emptyList()
                if (docs.isEmpty()) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                // Clean up previous item listeners
                itemListeners.forEach { it.remove() }
                itemListeners.clear()

                val rawSummaries = docs.mapNotNull { doc ->
                    val typeStr = doc.getString("type") ?: ShoppingListType.CUSTOM.name
                    val coverThemeMap = doc.get("coverTheme") as? Map<*, *>
                    val coverTheme = if (coverThemeMap != null) {
                        ListCoverTheme(
                            colorStart = coverThemeMap["colorStart"] as? String ?: "#0F172A",
                            colorEnd = coverThemeMap["colorEnd"] as? String ?: "#06B6D4",
                            icon = coverThemeMap["icon"] as? String ?: "shopping_cart"
                        )
                    } else ListCoverTheme()

                    ShoppingListSummary(
                        id = doc.id,
                        name = doc.getString("name") ?: "Lista",
                        type = try { ShoppingListType.valueOf(typeStr) } catch (_: Exception) { ShoppingListType.CUSTOM },
                        coverTheme = coverTheme,
                        itemCount = itemCountMap[doc.id] ?: 0
                    )
                }

                // Deduplicate MAIN lists: keep only 1 MAIN list summary
                val mainLists = rawSummaries.filter { it.type == ShoppingListType.MAIN }
                val customLists = rawSummaries.filter { it.type == ShoppingListType.CUSTOM }
                val deduplicated = if (mainLists.size > 1) {
                    listOf(mainLists.first()) + customLists
                } else rawSummaries

                fun emitUpdatedList() {
                    val currentList = deduplicated.map { summary ->
                        summary.copy(itemCount = itemCountMap[summary.id] ?: 0)
                    }.sortedWith(compareBy<ShoppingListSummary> { it.type != ShoppingListType.MAIN }.thenBy { it.name })

                    trySend(currentList)
                }

                emitUpdatedList()

                // Attach real-time listener for items subcollection on each list
                for (doc in docs) {
                    val listId = doc.id
                    val itemReg = doc.reference.collection("items").addSnapshotListener { itemsSnapshot, _ ->
                        val count = itemsSnapshot?.size() ?: 0
                        itemCountMap[listId] = count
                        emitUpdatedList()
                    }
                    itemListeners.add(itemReg)
                }
            }

        awaitClose {
            subscription.remove()
            itemListeners.forEach { it.remove() }
            itemListeners.clear()
        }
    }

    override fun observeList(householdId: String, listId: String): Flow<ShoppingList?> = callbackFlow {
        if (householdId.isBlank() || listId.isBlank()) {
            trySend(null)
            awaitClose { }
            return@callbackFlow
        }

        val subscription = firestore.collection("households")
            .document(householdId)
            .collection("lists")
            .document(listId)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null || !snapshot.exists()) {
                    trySend(null)
                    return@addSnapshotListener
                }
                val typeStr = snapshot.getString("type") ?: ShoppingListType.CUSTOM.name
                val coverThemeMap = snapshot.get("coverTheme") as? Map<*, *>
                val coverTheme = if (coverThemeMap != null) {
                    ListCoverTheme(
                        colorStart = coverThemeMap["colorStart"] as? String ?: "#0F172A",
                        colorEnd = coverThemeMap["colorEnd"] as? String ?: "#06B6D4",
                        icon = coverThemeMap["icon"] as? String ?: "shopping_cart"
                    )
                } else ListCoverTheme()

                val shoppingList = ShoppingList(
                    id = snapshot.id,
                    householdId = householdId,
                    name = snapshot.getString("name") ?: "Lista",
                    type = try { ShoppingListType.valueOf(typeStr) } catch (_: Exception) { ShoppingListType.CUSTOM },
                    coverTheme = coverTheme,
                    createdBy = snapshot.getString("createdBy") ?: "",
                    createdAt = snapshot.getLong("createdAt") ?: System.currentTimeMillis()
                )
                trySend(shoppingList)
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun createList(
        householdId: String,
        name: String,
        coverTheme: ListCoverTheme,
        createdBy: String
    ): Result<String> = try {
        val listRef = firestore.collection("households")
            .document(householdId)
            .collection("lists")
            .document()

        val listData = ShoppingList(
            id = listRef.id,
            householdId = householdId,
            name = name,
            type = ShoppingListType.CUSTOM,
            coverTheme = coverTheme,
            createdBy = createdBy,
            createdAt = System.currentTimeMillis()
        )

        listRef.set(listData).await()
        Result.success(listRef.id)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun renameList(householdId: String, listId: String, name: String): Result<Unit> = try {
        firestore.collection("households")
            .document(householdId)
            .collection("lists")
            .document(listId)
            .update("name", name)
            .await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun deleteList(householdId: String, listId: String): Result<Unit> = try {
        val doc = firestore.collection("households")
            .document(householdId)
            .collection("lists")
            .document(listId)
            .get()
            .await()

        val type = doc.getString("type") ?: ShoppingListType.CUSTOM.name
        if (type == ShoppingListType.MAIN.name) {
            throw Exception("No se puede eliminar la lista principal")
        }

        doc.reference.delete().await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun ensureMainListExists(householdId: String, createdBy: String): Result<String> = try {
        if (householdId.isBlank()) throw Exception("Household ID inválido")

        val listsRef = firestore.collection("households")
            .document(householdId)
            .collection("lists")

        val query = listsRef
            .whereEqualTo("type", ShoppingListType.MAIN.name)
            .get()
            .await()

        if (!query.isEmpty) {
            val mainDocs = query.documents
            // If duplicate MAIN documents exist, clean up empty ones
            if (mainDocs.size > 1) {
                var chosenDoc = mainDocs.first()
                var maxItems = -1

                for (doc in mainDocs) {
                    val count = try {
                        doc.reference.collection("items").get().await().size()
                    } catch (_: Exception) { 0 }
                    if (count > maxItems) {
                        maxItems = count
                        chosenDoc = doc
                    }
                }

                // Delete other duplicate empty MAIN lists
                for (doc in mainDocs) {
                    if (doc.id != chosenDoc.id) {
                        try { doc.reference.delete().await() } catch (_: Exception) {}
                    }
                }
                Result.success(chosenDoc.id)
            } else {
                Result.success(mainDocs.first().id)
            }
        } else {
            val listRef = listsRef.document("main")

            val mainList = ShoppingList(
                id = "main",
                householdId = householdId,
                name = "Principal",
                type = ShoppingListType.MAIN,
                coverTheme = ListCoverTheme(colorStart = "#0F172A", colorEnd = "#06B6D4", icon = "home"),
                createdBy = createdBy,
                createdAt = System.currentTimeMillis()
            )

            listRef.set(mainList).await()

            // One-time migration: check if households/{householdId}/cart items exist and copy them to this list items subcollection
            val cartSnapshot = firestore.collection("households")
                .document(householdId)
                .collection("cart")
                .get()
                .await()

            for (itemDoc in cartSnapshot.documents) {
                val itemData = itemDoc.data
                if (itemData != null) {
                    listRef.collection("items").document(itemDoc.id).set(itemData).await()
                }
            }

            Result.success("main")
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override fun observeListItems(householdId: String, listId: String): Flow<List<CartItem>> = callbackFlow {
        if (householdId.isBlank() || listId.isBlank()) {
            trySend(emptyList())
            awaitClose { }
            return@callbackFlow
        }

        val subscription = firestore.collection("households")
            .document(householdId)
            .collection("lists")
            .document(listId)
            .collection("items")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val items = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(CartItem::class.java)
                } ?: emptyList()
                trySend(items)
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun addListItem(householdId: String, listId: String, item: CartItem): Result<Unit> = try {
        val itemRef = firestore.collection("households")
            .document(householdId)
            .collection("lists")
            .document(listId)
            .collection("items")
            .document(item.itemId)

        itemRef.set(item).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun updateListItemQuantity(householdId: String, listId: String, itemId: String, newQuantity: Int): Result<Unit> = try {
        val itemRef = firestore.collection("households")
            .document(householdId)
            .collection("lists")
            .document(listId)
            .collection("items")
            .document(itemId)

        if (newQuantity <= 0) {
            itemRef.delete().await()
        } else {
            itemRef.update("cantidad", newQuantity).await()
        }
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun removeListItem(householdId: String, listId: String, itemId: String): Result<Unit> = try {
        firestore.collection("households")
            .document(householdId)
            .collection("lists")
            .document(listId)
            .collection("items")
            .document(itemId)
            .delete()
            .await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun clearListItems(householdId: String, listId: String): Result<Unit> = try {
        val itemsSnapshot = firestore.collection("households")
            .document(householdId)
            .collection("lists")
            .document(listId)
            .collection("items")
            .get()
            .await()

        val batch = firestore.batch()
        for (doc in itemsSnapshot.documents) {
            batch.delete(doc.reference)
        }
        batch.commit().await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
