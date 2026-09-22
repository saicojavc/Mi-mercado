package com.saico.mimercado.feature.products.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.saico.mimercado.core.model.Product
import com.saico.mimercado.core.ui.navigation.NavigationCommand
import com.saico.mimercado.core.ui.navigation.Navigator
import com.saico.mimercado.core.ui.navigation.routes.lists.CreateListRoute
import com.saico.mimercado.core.ui.navigation.routes.lists.ShoppingListDetailRoute
import com.saico.mimercado.core.ui.navigation.routes.lists.ShoppingListsRoute
import com.saico.mimercado.core.ui.navigation.routes.products.CreateCustomProductRoute
import com.saico.mimercado.core.ui.navigation.routes.products.ProductDetailsRoute
import com.saico.mimercado.core.ui.navigation.routes.products.ProductsRoute
import com.saico.mimercado.core.ui.navigation.routes.profile.ProfileRoute
import com.saico.mimercado.core.ui.navigation.routes.search.SearchRoute
import com.saico.mimercado.feature.products.CreateCustomProductScreen
import com.saico.mimercado.feature.products.CreateCustomProductViewModel
import com.saico.mimercado.feature.products.CreateListScreen
import com.saico.mimercado.feature.products.CreateListViewModel
import com.saico.mimercado.feature.products.ProductDetailsScreen
import com.saico.mimercado.feature.products.ProductDetailsViewModel
import com.saico.mimercado.feature.products.ProductListScreen
import com.saico.mimercado.feature.products.ProductListViewModel
import com.saico.mimercado.feature.products.ShoppingListDetailScreen
import com.saico.mimercado.feature.products.ShoppingListDetailViewModel
import com.saico.mimercado.feature.products.ShoppingListsScreen
import com.saico.mimercado.feature.products.ShoppingListsViewModel
import kotlinx.coroutines.flow.SharedFlow

fun NavGraphBuilder.productsGraph(
    totalCartItems: Int,
    errorMessages: SharedFlow<String>,
    onAddToCart: (Product) -> Unit,
    navigator: Navigator
) {
    composable<ShoppingListsRoute> {
        val viewModel: ShoppingListsViewModel = hiltViewModel()
        ShoppingListsScreen(
            viewModel = viewModel,
            onListClick = { listId ->
                navigator.navigate(NavigationCommand.NavigateTo(ShoppingListDetailRoute(listId)))
            },
            onCreateListClick = {
                navigator.navigate(NavigationCommand.NavigateTo(CreateListRoute))
            },
            onProfileClick = {
                navigator.navigate(NavigationCommand.NavigateTo(ProfileRoute))
            }
        )
    }

    composable<ProductsRoute> {
        val viewModel: ProductListViewModel = hiltViewModel()
        ProductListScreen(
            viewModel = viewModel,
            totalCartItems = totalCartItems,
            errorMessages = errorMessages,
            onAddToCart = onAddToCart,
            onProductClick = { product ->
                navigator.navigate(NavigationCommand.NavigateTo(ProductDetailsRoute(product.id, product.isCustom)))
            },
            onCreateCustomProductClick = {
                val query = viewModel.uiState.value.searchQuery
                navigator.navigate(NavigationCommand.NavigateTo(CreateCustomProductRoute(prefillName = query.ifBlank { null })))
            },
            onSettingsClick = {
                navigator.navigate(NavigationCommand.NavigateTo(ProfileRoute))
            }
        )
    }

    composable<ShoppingListDetailRoute> {
        val viewModel: ShoppingListDetailViewModel = hiltViewModel()
        ShoppingListDetailScreen(
            viewModel = viewModel,
            onBackClick = { navigator.navigate(NavigationCommand.PopBackstack) },
            onSearchClick = { navigator.navigate(NavigationCommand.NavigateTo(SearchRoute)) },
            onScanBarcodeClick = { navigator.navigate(NavigationCommand.NavigateTo(SearchRoute)) },
            onCreateCustomProductClick = {
                navigator.navigate(NavigationCommand.NavigateTo(CreateCustomProductRoute()))
            }
        )
    }

    composable<CreateListRoute> {
        val viewModel: CreateListViewModel = hiltViewModel()
        CreateListScreen(
            viewModel = viewModel,
            onBackClick = { navigator.navigate(NavigationCommand.PopBackstack) }
        )
    }

    composable<ProductDetailsRoute> {
        val viewModel: ProductDetailsViewModel = hiltViewModel()
        ProductDetailsScreen(
            viewModel = viewModel,
            onAddToCart = onAddToCart,
            onEditClick = { productId ->
                navigator.navigate(NavigationCommand.NavigateTo(CreateCustomProductRoute(editingProductId = productId)))
            },
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
}
