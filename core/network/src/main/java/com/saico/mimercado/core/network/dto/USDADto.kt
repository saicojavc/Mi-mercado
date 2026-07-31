package com.saico.mimercado.core.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class USDASearchResponseDto(
    @Json(name = "totalHits") val totalHits: Int = 0,
    @Json(name = "currentPage") val currentPage: Int = 0,
    @Json(name = "totalPages") val totalPages: Int = 0,
    @Json(name = "foods") val foods: List<USDAFoodDto> = emptyList()
)

@JsonClass(generateAdapter = true)
data class USDAFoodDto(
    @Json(name = "fdcId") val fdcId: Int,
    @Json(name = "description") val description: String,
    @Json(name = "brandOwner") val brandOwner: String? = null,
    @Json(name = "gtinUpc") val gtinUpc: String? = null,
    @Json(name = "foodCategory") val foodCategory: String? = null
)

@JsonClass(generateAdapter = true)
data class USDAFoodDetailsDto(
    @Json(name = "fdcId") val fdcId: Int,
    @Json(name = "description") val description: String,
    @Json(name = "brandOwner") val brandOwner: String? = null,
    @Json(name = "gtinUpc") val gtinUpc: String? = null,
    @Json(name = "ingredients") val ingredients: String? = null,
    @Json(name = "labelNutrients") val labelNutrients: USDALabelNutrientsDto? = null,
    @Json(name = "foodCategory") val foodCategory: String? = null
)

@JsonClass(generateAdapter = true)
data class USDALabelNutrientsDto(
    @Json(name = "fat") val fat: USDANutrientValueDto? = null,
    @Json(name = "saturatedFat") val saturatedFat: USDANutrientValueDto? = null,
    @Json(name = "transFat") val transFat: USDANutrientValueDto? = null,
    @Json(name = "cholesterol") val cholesterol: USDANutrientValueDto? = null,
    @Json(name = "sodium") val sodium: USDANutrientValueDto? = null,
    @Json(name = "carbohydrates") val carbohydrates: USDANutrientValueDto? = null,
    @Json(name = "fiber") val fiber: USDANutrientValueDto? = null,
    @Json(name = "sugars") val sugars: USDANutrientValueDto? = null,
    @Json(name = "protein") val protein: USDANutrientValueDto? = null,
    @Json(name = "calories") val calories: USDANutrientValueDto? = null
)

@JsonClass(generateAdapter = true)
data class USDANutrientValueDto(
    @Json(name = "value") val value: Double = 0.0
)
