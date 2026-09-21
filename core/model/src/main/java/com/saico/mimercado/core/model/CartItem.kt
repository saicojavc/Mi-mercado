package com.saico.mimercado.core.model

import kotlinx.serialization.Serializable

@Serializable
data class CartItem(
    var itemId: String = "",
    var upc: String = "",
    var nombre: String = "",
    var brands: String = "",
    var imageUrl: String = "",
    var categoria: String = "",
    var cantidad: Int = 0,
    var timestamp: Long = System.currentTimeMillis(),
    var addedBy: String = "",
    var addedByAvatar: String? = null,
    var addedByDisplayName: String? = null
) {
    fun toProduct() = Product(
        id = itemId.substringBefore("_"),
        upc = upc,
        nombre = nombre,
        brands = brands,
        imageUrl = imageUrl,
        categoria = categoria,
        isCustom = brands.contains("Personalizado", ignoreCase = true)
    )
}
