package com.saico.mimercado.core.ui.navigation

import kotlinx.coroutines.flow.StateFlow

interface Navigator {
    val commands: StateFlow<NavigationCommand>
    fun navigate(command: NavigationCommand)
    fun onNavigated()
}
