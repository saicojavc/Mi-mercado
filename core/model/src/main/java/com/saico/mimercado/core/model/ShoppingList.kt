package com.saico.mimercado.core.model

import kotlinx.serialization.Serializable

@Serializable
enum class ShoppingListType { MAIN, CUSTOM }

@Serializable
data class ShoppingList(
    val id: String = "",
    val householdId: String = "",
    val name: String = "",
    val type: ShoppingListType = ShoppingListType.CUSTOM,
    val coverTheme: ListCoverTheme = ListCoverTheme(),
    val createdBy: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Serializable
data class ListCoverTheme(
    val colorStart: String = "#00D9FF", // Cyan default
    val colorEnd: String = "#06B6D4",
    val icon: String = "shopping_cart"
)

@Serializable
data class ShoppingListSummary(
    val id: String,
    val name: String,
    val type: ShoppingListType,
    val coverTheme: ListCoverTheme,
    val itemCount: Int
)
