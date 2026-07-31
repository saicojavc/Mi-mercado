package com.saico.mimercado.feature.products

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.saico.mimercado.core.common.UsdaImageResolver
import com.saico.mimercado.core.model.Product
import com.saico.mimercado.core.model.ProductDetails
import com.saico.mimercado.core.ui.theme.AppBackground
import com.saico.mimercado.core.ui.theme.PrimaryCyan
import com.saico.mimercado.core.ui.theme.SecondaryTeal
import com.saico.mimercado.core.ui.theme.TextDark

@Composable
fun ProductDetailsScreen(
    viewModel: ProductDetailsViewModel,
    onAddToCart: (Product) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = AppBackground,
        bottomBar = {
            if (uiState is ProductDetailsUiState.Success) {
                val details = (uiState as ProductDetailsUiState.Success).details
                BottomAppBar(
                    containerColor = Color.White,
                    tonalElevation = 8.dp,
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Button(
                        onClick = {
                            onAddToCart(
                                Product(
                                    id = details.id,
                                    upc = details.upc,
                                    nombre = details.name,
                                    categoria = details.category,
                                    imageUrl = details.imageUrl,
                                    brands = details.brand
                                )
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Add to Cart", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier.padding(innerPadding).fillMaxSize(),
            color = AppBackground
        ) {
            when (val state = uiState) {
                is ProductDetailsUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = PrimaryCyan)
                    }
                }
                is ProductDetailsUiState.Success -> {
                    ProductDetailsContent(details = state.details)
                }
                is ProductDetailsUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = state.message, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductDetailsContent(details: ProductDetails) {
    
    val candidateUrls = remember(details.id) {
        val list = mutableListOf<String>()
        if (details.imageUrl.isNotBlank()) list.add(details.imageUrl)
        list.add(UsdaImageResolver.getSearchThumbnailUrl(details.brand, details.name))
        
        val upc = details.upc.filter { it.isDigit() }
        if (upc.isNotEmpty()) {
            val upc12 = upc.padStart(12, '0').takeLast(12)
            list.add("https://i5.walmartimages.com/asr/$upc12.jpg")
            list.add("https://target.scene7.com/is/image/Target/GUEST_$upc12?wid=400&hei=400&fmt=pjpeg")
            list.add(UsdaImageResolver.buildOffUrl(upc))
        }
        list
    }
    
    var urlIndex by remember(details.id) { mutableIntStateOf(0) }
    val currentUrl = candidateUrls.getOrNull(urlIndex) ?: ""

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Aesthetic Image Header
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                if (currentUrl.isNotEmpty()) {
                    AsyncImage(
                        model = currentUrl,
                        contentDescription = details.name,
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        contentScale = ContentScale.Fit,
                        onError = {
                            if (urlIndex < candidateUrls.size - 1) {
                                urlIndex++
                            }
                        }
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.size(100.dp),
                        tint = Color(0xFFEEEEEE)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Title & Brand
        Text(
            text = details.name,
            style = MaterialTheme.typography.headlineSmall,
            color = TextDark,
            fontWeight = FontWeight.ExtraBold,
            lineHeight = 32.sp
        )
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = details.brand,
                style = MaterialTheme.typography.titleMedium,
                color = SecondaryTeal,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = " • ",
                color = Color.Gray
            )
            Text(
                text = details.category,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Info Sections
        InfoSection(
            title = "Description & Ingredients",
            icon = Icons.Default.List,
            content = details.ingredients
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Nutrition Card
        Text(
            text = "Nutritional Information",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                details.nutrients.forEach { (name, value) ->
                    NutrientRow(name = name, value = value)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun InfoSection(title: String, icon: ImageVector, content: String) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = PrimaryCyan)
            Spacer(Modifier.width(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium,
            color = TextDark.copy(alpha = 0.8f),
            lineHeight = 22.sp
        )
    }
}

@Composable
fun NutrientRow(name: String, value: Double) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = name, color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
        Text(
            text = if (name == "Calories") value.toInt().toString() else "${value}g",
            fontWeight = FontWeight.Bold,
            color = TextDark
        )
    }
    HorizontalDivider(color = Color(0xFFF0F0F0))
}
