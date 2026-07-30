package com.saico.mimercado.core.ui.navigation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ComposeNavigator @Inject constructor() : Navigator {

    private val commandFlow: MutableStateFlow<NavigationCommand> =
        MutableStateFlow(NavigationCommand.Idle)

    override val commands
        get() = commandFlow.asStateFlow()

    override fun navigate(command: NavigationCommand) {
        commandFlow.update { command }
    }

    override fun onNavigated() {
        commandFlow.update { NavigationCommand.Idle }
    }
}
