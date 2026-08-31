package com.saico.mimercado.core.ui.navigation.routes.search

import com.saico.mimercado.core.ui.navigation.routes.Route
import kotlinx.serialization.Serializable

@Serializable
data object SearchRoute : Route {
    override val route: String = "search"
}
