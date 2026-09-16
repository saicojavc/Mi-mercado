package com.saico.mimercado.feature.products.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.saico.mimercado.core.model.Product
import com.saico.mimercado.core.ui.navigation.NavigationCommand
import com.saico.mimercado.core.ui.navigation.Navigator
import com.saico.mimercado.core.ui.navigation.routes.household.HouseholdSettingsRoute
import com.saico.mimercado.core.ui.navigation.routes.products.CreateCustomProductRoute
import com.saico.mimercado.core.ui.navigation.routes.products.ProductDetailsRoute
import com.saico.mimercado.core.ui.navigation.routes.products.ProductsRoute
import com.saico.mimercado.feature.products.CreateCustomProductScreen
import com.saico.mimercado.feature.products.CreateCustomProductViewModel
import com.saico.mimercado.feature.products.ProductDetailsScreen
import com.saico.mimercado.feature.products.ProductDetailsViewModel
import com.saico.mimercado.feature.products.ProductListScreen
import com.saico.mimercado.feature.products.ProductListViewModel
import kotlinx.coroutines.flow.SharedFlow

fun NavGraphBuilder.productsGraph(
    totalCartItems: Int,
    errorMessages: SharedFlow<String>,
    onAddToCart: (Product) -> Unit,
    navigator: Navigator
) {
    composable<ProductsRoute> {
        val viewModel: ProductListViewModel = hiltViewModel()
        ProductListScreen(
            viewModel = viewModel,
            totalCartItems = totalCartItems,
            errorMessages = errorMessages,
            onAddToCart = onAddToCart,
            onProductClick = { product ->
                navigator.navigate(NavigationCommand.NavigateTo(ProductDetailsRoute(product.id)))
            },
            onCreateCustomProductClick = {
                navigator.navigate(NavigationCommand.NavigateTo(CreateCustomProductRoute()))
            },
            onSettingsClick = {
                navigator.navigate(NavigationCommand.NavigateTo(HouseholdSettingsRoute))
            }
        )
    }

    composable<ProductDetailsRoute> {
        val viewModel: ProductDetailsViewModel = hiltViewModel()
        ProductDetailsScreen(
            viewModel = viewModel,
            onAddToCart = onAddToCart,
            onBackClick = { navigator.navigate(NavigationCommand.PopBackstack) }
        )
    }

    composable<CreateCustomProductRoute> {
        val viewModel: CreateCustomProductViewModel = hiltViewModel()
        CreateCustomProductScreen(
            viewModel = viewModel,
            onBackClick = { navigator.navigate(NavigationCommand.PopBackstack) }
        )
    }

    // Removida la declaración duplicada de HouseholdSettingsRoute.
    // Ahora es administrada de forma centralizada por el nuevo módulo :feature:settings
}
