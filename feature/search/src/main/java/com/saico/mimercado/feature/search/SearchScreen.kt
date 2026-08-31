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
import com.saico.mimercado.core.ui.components.CategoryFilter
import com.saico.mimercado.core.ui.components.ProductRow
import com.saico.mimercado.core.ui.theme.PrimaryCyan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
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
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(uiState.products, key = { it.id }) { product ->
                    ProductRow(
                        product = product,
                        onAddClick = { viewModel.onEvent(SearchUiEvent.AddToCart(product)) },
                        onClick = { onProductClick(product.id) }
                    )
                }
            }
        }
    }
}
