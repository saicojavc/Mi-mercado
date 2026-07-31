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
    @Json(name = "publishedDate") val publishedDate: String? = null,
    @Json(name = "foodCategory") val foodCategory: String? = null,
    @Json(name = "image") val image: String? = null // USDA API usually doesn't provide images directly in search, might need another way or placeholder
)
