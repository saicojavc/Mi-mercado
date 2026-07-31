package com.saico.mimercado.core.network.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.http.GET
import retrofit2.http.Path

@JsonClass(generateAdapter = true)
data class OffProductResponse(
    @Json(name = "product") val product: OffProductDto? = null,
    @Json(name = "status") val status: Int = 0
)

@JsonClass(generateAdapter = true)
data class OffProductDto(
    @Json(name = "image_url") val imageUrl: String? = null,
    @Json(name = "image_front_url") val imageFrontUrl: String? = null
)

interface OffApiService {
    @GET("api/v0/product/{barcode}.json")
    suspend fun getProductMetadata(@Path("barcode") barcode: String): OffProductResponse
}
