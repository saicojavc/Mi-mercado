package com.saico.mimercado.feature.auth.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.saico.mimercado.core.ui.navigation.routes.auth.SignInRoute
import com.saico.mimercado.feature.auth.SignInScreen
import com.saico.mimercado.feature.auth.SignInViewModel

fun NavGraphBuilder.authGraph(
    onSignInSuccess: () -> Unit,
    fcmManager: com.saico.mimercado.core.network.fcm.FCMRegistrationManager
) {
    composable<SignInRoute> {
        val viewModel: SignInViewModel = hiltViewModel()
        SignInScreen(
            viewModel = viewModel,
            onSignInSuccess = {
                fcmManager.registerDeviceToken() // Registramos el token solo tras el login
                onSignInSuccess()
            }
        )
    }
}
