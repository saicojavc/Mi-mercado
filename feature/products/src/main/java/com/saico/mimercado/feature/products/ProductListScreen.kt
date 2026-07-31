package com.saico.mimercado.feature.products

import android.Manifest
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.saico.mimercado.core.model.Product
import com.saico.mimercado.core.ui.components.CategoryFilter
import com.saico.mimercado.core.ui.components.ProductRow
import com.saico.mimercado.core.ui.theme.AppBackground
import com.saico.mimercado.core.ui.theme.PrimaryCyan
import com.saico.mimercado.core.ui.theme.TextDark
import com.saico.mimercado.feature.products.components.BarcodeScannerView
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun ProductListScreen(
    viewModel: ProductListViewModel,
    totalCartItems: Int,
    errorMessages: SharedFlow<String>,
    onAddToCart: (Product) -> Unit,
    onProductClick: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedStore by viewModel.selectedStore.collectAsState()
    val products by viewModel.filteredProducts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isPaginating by viewModel.isPaginating.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val categories = viewModel.categories
    val stores = viewModel.stores

    var showStoreFilters by remember { mutableStateOf(false) }
    var showScanner by remember { mutableStateOf(false) }
    
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    val context = LocalContext.current
    val listState = rememberLazyListState()

    val shouldLoadMore = remember {
        derivedStateOf {
            val totalItemsCount = listState.layoutInfo.totalItemsCount
            val lastVisibleItemIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisibleItemIndex >= totalItemsCount - 5 && totalItemsCount > 0
        }
    }

    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value) {
            viewModel.loadNextPage()
        }
    }

    var previousTotalItems by remember { mutableIntStateOf(totalCartItems) }
    val badgeScale = remember { Animatable(1f) }

    LaunchedEffect(totalCartItems) {
        if (totalCartItems > previousTotalItems) {
            badgeScale.animateTo(1.5f, animationSpec = tween(100))
            badgeScale.animateTo(1f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        }
        previousTotalItems = totalCartItems
    }

    LaunchedEffect(errorMessages) {
        errorMessages.collectLatest { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

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
                onClick = { viewModel.navigateToCart() },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                BadgedBox(
                    badge = {
                        if (totalCartItems > 0) {
                            Badge(
                                modifier = Modifier.scale(badgeScale.value),
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.onError
                            ) {
                                Text(totalCartItems.toString())
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
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.onSearchQueryChanged(it) },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Search products or UPC...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = { 
                            if (cameraPermissionState.status.isGranted) {
                                showScanner = true
                            } else {
                                cameraPermissionState.launchPermissionRequest()
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = "Scan Barcode",
                                tint = PrimaryCyan
                            )
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.LightGray,
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White
                    )
                )
                
                IconButton(
                    onClick = { showStoreFilters = !showStoreFilters },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "Filter by Store",
                        tint = if (selectedStore != null) MaterialTheme.colorScheme.primary else Color.Gray
                    )
                }
            }

            AnimatedVisibility(visible = showStoreFilters || selectedStore != null) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    items(stores) { store ->
                        FilterChip(
                            selected = selectedStore == store,
                            onClick = { viewModel.onStoreSelected(store) },
                            label = { Text(store) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }
            }

            CategoryFilter(
                categories = categories,
                selectedCategory = selectedCategory,
                onCategorySelected = { viewModel.selectCategory(it) }
            )
            
            Box(modifier = Modifier.fillMaxSize()) {
                if (isLoading && products.isEmpty()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(products) { product ->
                            ProductRow(
                                product = product,
                                onAddClick = { onAddToCart(product) },
                                onClick = { onProductClick(product) }
                            )
                        }
                        
                        if (isPaginating) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(modifier = Modifier.size(32.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showScanner) {
        Dialog(
            onDismissRequest = { showScanner = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
                BarcodeScannerView(
                    onBarcodeScanned = { barcode ->
                        showScanner = false
                        viewModel.onSearchQueryChanged(barcode)
                    }
                )
                
                IconButton(
                    onClick = { showScanner = false },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.5f))
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }
        }
    }
}
