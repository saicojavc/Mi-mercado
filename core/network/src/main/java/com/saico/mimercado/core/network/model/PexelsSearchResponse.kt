package com.saico.mimercado.core.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PexelsSearchResponse(
    @Json(name = "photos") val photos: List<PexelsPhoto>
)

@JsonClass(generateAdapter = true)
data class PexelsPhoto(
    @Json(name = "id") val id: Long,
    @Json(name = "src") val src: PexelsPhotoSource,
    @Json(name = "photographer") val photographer: String,
    @Json(name = "photographer_url") val photographerUrl: String
)

@JsonClass(generateAdapter = true)
data class PexelsPhotoSource(
    @Json(name = "original") val original: String,
    @Json(name = "large") val large: String,
    @Json(name = "medium") val medium: String,
    @Json(name = "small") val small: String,
    @Json(name = "portrait") val portrait: String,
    @Json(name = "landscape") val landscape: String,
    @Json(name = "tiny") val tiny: String
)
