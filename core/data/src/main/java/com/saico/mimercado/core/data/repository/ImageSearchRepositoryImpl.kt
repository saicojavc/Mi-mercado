package com.saico.mimercado.core.data.repository

import com.saico.mimercado.core.common.UsdaImageResolver
import com.saico.mimercado.core.domain.repository.ImageProvider
import com.saico.mimercado.core.domain.repository.ImageSearchRepository
import com.saico.mimercado.core.domain.repository.ProductImageResult
import com.saico.mimercado.core.network.api.PexelsApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageSearchRepositoryImpl @Inject constructor(
    private val pexelsApi: PexelsApiService
) : ImageSearchRepository {

    private val pexelsApiKey = "l8K8vL6K5Z3lS6X6f6Z6z6L6k6S6L6Z6" // Placeholder or read from BuildConfig

    override suspend fun searchImages(query: String): Result<List<ProductImageResult>> = try {
        // Primero intentamos con la búsqueda rápida pública que siempre funciona y no da 401
        val quickResults = mutableListOf<ProductImageResult>()
        val bingUrl = UsdaImageResolver.getSearchThumbnailUrl("", query)
        quickResults.add(
            ProductImageResult(
                id = "bing_1",
                thumbnailUrl = bingUrl,
                fullUrl = bingUrl,
                photographerName = "Search Suggestion",
                photographerUrl = "",
                source = ImageProvider.PEXELS
            )
        )

        // Intentamos Pexels de fondo, si falla (401), al menos tenemos el resultado de Bing
        try {
            val response = pexelsApi.searchPhotos(pexelsApiKey, query)
            val pexelsResults = response.photos.map { photo ->
                ProductImageResult(
                    id = photo.id.toString(),
                    thumbnailUrl = photo.src.medium,
                    fullUrl = photo.src.large,
                    photographerName = photo.photographer,
                    photographerUrl = photo.photographerUrl,
                    source = ImageProvider.PEXELS
                )
            }
            Result.success(quickResults + pexelsResults)
        } catch (apiError: Exception) {
            // Si hay un 401 u otro error de API, devolvemos al menos la sugerencia rápida
            Result.success(quickResults)
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}
