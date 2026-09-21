package com.saico.mimercado.core.ui.navigation.routes.auth

import com.saico.mimercado.core.ui.navigation.routes.Route
import kotlinx.serialization.Serializable

@Serializable
object SignInRoute : Route {
    override val route: String = "sign_in"
}
