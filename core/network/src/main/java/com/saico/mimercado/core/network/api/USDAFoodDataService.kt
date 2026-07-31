package com.saico.mimercado.core.network.api

import com.saico.mimercado.core.network.dto.USDASearchResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface USDAFoodDataService {
    /**
     * Documentation: https://app.swaggerhub.com/apis/fdc_api/FoodDataCentral/1.0.1#/FDC/getFoodsSearch
     */
    @GET("v1/foods/search")
    suspend fun searchFoods(
        @Query("api_key") apiKey: String,
        @Query("query") query: String,
        @Query("dataType") dataType: String? = "Branded",
        @Query("pageSize") pageSize: Int = 20,
        @Query("pageNumber") page: Int = 1
    ): USDASearchResponseDto
}
