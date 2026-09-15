package com.saico.mimercado.core.ui.navigation.routes.products

import com.saico.mimercado.core.ui.navigation.routes.Route
import kotlinx.serialization.Serializable

@Serializable
data object ProductsRoute : Route {
    override val route: String = "products"
}

@Serializable
data class ProductDetailsRoute(val fdcId: String) : Route {
    override val route: String = "product_details/$fdcId"
}

@Serializable
data class CreateCustomProductRoute(val prefillName: String? = null) : Route {
    override val route: String = "create_custom_product"
}

