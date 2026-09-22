package com.saico.mimercado.core.ui.navigation.routes.profile

import com.saico.mimercado.core.ui.navigation.routes.Route
import kotlinx.serialization.Serializable

@Serializable
data object ProfileRoute : Route {
    override val route: String = "profile"
}
