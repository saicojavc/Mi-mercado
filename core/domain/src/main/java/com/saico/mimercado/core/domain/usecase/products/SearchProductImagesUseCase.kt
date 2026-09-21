package com.saico.mimercado.core.domain.usecase.products

import com.saico.mimercado.core.domain.repository.ImageSearchRepository
import javax.inject.Inject

class SearchProductImagesUseCase @Inject constructor(
    private val repository: ImageSearchRepository
) {
    suspend operator fun invoke(query: String) = repository.searchImages(query)
}
