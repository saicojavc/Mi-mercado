package com.saico.mimercado.core.ui.navigation.routes.lists

import com.saico.mimercado.core.ui.navigation.routes.Route
import kotlinx.serialization.Serializable

@Serializable
data object ShoppingListsRoute : Route {
    override val route: String = "shopping_lists"
}
