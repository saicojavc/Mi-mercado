package com.saico.mimercado.core.ui.navigation.routes.lists

import com.saico.mimercado.core.ui.navigation.routes.Route
import kotlinx.serialization.Serializable

@Serializable
data class ShoppingListDetailRoute(
    val listId: String
) : Route {
    override val route: String = "shopping_list_detail/$listId"
}
