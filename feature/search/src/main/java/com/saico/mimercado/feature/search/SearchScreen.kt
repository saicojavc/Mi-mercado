package com.saico.mimercado.feature.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.saico.mimercado.core.ui.components.AddToCartButton
import com.saico.mimercado.core.ui.components.CategoryFilter
import com.saico.mimercado.core.ui.components.ProductCard
import com.saico.mimercado.core.ui.navigation.Navigator
import com.saico.mimercado.core.ui.navigation.NavigationCommand
import com.saico.mimercado.core.ui.navigation.routes.products.CreateCustomProductRoute
import com.saico.mimercado.core.ui.theme.PrimaryCyan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navigator: Navigator,
    onProductClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        SearchBar(
            query = uiState.searchQuery,
            onQueryChange = { viewModel.onEvent(SearchUiEvent.QueryChanged(it)) },
            onSearch = { /* La búsqueda es reactiva por debounce */ },
            active = false,
            onActiveChange = { },
            placeholder = { Text("Buscar productos...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = SearchBarDefaults.colors(
                containerColor = Color(0xFFF3F3F5)
            )
        ) { }

        CategoryFilter(
            categories = uiState.categories,
            selectedCategory = uiState.selectedCategory,
            onCategorySelected = { viewModel.onEvent(SearchUiEvent.CategorySelected(it)) }
        )

        if (uiState.isLoading && uiState.products.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryCyan)
            }
        } else if (uiState.error != null && uiState.products.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = uiState.error ?: "Error desconocido", color = Color.Red)
            }
        } else if (uiState.products.isEmpty() && !uiState.isLoading) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "No se encontraron productos", color = Color.Gray)
                Spacer(Modifier.height(16.dp))
                Button(onClick = { 
                    navigator.navigate(NavigationCommand.NavigateTo(CreateCustomProductRoute(prefillName = uiState.searchQuery.ifBlank { null })))
                }) {
                    Text("Añadir producto manualmente")
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(uiState.products, key = { it.product.id }) { decorated ->
                    ProductCard(
                        product = decorated.product,
                        onClick = { onProductClick(decorated.product.id) },
                        additionalBrandsCount = decorated.additionalBrandsCount,
                        onGetCachedUrl = { viewModel.imageCache.getVerifiedUrl(it) },
                        onSaveCachedUrl = { key, url -> viewModel.imageCache.saveVerifiedUrl(key, url) },
                        trailingContent = {
                            AddToCartButton(
                                onClick = { viewModel.onEvent(SearchUiEvent.AddToCart(decorated.product)) }
                            )
                        }
                    )
                }
            }
        }
    }
}
