package com.saico.mimercado.core.model

import kotlinx.serialization.Serializable

@Serializable
data class DecoratedProduct(
    val product: Product,
    val additionalBrandsCount: Int = 0
)
