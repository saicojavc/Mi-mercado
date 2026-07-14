package com.saico.mimercado.model

import kotlinx.serialization.Serializable

@Serializable
data class CartItem(
    var productId: String = "",
    var nombre: String = "",
    var emoji: String = "",
    var categoria: String = "",
    var cantidad: Int = 0,
    var timestamp: Long = System.currentTimeMillis(),
    var addedBy: String = ""
)
