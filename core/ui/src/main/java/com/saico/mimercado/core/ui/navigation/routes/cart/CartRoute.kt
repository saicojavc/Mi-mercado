package com.saico.mimercado.core.ui.navigation.routes.cart

import com.saico.mimercado.core.ui.navigation.routes.Route
import kotlinx.serialization.Serializable

@Serializable
data object CartRoute : Route {
    override val route: String = "cart"
}
