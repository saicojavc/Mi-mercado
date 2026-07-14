package com.saico.mimercado.ui.screens

import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.saico.mimercado.R
import com.saico.mimercado.ui.components.CategoryFilter
import com.saico.mimercado.ui.components.ProductRow
import com.saico.mimercado.ui.theme.AppBackground
import com.saico.mimercado.ui.theme.MiMercadoTheme
import com.saico.mimercado.ui.theme.TextDark
import com.saico.mimercado.ui.viewmodel.CartViewModel
import com.saico.mimercado.ui.viewmodel.SimpleProductListViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    productListViewModel: SimpleProductListViewModel = viewModel(),
    cartViewModel: CartViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val selectedCategory by productListViewModel.selectedCategory.collectAsState()
    val products by productListViewModel.filteredProducts.collectAsState()
    val categories = productListViewModel.categories

    val cartItems by cartViewModel.cartItems.collectAsState()
    val totalItems = cartItems.sumOf { it.cantidad }

    val context = LocalContext.current

    var previousTotalItems by remember { mutableIntStateOf(totalItems) }
    val badgeScale = remember { Animatable(1f) }

    LaunchedEffect(totalItems) {
        if (totalItems > previousTotalItems) {
            badgeScale.animateTo(1.5f, animationSpec = tween(100))
            badgeScale.animateTo(1f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        }
        previousTotalItems = totalItems
    }

    LaunchedEffect(cartViewModel.errorMessages) {
        cartViewModel.errorMessages.collectLatest { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = AppBackground,
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = stringResource(R.string.app_name),
                        fontWeight = FontWeight.Bold
                    ) 
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppBackground,
                    titleContentColor = TextDark
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showBottomSheet = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                BadgedBox(
                    badge = {
                        if (totalItems > 0) {
                            Badge(
                                modifier = Modifier.scale(badgeScale.value),
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.onError
                            ) {
                                Text(totalItems.toString())
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = stringResource(R.string.view_cart)
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            CategoryFilter(
                categories = categories,
                selectedCategory = selectedCategory,
                onCategorySelected = { productListViewModel.selectCategory(it) }
            )
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(products) { product ->
                    ProductRow(
                        product = product,
                        onAddClick = { cartViewModel.addToCart(product) }
                    )
                }
            }
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                CartScreen(viewModel = cartViewModel)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProductListScreenPreview() {
    MiMercadoTheme {
        ProductListScreen()
    }
}
