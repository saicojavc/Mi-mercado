package com.saico.mimercado.feature.lists

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saico.mimercado.core.common.CategoryMapper
import com.saico.mimercado.core.model.CartItem
import com.saico.mimercado.core.model.Product
import com.saico.mimercado.core.ui.components.AddToCartButton
import com.saico.mimercado.core.ui.components.AppToast
import com.saico.mimercado.core.ui.components.ProductCard
import com.saico.mimercado.core.ui.util.AvatarUtils

@Composable
fun ListItemRow(
    item: CartItem,
    onQuantityChange: (Int) -> Unit,
    onProductClick: () -> Unit = {}
) {
    val product = remember(item) { item.toProduct() }

    ProductCard(
        product = product,
        onClick = onProductClick,
        avatarBadge = {
            item.addedByAvatar?.let { avatar ->
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                    modifier = Modifier.size(20.dp).align(Alignment.TopStart)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(AvatarUtils.getAvatarEmoji(avatar), fontSize = 10.sp)
                    }
                }
            }
        },
        trailingContent = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(end = 8.dp)
            ) {
                IconButton(onClick = { onQuantityChange(item.cantidad - 1) }, modifier = Modifier.size(32.dp)) {
                    Icon(if (item.cantidad == 1) Icons.Default.Delete else Icons.Default.Remove, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                }
                Text(
                    text = "${item.cantidad}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = { onQuantityChange(item.cantidad + 1) }, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListDetailScreen(
    viewModel: ShoppingListDetailViewModel,
    onBackClick: () -> Unit,
    onSearchClick: () -> Unit,
    onScanBarcodeClick: () -> Unit,
    onProductClick: (Product) -> Unit = {},
    onCreateCustomProductClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var showMenu by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf(false) }
    var renameInput by remember { mutableStateOf("") }

    val filteredFavorites = remember(uiState.favorites, uiState.selectedFavoriteCategory) {
        val selectedCat = uiState.selectedFavoriteCategory
        if (selectedCat == null) {
            emptyList()
        } else if (selectedCat == "Todos") {
            uiState.favorites
        } else {
            uiState.favorites.filter { fav ->
                val normalized = CategoryMapper.getNormalizedCategory(fav.categoria, fav.nombre)
                normalized.contains(selectedCat, ignoreCase = true) ||
                        fav.categoria.contains(selectedCat, ignoreCase = true)
            }
        }
    }

    if (showRenameDialog) {
        AlertDialog(
            onDismissRequest = { showRenameDialog = false },
            title = { Text("Renombrar lista") },
            text = {
                OutlinedTextField(
                    value = renameInput,
                    onValueChange = { renameInput = it },
                    label = { Text("Nuevo nombre") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.renameList(renameInput)
                        showRenameDialog = false
                    },
                    enabled = renameInput.isNotBlank()
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRenameDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = uiState.list?.name ?: "Lista",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Text(
                            text = "${uiState.items.sumOf { it.cantidad }} productos en lista",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                },
                actions = {
                    IconButton(onClick = onScanBarcodeClick) {
                        Icon(Icons.Default.QrCodeScanner, contentDescription = "Escanear")
                    }
                    IconButton(onClick = onSearchClick) {
                        Icon(Icons.Default.Search, contentDescription = "Buscar")
                    }
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Opciones")
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Renombrar lista") },
                                onClick = {
                                    showMenu = false
                                    renameInput = uiState.list?.name ?: ""
                                    showRenameDialog = true
                                },
                                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) }
                            )
                            if (uiState.list?.type == com.saico.mimercado.core.model.ShoppingListType.CUSTOM) {
                                DropdownMenuItem(
                                    text = { Text("Eliminar lista", color = MaterialTheme.colorScheme.error) },
                                    onClick = {
                                        showMenu = false
                                        viewModel.deleteList(onDeleted = onBackClick)
                                    },
                                    leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) }
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            // Persistent Input "Necesito..." + + Button
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = uiState.quickAddText,
                        onValueChange = viewModel::onQuickAddTextChanged,
                        placeholder = { Text("Necesito...", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.secondary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        )
                    )

                    FloatingActionButton(
                        onClick = { viewModel.confirmQuickAdd() },
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary,
                        shape = CircleShape,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Agregar rápido")
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (uiState.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // SECTION 1: ITEMS IN LIST (LO QUE NECESITO COMPRAR)
                    item {
                        Text(
                            "En tu lista (${uiState.items.sumOf { it.cantidad }})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    if (uiState.items.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            ) {
                                Column(
                                    modifier = Modifier.padding(20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        Icons.Default.ShoppingCart,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(40.dp)
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        "Tu lista está vacía",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        "Escribe abajo o toca una categoría para agregar productos.",
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    } else {
                        items(uiState.items, key = { it.itemId }) { item ->
                            ListItemRow(
                                item = item,
                                onQuantityChange = { newQty -> viewModel.updateQuantity(item.itemId, newQty) },
                                onProductClick = { onProductClick(item.toProduct()) }
                            )
                        }
                    }

                    // SECTION 2: EXPLORATION TABS (FAVORITOS VS CATALOGO)
                    item {
                        HorizontalDivider(
                            modifier = Modifier.padding(top = 8.dp, bottom = 12.dp),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )

                        Text(
                            "Agregar Productos",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Spacer(Modifier.height(8.dp))

                        // Segmented Tabs: [❤️ Mis Favoritos] | [📦 Catálogo Completo]
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = uiState.activeTab == ExplorationTab.FAVORITES,
                                onClick = { viewModel.selectExplorationTab(ExplorationTab.FAVORITES) },
                                label = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.Favorite,
                                            contentDescription = null,
                                            tint = if (uiState.activeTab == ExplorationTab.FAVORITES) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(Modifier.width(6.dp))
                                        Text("Mis Favoritos (${uiState.favorites.size})", fontWeight = FontWeight.Bold)
                                    }
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.secondary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onSecondary,
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier.weight(1f)
                            )

                            FilterChip(
                                selected = uiState.activeTab == ExplorationTab.CATALOG,
                                onClick = { viewModel.selectExplorationTab(ExplorationTab.CATALOG) },
                                label = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.GridView,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(Modifier.width(6.dp))
                                        Text("Catálogo", fontWeight = FontWeight.Bold)
                                    }
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.secondary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onSecondary,
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // TAB 1: MIS FAVORITOS
                    if (uiState.activeTab == ExplorationTab.FAVORITES) {
                        item {
                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(favoriteCategoryFilters) { category ->
                                    val isSelected = uiState.selectedFavoriteCategory == category
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { viewModel.selectFavoriteCategory(category) },
                                        label = { Text(category, fontSize = 13.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                                        ),
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                }
                            }
                        }

                        val favCategory = uiState.selectedFavoriteCategory
                        if (favCategory == null) {
                            item {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 8.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.TouchApp,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(Modifier.width(10.dp))
                                        Text(
                                            "Toca una categoría arriba para ver tus productos favoritos.",
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        } else if (filteredFavorites.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        if (uiState.favorites.isEmpty())
                                            "Aún no tienes productos en tus Favoritos. Explora el Catálogo para guardar tus preferidos."
                                        else
                                            "No tienes productos favoritos en la categoría $favCategory.",
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        } else {
                            items(filteredFavorites, key = { "fav_${it.id}" }) { product ->
                                ProductCard(
                                    product = product,
                                    onClick = { onProductClick(product) },
                                    trailingContent = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            IconButton(onClick = { viewModel.toggleFavorite(product) }) {
                                                Icon(
                                                    Icons.Default.Favorite,
                                                    contentDescription = "Quitar de favoritos",
                                                    tint = MaterialTheme.colorScheme.error
                                                )
                                            }
                                            AddToCartButton(onClick = { viewModel.addProductToList(product) })
                                        }
                                    }
                                )
                            }
                        }

                        // CREATE CUSTOM PRODUCT BUTTON FOR FAVORITES
                        item {
                            Spacer(Modifier.height(8.dp))
                            OutlinedButton(
                                onClick = onCreateCustomProductClick,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Crear producto personalizado")
                            }
                        }
                    }

                    // TAB 2: CATÁLOGO COMPLETO POR CATEGORÍA
                    if (uiState.activeTab == ExplorationTab.CATALOG) {
                        item {
                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(catalogCategories) { category ->
                                    val isSelected = uiState.selectedCatalogCategory == category
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { viewModel.selectCatalogCategory(category) },
                                        label = { Text(category, fontSize = 13.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                                        ),
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                }
                            }
                        }

                        val selectedCat = uiState.selectedCatalogCategory
                        if (selectedCat == null) {
                            item {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 8.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.TouchApp,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(Modifier.width(10.dp))
                                        Text(
                                            "Toca una categoría arriba para ver productos del catálogo.",
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        } else {
                            val products = uiState.categoryProductsMap[selectedCat] ?: emptyList()

                            if (uiState.isLoadingCategoryProducts && products.isEmpty()) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(24.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(modifier = Modifier.size(28.dp))
                                    }
                                }
                            } else if (products.isEmpty()) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(24.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            "No se encontraron productos en $selectedCat",
                                            fontSize = 14.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            } else {
                                items(products, key = { "cat_${selectedCat}_${it.id}" }) { product ->
                                    val isFav = uiState.favorites.any { it.id == product.id }
                                    ProductCard(
                                        product = product,
                                        onClick = { onProductClick(product) },
                                        trailingContent = {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                IconButton(onClick = { viewModel.toggleFavorite(product) }) {
                                                    Icon(
                                                        imageVector = if (isFav) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                                                        contentDescription = if (isFav) "Quitar de favoritos" else "Guardar en favoritos",
                                                        tint = if (isFav) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                                AddToCartButton(onClick = { viewModel.addProductToList(product) })
                                            }
                                        }
                                    )
                                }
                            }
                        }

                        // CREATE CUSTOM PRODUCT BUTTON
                        item {
                            Spacer(Modifier.height(8.dp))
                            OutlinedButton(
                                onClick = onCreateCustomProductClick,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Crear producto personalizado")
                            }
                        }
                    }
                }
            }

            AppToast(
                message = uiState.toastMessage,
                onDismiss = viewModel::clearToast,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 72.dp)
            )
        }
    }
}
