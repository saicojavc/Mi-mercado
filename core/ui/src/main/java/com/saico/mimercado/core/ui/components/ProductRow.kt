package com.saico.mimercado.core.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.saico.mimercado.core.common.UsdaImageResolver
import com.saico.mimercado.core.model.Product
import com.saico.mimercado.core.ui.R
import com.saico.mimercado.core.ui.theme.PrimaryCyan
import com.saico.mimercado.core.ui.theme.SecondaryTeal
import com.saico.mimercado.core.ui.theme.TextDark

@Composable
fun ProductRow(
    product: Product,
    onAddClick: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    val upc = remember(product.upc) { product.upc.filter { it.isDigit() } }
    
    val candidateUrls = remember(upc, product.imageUrl, product.nombre) {
        val list = mutableListOf<String>()
        
        // 1. Precise Enriched URL (OFF API)
        if (product.imageUrl.isNotBlank()) list.add(product.imageUrl)
        
        // 2. Bing/Google Search Thumbnail (The "Golden" Fallback)
        list.add(UsdaImageResolver.getSearchThumbnailUrl(product.brands, product.nombre))
        
        if (upc.isNotEmpty()) {
            val upc12 = upc.padStart(12, '0').takeLast(12)
            // 3. Retailer CDNs
            list.add("https://i5.walmartimages.com/asr/$upc12.jpg")
            list.add("https://target.scene7.com/is/image/Target/GUEST_$upc12?wid=400&hei=400&fmt=pjpeg")
            list.add(UsdaImageResolver.buildOffUrl(upc))
        }
        list
    }
    
    var urlIndex by remember(product.id) { mutableIntStateOf(0) }
    val currentUrl = candidateUrls.getOrNull(urlIndex) ?: ""

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(72.dp),
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFF3F3F5)
            ) {
                if (currentUrl.isNotEmpty()) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(currentUrl)
                            .crossfade(true)
                            .diskCachePolicy(CachePolicy.ENABLED)
                            .setHeader("User-Agent", "Mozilla/5.0")
                            .build(),
                        contentDescription = product.nombre,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit,
                        onError = {
                            if (urlIndex < candidateUrls.size - 1) {
                                urlIndex++
                            }
                        },
                        error = rememberVectorPainter(Icons.Default.ShoppingCart),
                        placeholder = rememberVectorPainter(Icons.Default.ShoppingCart)
                    )
                } else {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = null,
                            tint = Color.LightGray,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextDark,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2
                )
                if (product.brands.isNotEmpty()) {
                    Text(
                        text = product.brands,
                        style = MaterialTheme.typography.bodySmall,
                        color = SecondaryTeal
                    )
                }
                Text(
                    text = product.categoria,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }

            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()
            val scale by animateFloatAsState(
                targetValue = if (isPressed) 0.95f else 1f,
                label = "scale"
            )

            Box(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(44.dp)
                    .scale(scale)
                    .clip(CircleShape)
                    .background(PrimaryCyan)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onAddClick
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_to_cart),
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
