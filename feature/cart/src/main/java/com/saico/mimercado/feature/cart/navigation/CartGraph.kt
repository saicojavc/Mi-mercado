package com.saico.mimercado.feature.cart.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.saico.mimercado.core.ui.navigation.routes.cart.CartRoute
import com.saico.mimercado.feature.cart.CartScreen
import com.saico.mimercado.feature.cart.CartViewModel

fun NavGraphBuilder.cartGraph() {
    composable<CartRoute> {
        val viewModel: CartViewModel = hiltViewModel()
        CartScreen(viewModel = viewModel)
    }
}
