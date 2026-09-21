package com.saico.mimercado.core.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saico.mimercado.core.common.CategoryMapper
import com.saico.mimercado.core.common.UsdaImageResolver
import com.saico.mimercado.core.model.Product
import com.saico.mimercado.core.ui.R
import com.saico.mimercado.core.ui.theme.getCategoryColor

@Composable
fun ProductCard(
    product: Product,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    trailingContent: @Composable (RowScope.() -> Unit)? = null,
    avatarBadge: @Composable (BoxScope.() -> Unit)? = null,
    subContent: @Composable (ColumnScope.() -> Unit)? = null,
    additionalBrandsCount: Int = 0,
    onGetCachedUrl: ((String) -> String?)? = null,
    onSaveCachedUrl: ((String, String) -> Unit)? = null
) {
    val upc = remember(product.upc) { product.upc.filter { it.isDigit() } }
    val cacheKey = remember(product.id, product.upc) { product.upc.ifBlank { product.id } }
    
    val candidateUrls = remember(upc, product.imageUrl, product.nombre) {
        val list = mutableListOf<String>()
        
        // 0. PRIORIDAD MÁXIMA: Caché verificada (Instantáneo)
        onGetCachedUrl?.invoke(cacheKey)?.let { cached ->
            if (cached.isNotBlank()) list.add(cached)
        }

        // 1. URL de la API (Si viene de Discover con imagen)
        if (product.imageUrl.isNotBlank()) list.add(product.imageUrl)
        
        // 2. Búsqueda Rápida (Bing) - Es el "Salvavidas" que carga en milisegundos
        list.add(UsdaImageResolver.getSearchThumbnailUrl(product.brands, product.nombre))
        
        // 3. Intentos de Alta Calidad (Solo si los anteriores no existen o fallan)
        if (upc.isNotEmpty()) {
            list.add(UsdaImageResolver.buildWalmartUrl(upc))
            list.add(UsdaImageResolver.buildOffUrl(upc))
        }
        
        list.distinct().filter { it.isNotBlank() } 
    }

    val normalizedCategory = remember(product.categoria, product.nombre) {
        CategoryMapper.getNormalizedCategory(product.categoria, product.nombre)
    }
    val categoryColor = remember(normalizedCategory) { getCategoryColor(normalizedCategory) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onClick() },
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Accent bar
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(categoryColor)
            )

            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Image with Avatar Badge support
                Box {
                    ProductImage(
                        candidateUrls = candidateUrls,
                        productName = product.nombre,
                        modifier = Modifier.size(64.dp),
                        onUrlVerified = { verifiedUrl ->
                            onSaveCachedUrl?.invoke(cacheKey, verifiedUrl)
                        }
                    )
                    avatarBadge?.invoke(this)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = product.nombre,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        
                        if (product.isCustom) {
                            Surface(
                                color = MaterialTheme.colorScheme.tertiary,
                                shape = MaterialTheme.shapes.extraSmall,
                                modifier = Modifier.padding(start = 2.dp)
                            ) {
                                Text(
                                    text = "Personalizado",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                    color = MaterialTheme.colorScheme.onTertiary,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    if (product.brands.isNotEmpty()) {
                        Text(
                            text = if (additionalBrandsCount > 0) "${product.brands} +$additionalBrandsCount marcas" else product.brands,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            fontWeight = if (additionalBrandsCount > 0) FontWeight.Bold else FontWeight.Normal
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Surface(
                            color = categoryColor.copy(alpha = 0.15f),
                            shape = MaterialTheme.shapes.extraSmall
                        ) {
                            Text(
                                text = normalizedCategory,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = categoryColor,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        
                        subContent?.invoke(this@Column)
                    }
                }
            }

            trailingContent?.invoke(this)
        }
    }
}

@Composable
fun AddToCartButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        label = "scale"
    )

    Box(
        modifier = modifier
            .padding(end = 16.dp)
            .size(40.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = stringResource(R.string.add_to_cart),
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
    }
}
