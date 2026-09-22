package com.saico.mimercado.feature.settings.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.saico.mimercado.core.ui.navigation.NavigationCommand
import com.saico.mimercado.core.ui.navigation.Navigator
import com.saico.mimercado.core.ui.navigation.routes.household.HouseholdSettingsRoute
import com.saico.mimercado.core.ui.navigation.routes.profile.ProfileRoute
import com.saico.mimercado.feature.settings.HouseholdSettingsScreen
import com.saico.mimercado.feature.settings.HouseholdSettingsViewModel
import com.saico.mimercado.feature.settings.ProfileScreen
import com.saico.mimercado.feature.settings.ProfileViewModel

fun NavGraphBuilder.settingsGraph(
    navigator: Navigator
) {
    composable<ProfileRoute> {
        val viewModel: ProfileViewModel = hiltViewModel()
        ProfileScreen(viewModel = viewModel)
    }

    composable<HouseholdSettingsRoute> {
        val viewModel: HouseholdSettingsViewModel = hiltViewModel()
        HouseholdSettingsScreen(
            viewModel = viewModel,
            onBackClick = { navigator.navigate(NavigationCommand.PopBackstack) }
        )
    }
}
