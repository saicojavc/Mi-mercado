package com.saico.mimercado.core.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController

@Composable
fun NavigatorHandler(
    navigator: Navigator,
    navController: NavController,
) {
    val command by navigator.commands.collectAsState()
    when (val finalCommand = command) {
        is NavigationCommand.PopBackstack -> {
            navController.popBackStack()
            navigator.onNavigated()
        }

        is NavigationCommand.PopBackstackUntil -> {
            navController.popBackStack(
                finalCommand.route,
                finalCommand.inclusive
            )
            navigator.onNavigated()
        }

        is NavigationCommand.NavigateTo -> {
            navController.navigate(finalCommand.route, finalCommand.navOptions)
            navigator.onNavigated()
        }

        NavigationCommand.Idle -> {}
    }
}
