package com.saico.mimercado.core.ui.navigation

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class NavigationModule {

    @Binds
    internal abstract fun bindNavigator(impl: ComposeNavigator): Navigator
}
