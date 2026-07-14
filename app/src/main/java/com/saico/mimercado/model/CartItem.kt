package com.saico.mimercado.model

import kotlinx.serialization.Serializable

@Serializable
data class CartItem(
    val productId: String = "",
    val nombre: String = "",
    val imagenUrl: String = "",
    val categoria: String = "",
    val cantidad: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)
