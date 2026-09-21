package com.saico.mimercado.core.domain.usecase.products

import com.saico.mimercado.core.domain.repository.FavoriteRepository
import com.saico.mimercado.core.model.Product
import java.util.UUID
import javax.inject.Inject

class CreateCustomProductUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) {
    suspend operator fun invoke(
        nombre: String,
        categoria: String,
        imageUrl: String,
        id: String? = null
    ): Result<Product> {
        return try {
            val customProduct = Product(
                id = id ?: UUID.randomUUID().toString(),
                nombre = nombre,
                categoria = categoria,
                imageUrl = imageUrl,
                brands = "Personalizado",
                isFavorite = true,
                isCustom = true
            )
            favoriteRepository.saveCustomProduct(customProduct)
            Result.success(customProduct)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
