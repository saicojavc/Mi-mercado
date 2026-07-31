package com.saico.mimercado

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.saico.mimercado.core.network.fcm.FCMRegistrationManager
import com.saico.mimercado.core.ui.navigation.Navigator
import com.saico.mimercado.core.ui.navigation.NavigatorHandler
import com.saico.mimercado.core.ui.navigation.routes.Route
import com.saico.mimercado.core.ui.theme.MiMercadoTheme
import com.saico.mimercado.feature.cart.CartViewModel
import com.saico.mimercado.feature.cart.navigation.cartGraph
import com.saico.mimercado.feature.products.navigation.productsGraph
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    @Inject lateinit var navigator: Navigator
    @Inject lateinit var fcmManager: FCMRegistrationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Dynamic permission check on Android 13+
        if (Build.VERSION.SDK_INT >= 33) {
            val requestPermissionLauncher = registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    Log.d("MainActivity", "✅ Notification permission granted")
                } else {
                    Log.w("MainActivity", "❌ Notification permission denied")
                }
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        // Initialize device token registration on startup
        fcmManager.registerDeviceToken()

        setContent {
            MiMercadoTheme(darkTheme = viewModel.isDarkMode.value) {
                val navController = rememberNavController()
                NavigatorHandler(navigator = navigator, navController = navController)

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainContainer(
                        navController = navController,
                        startDestination = viewModel.firstScreen,
                        navigator = navigator
                    )
                }
            }
        }
    }
}

@Composable
private fun MainContainer(
    navController: NavHostController,
    startDestination: Route,
    navigator: Navigator
) {
    val cartViewModel: CartViewModel = hiltViewModel()
    val cartItems by cartViewModel.cartItems.collectAsState()
    val totalItems = cartItems.sumOf { it.cantidad }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        productsGraph(
            totalCartItems = totalItems,
            errorMessages = cartViewModel.errorMessages,
            onAddToCart = { cartViewModel.addToCart(it) },
            navigator = navigator
        )
        cartGraph()
    }
}
