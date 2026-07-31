package com.saico.mimercado.core.model

import kotlinx.serialization.Serializable

@Serializable
data class ProductDetails(
    val id: String,
    val upc: String,
    val name: String,
    val brand: String,
    val category: String,
    val imageUrl: String,
    val ingredients: String,
    val nutrients: Map<String, Double>
)
