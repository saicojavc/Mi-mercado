package com.saico.mimercado.feature.lists.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.saico.mimercado.core.ui.navigation.NavigationCommand
import com.saico.mimercado.core.ui.navigation.Navigator
import com.saico.mimercado.core.ui.navigation.routes.lists.CreateListRoute
import com.saico.mimercado.core.ui.navigation.routes.lists.ShoppingListDetailRoute
import com.saico.mimercado.core.ui.navigation.routes.lists.ShoppingListsRoute
import com.saico.mimercado.core.ui.navigation.routes.products.CreateCustomProductRoute
import com.saico.mimercado.core.ui.navigation.routes.products.ProductDetailsRoute
import com.saico.mimercado.core.ui.navigation.routes.profile.ProfileRoute
import com.saico.mimercado.core.ui.navigation.routes.search.SearchRoute
import com.saico.mimercado.feature.lists.CreateListScreen
import com.saico.mimercado.feature.lists.CreateListViewModel
import com.saico.mimercado.feature.lists.ShoppingListDetailScreen
import com.saico.mimercado.feature.lists.ShoppingListDetailViewModel
import com.saico.mimercado.feature.lists.ShoppingListsScreen
import com.saico.mimercado.feature.lists.ShoppingListsViewModel

fun NavGraphBuilder.listsGraph(
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

    composable<ShoppingListDetailRoute> {
        val viewModel: ShoppingListDetailViewModel = hiltViewModel()
        ShoppingListDetailScreen(
            viewModel = viewModel,
            onBackClick = { navigator.navigate(NavigationCommand.PopBackstack) },
            onSearchClick = { navigator.navigate(NavigationCommand.NavigateTo(SearchRoute)) },
            onScanBarcodeClick = { navigator.navigate(NavigationCommand.NavigateTo(SearchRoute)) },
            onProductClick = { product ->
                navigator.navigate(NavigationCommand.NavigateTo(ProductDetailsRoute(product.id, product.isCustom)))
            },
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
}
