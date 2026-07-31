package com.saico.mimercado.core.data.repository

import com.saico.mimercado.core.common.UsdaImageResolver
import com.saico.mimercado.core.domain.repository.ProductRepository
import com.saico.mimercado.core.model.Product
import com.saico.mimercado.core.model.ProductDetails
import com.saico.mimercado.core.network.BuildConfig
import com.saico.mimercado.core.network.api.OffApiService
import com.saico.mimercado.core.network.api.USDAFoodDataService
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.util.Locale
import javax.inject.Inject

class NetworkProductRepository @Inject constructor(
    private val usdaService: USDAFoodDataService,
    private val offService: OffApiService
) : ProductRepository {

    override suspend fun getProducts(
        category: String?,
        searchQuery: String?,
        store: String?,
        page: Int
    ): Result<List<Product>> = coroutineScope {
        try {
            val isUpc = searchQuery != null && searchQuery.all { it.isDigit() } && searchQuery.length >= 8
            
            val baseQuery = when {
                !searchQuery.isNullOrBlank() -> searchQuery
                !category.isNullOrBlank() && category != "Todos" -> mapToEnglishCategory(category)
                else -> "food"
            }
            
            // Do NOT append store if searching by exact UPC to avoid API confusion
            val finalQuery = if (!store.isNullOrBlank() && !isUpc) "$baseQuery $store" else baseQuery
            
            val usdaResponse = usdaService.searchFoods(
                apiKey = BuildConfig.USDA_API_KEY,
                query = finalQuery,
                dataType = "Branded",
                page = page
            )
            
            val products = usdaResponse.foods.map { dto ->
                async {
                    val upc = dto.gtinUpc ?: ""
                    var imageUrl = ""
                    
                    if (upc.isNotBlank()) {
                        try {
                            val offResponse = offService.getProductMetadata(upc)
                            if (offResponse.status == 1) {
                                imageUrl = offResponse.product?.imageFrontUrl 
                                    ?: offResponse.product?.imageUrl 
                                    ?: ""
                            }
                        } catch (e: Exception) {}
                    }

                    Product(
                        id = dto.fdcId.toString(),
                        upc = upc,
                        nombre = dto.description.formatTitleCase(),
                        categoria = category ?: dto.foodCategory ?: "General",
                        imageUrl = imageUrl,
                        brands = dto.brandOwner?.formatTitleCase() ?: "American Brand"
                    )
                }
            }.awaitAll()
            
            Result.success(products)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getProductDetails(fdcId: String): Result<ProductDetails> = coroutineScope {
        try {
            val dto = usdaService.getFoodDetails(fdcId, BuildConfig.USDA_API_KEY)
            val upc = dto.gtinUpc ?: ""
            
            val imageUrlAsync = async {
                var url = ""
                if (upc.isNotBlank()) {
                    try {
                        val offResponse = offService.getProductMetadata(upc)
                        if (offResponse.status == 1) {
                            url = offResponse.product?.imageFrontUrl ?: offResponse.product?.imageUrl ?: ""
                        }
                    } catch (e: Exception) {}
                }
                url
            }

            val imageUrl = imageUrlAsync.await()
            
            val details = ProductDetails(
                id = dto.fdcId.toString(),
                upc = upc,
                name = dto.description.formatTitleCase(),
                brand = dto.brandOwner?.formatTitleCase() ?: "American Brand",
                category = dto.foodCategory ?: "General",
                imageUrl = imageUrl,
                ingredients = dto.ingredients ?: "No ingredients listed",
                nutrients = mapOf(
                    "Calories" to (dto.labelNutrients?.calories?.value ?: 0.0),
                    "Protein" to (dto.labelNutrients?.protein?.value ?: 0.0),
                    "Fat" to (dto.labelNutrients?.fat?.value ?: 0.0),
                    "Carbs" to (dto.labelNutrients?.carbohydrates?.value ?: 0.0),
                    "Sodium" to (dto.labelNutrients?.sodium?.value ?: 0.0)
                )
            )
            Result.success(details)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun mapToEnglishCategory(category: String): String {
        return when (category) {
            "Lácteos" -> "Dairy"
            "Panadería" -> "Bakery"
            "Carnes" -> "Meat"
            "Frutas y verduras" -> "Fruit Vegetable"
            "Despensa" -> "Pantry"
            "Limpieza" -> "Cleaning"
            "Bebidas" -> "Beverage"
            else -> category
        }
    }

    private fun String.formatTitleCase(): String {
        return try {
            this.lowercase(Locale.US)
                .split(" ")
                .filter { it.isNotBlank() }
                .joinToString(" ") { word ->
                    word.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.US) else it.toString() }
                }
        } catch (e: Exception) {
            this
        }
    }
}
