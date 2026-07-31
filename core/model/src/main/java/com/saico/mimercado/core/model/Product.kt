package com.saico.mimercado.core.model

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: String = "",
    val nombre: String = "",
    val categoria: String = "",
    val imageUrl: String = "",
    val brands: String = ""
) {
    companion object {
        // Keeping sampleProducts for a while until API is fully integrated, but updated to the new structure
        val sampleProducts = listOf(
            Product("1", "Queso", "Lácteos", "", "Marca A"),
            Product("2", "Leche", "Lácteos", "", "Marca B"),
        )
    }
}
