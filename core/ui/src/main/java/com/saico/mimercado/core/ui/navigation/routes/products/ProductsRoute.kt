package com.saico.mimercado.core.ui.navigation.routes.products

import com.saico.mimercado.core.ui.navigation.routes.Route
import kotlinx.serialization.Serializable

@Serializable
data object ProductsRoute : Route {
    override val route: String = "products"
}
