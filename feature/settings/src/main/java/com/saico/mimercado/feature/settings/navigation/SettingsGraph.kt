package com.saico.mimercado.feature.settings.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.saico.mimercado.core.ui.navigation.NavigationCommand
import com.saico.mimercado.core.ui.navigation.Navigator
import com.saico.mimercado.core.ui.navigation.routes.household.HouseholdSettingsRoute
import com.saico.mimercado.feature.settings.HouseholdSettingsScreen
import com.saico.mimercado.feature.settings.HouseholdSettingsViewModel

fun NavGraphBuilder.settingsGraph(
    navigator: Navigator
) {
    composable<HouseholdSettingsRoute> {
        val viewModel: HouseholdSettingsViewModel = hiltViewModel()
        HouseholdSettingsScreen(
            viewModel = viewModel,
            onBackClick = { navigator.navigate(NavigationCommand.PopBackstack) }
        )
    }
}
