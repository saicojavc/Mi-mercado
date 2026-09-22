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
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.saico.mimercado.core.network.fcm.FCMRegistrationManager
import com.saico.mimercado.core.ui.navigation.NavigationCommand
import com.saico.mimercado.core.ui.navigation.Navigator
import com.saico.mimercado.core.ui.navigation.NavigatorHandler
import com.saico.mimercado.core.ui.navigation.routes.Route
import com.saico.mimercado.core.ui.navigation.routes.lists.ShoppingListsRoute
import com.saico.mimercado.core.ui.navigation.routes.products.ProductsRoute
import com.saico.mimercado.core.ui.navigation.routes.profile.ProfileRoute
import com.saico.mimercado.core.ui.theme.MiMercadoTheme
import com.saico.mimercado.feature.auth.navigation.authGraph
import com.saico.mimercado.feature.cart.CartViewModel
import com.saico.mimercado.feature.cart.navigation.cartGraph
import com.saico.mimercado.feature.customproduct.navigation.customProductGraph
import com.saico.mimercado.feature.lists.navigation.listsGraph
import com.saico.mimercado.feature.products.navigation.productsGraph
import com.saico.mimercado.feature.search.navigation.searchGraph
import com.saico.mimercado.feature.settings.navigation.settingsGraph
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

        checkGooglePlayServices()

        setContent {
            MiMercadoTheme(darkTheme = viewModel.isDarkMode.value) {
                val navController = rememberNavController()
                NavigatorHandler(navigator = navigator, navController = navController)

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val startDestination by viewModel.startDestination.collectAsState()

                    LaunchedEffect(startDestination) {
                        startDestination?.let { destination ->
                            navController.navigate(destination) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    }

                    if (startDestination != null) {
                        MainContainer(
                            navController = navController,
                            startDestination = startDestination!!,
                            navigator = navigator
                        )
                    }
                }
            }
        }
    }

    private fun checkGooglePlayServices() {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(this)
        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(resultCode)) {
                googleApiAvailability.getErrorDialog(this, resultCode, 9000)?.show()
            } else {
                Log.e("MainActivity", "❌ This device is not supported for Google Play Services")
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

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = currentDestination?.route ?: ""

    val showBottomNav = currentRoute.contains("ShoppingListsRoute", ignoreCase = true) ||
            currentRoute.contains("ProfileRoute", ignoreCase = true) ||
            currentRoute.contains("ProductsRoute", ignoreCase = true) ||
            currentRoute.contains("shopping_lists", ignoreCase = true) ||
            currentRoute.contains("profile", ignoreCase = true) ||
            currentRoute.contains("products", ignoreCase = true)

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomNav,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(28.dp),
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 6.dp,
                        shadowElevation = 8.dp
                    ) {
                        NavigationBar(
                            containerColor = Color.Transparent,
                            contentColor = MaterialTheme.colorScheme.onSurface,
                            tonalElevation = 0.dp,
                            modifier = Modifier
                                .height(72.dp)
                                .padding(top = 4.dp, bottom = 4.dp)
                        ) {
                            val isListsSelected = currentRoute.contains("lists", ignoreCase = true)
                            val isCatalogSelected = currentRoute.contains("products", ignoreCase = true) && !isListsSelected
                            val isProfileSelected = currentRoute.contains("profile", ignoreCase = true)

                            NavigationBarItem(
                                selected = isListsSelected,
                                onClick = {
                                    navigator.navigate(NavigationCommand.NavigateTo(ShoppingListsRoute))
                                },
                                icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Listas") },
                                label = { Text("Mis Listas", fontWeight = if (isListsSelected) FontWeight.Bold else FontWeight.Normal) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.secondary,
                                    selectedTextColor = MaterialTheme.colorScheme.secondary,
                                    indicatorColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            )

                            NavigationBarItem(
                                selected = isCatalogSelected,
                                onClick = {
                                    navigator.navigate(NavigationCommand.NavigateTo(ProductsRoute))
                                },
                                icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Catálogo") },
                                label = { Text("Catálogo", fontWeight = if (isCatalogSelected) FontWeight.Bold else FontWeight.Normal) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.secondary,
                                    selectedTextColor = MaterialTheme.colorScheme.secondary,
                                    indicatorColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            )

                            NavigationBarItem(
                                selected = isProfileSelected,
                                onClick = {
                                    navigator.navigate(NavigationCommand.NavigateTo(ProfileRoute))
                                },
                                icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Perfil") },
                                label = { Text("Perfil", fontWeight = if (isProfileSelected) FontWeight.Bold else FontWeight.Normal) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.secondary,
                                    selectedTextColor = MaterialTheme.colorScheme.secondary,
                                    indicatorColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            authGraph(
                onSignInSuccess = {},
                fcmManager = (navController.context as MainActivity).fcmManager
            )
            listsGraph(navigator = navigator)
            customProductGraph(navigator = navigator)
            productsGraph(
                totalCartItems = 0,
                errorMessages = cartViewModel.errorMessages,
                onAddToCart = { cartViewModel.addToCart(it) },
                navigator = navigator
            )
            searchGraph(navigator = navigator)
            settingsGraph(navigator = navigator)
            cartGraph()
        }
    }
}
