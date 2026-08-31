package com.saico.mimercado.feature.search.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.saico.mimercado.core.ui.navigation.NavigationCommand
import com.saico.mimercado.core.ui.navigation.Navigator
import com.saico.mimercado.core.ui.navigation.routes.products.ProductDetailsRoute
import com.saico.mimercado.core.ui.navigation.routes.search.SearchRoute
import com.saico.mimercado.feature.search.SearchScreen

fun NavGraphBuilder.searchGraph(
    navigator: Navigator
) {
    composable<SearchRoute> {
        SearchScreen(
            onProductClick = { fdcId ->
                navigator.navigate(NavigationCommand.NavigateTo(ProductDetailsRoute(fdcId)))
            }
        )
    }
}
