package com.saico.mimercado.core.network.api

import com.saico.mimercado.core.network.model.PexelsSearchResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface PexelsApiService {
    @GET("v1/search")
    suspend fun searchPhotos(
        @Header("Authorization") apiKey: String,
        @Query("query") query: String,
        @Query("per_page") perPage: Int = 20
    ): PexelsSearchResponse
}
