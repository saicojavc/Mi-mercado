package com.saico.mimercado.core.model

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: String = "",
    val upc: String = "",
    val nombre: String = "",
    val categoria: String = "",
    val imageUrl: String = "",
    val brands: String = ""
) {
    companion object {
        val sampleProducts = listOf(
            Product("1", "078742351873", "Queso", "Lácteos", "", "Marca A"),
            Product("2", "044000032029", "Leche", "Lácteos", "", "Marca B"),
        )
    }
}
