package com.saico.mimercado.core.domain.repository

interface ImageSearchRepository {
    suspend fun searchImages(query: String): Result<List<ProductImageResult>>
}

enum class ImageProvider { PEXELS, UNSPLASH }

data class ProductImageResult(
    val id: String,
    val thumbnailUrl: String,
    val fullUrl: String,
    val photographerName: String,
    val photographerUrl: String,
    val source: ImageProvider
)
