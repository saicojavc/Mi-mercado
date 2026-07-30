package com.saico.mimercado.core.ui.navigation

import androidx.navigation.NavOptions
import com.saico.mimercado.core.ui.navigation.routes.Route

sealed interface NavigationCommand {
    data object Idle : NavigationCommand
    data object PopBackstack : NavigationCommand
    data class PopBackstackUntil(
        val route: Route,
        val inclusive: Boolean = false,
    ) : NavigationCommand
    data class NavigateTo(
        val route: Route,
        val navOptions: NavOptions? = null,
    ) : NavigationCommand
}
