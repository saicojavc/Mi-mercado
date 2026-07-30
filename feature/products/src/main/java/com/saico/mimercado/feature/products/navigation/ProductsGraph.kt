package com.saico.mimercado.feature.products.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.saico.mimercado.core.model.Product
import com.saico.mimercado.core.ui.navigation.routes.products.ProductsRoute
import com.saico.mimercado.feature.products.ProductListScreen
import com.saico.mimercado.feature.products.ProductListViewModel
import kotlinx.coroutines.flow.SharedFlow

fun NavGraphBuilder.productsGraph(
    totalCartItems: Int,
    errorMessages: SharedFlow<String>,
    onAddToCart: (Product) -> Unit
) {
    composable<ProductsRoute> {
        val viewModel: ProductListViewModel = hiltViewModel()
        ProductListScreen(
            viewModel = viewModel,
            totalCartItems = totalCartItems,
            errorMessages = errorMessages,
            onAddToCart = onAddToCart
        )
    }
}
