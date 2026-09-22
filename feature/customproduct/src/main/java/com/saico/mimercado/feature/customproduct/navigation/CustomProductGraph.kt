package com.saico.mimercado.feature.customproduct.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.saico.mimercado.core.ui.navigation.NavigationCommand
import com.saico.mimercado.core.ui.navigation.Navigator
import com.saico.mimercado.core.ui.navigation.routes.products.CreateCustomProductRoute
import com.saico.mimercado.feature.customproduct.CreateCustomProductScreen
import com.saico.mimercado.feature.customproduct.CreateCustomProductViewModel

fun NavGraphBuilder.customProductGraph(
    navigator: Navigator
) {
    composable<CreateCustomProductRoute> {
        val viewModel: CreateCustomProductViewModel = hiltViewModel()
        CreateCustomProductScreen(
            viewModel = viewModel,
            onBackClick = { navigator.navigate(NavigationCommand.PopBackstack) }
        )
    }
}
