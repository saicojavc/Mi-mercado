package com.saico.mimercado.core.data.repository

import android.util.Log
import com.saico.mimercado.core.domain.repository.ProductRepository
import com.saico.mimercado.core.model.Product
import com.saico.mimercado.core.network.BuildConfig
import com.saico.mimercado.core.network.api.USDAFoodDataService
import javax.inject.Inject

class NetworkProductRepository @Inject constructor(
    private val apiService: USDAFoodDataService
) : ProductRepository {

    override suspend fun getProducts(
        category: String?,
        searchQuery: String?,
        page: Int
    ): Result<List<Product>> {
        return try {
            val query = when {
                !searchQuery.isNullOrBlank() -> searchQuery
                !category.isNullOrBlank() && category != "Todos" -> mapToEnglishCategory(category)
                else -> "food"
            }
            
            Log.d("ProductRepo", "🔍 Fetching USDA products - Query: $query, Page: $page")
            
            val response = apiService.searchFoods(
                apiKey = BuildConfig.USDA_API_KEY,
                query = query,
                page = page
            )
            
            Log.d("ProductRepo", "✅ USDA Response received. Found ${response.foods.size} products.")
            
            val products = response.foods.map { dto ->
                Product(
                    id = dto.fdcId.toString(),
                    nombre = dto.description,
                    categoria = category ?: dto.foodCategory ?: "General",
                    imageUrl = "", // USDA doesn't provide easy search images
                    brands = dto.brandOwner ?: ""
                )
            }
            Result.success(products)
        } catch (e: Exception) {
            Log.e("ProductRepo", "❌ USDA API Call failed", e)
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
}
