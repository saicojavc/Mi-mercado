package com.saico.mimercado.core.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest

@Composable
fun ProductImage(
    candidateUrls: List<String>,
    productName: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    cacheKey: String? = null,
    onUrlVerified: ((String) -> Unit)? = null
) {
    val context = LocalContext.current
    
    // Lista local mutable para priorizar la caché
    val sortedUrls = remember(candidateUrls, cacheKey) {
        val list = candidateUrls.toMutableList()
        // No buscamos directamente aquí para no bloquear el hilo de UI, 
        // pero sortedUrls se inicializa una vez.
        list
    }

    // No usamos 'remember(sortedUrls)' para el index si queremos que sea instantáneo al cambiar de producto
    var urlIndex by remember(candidateUrls) { mutableIntStateOf(0) }
    val currentUrl = candidateUrls.getOrNull(urlIndex) ?: ""

    Surface(
        modifier = modifier.aspectRatio(1f),
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        if (currentUrl.isNotEmpty()) {
            Box(contentAlignment = Alignment.Center) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(currentUrl)
                        .crossfade(true)
                        .diskCachePolicy(CachePolicy.ENABLED)
                        .setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                        .build(),
                    contentDescription = productName,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = contentScale,
                    onSuccess = {
                        // Si cargó con éxito, notificamos para guardar en caché
                        onUrlVerified?.invoke(currentUrl)
                    },
                    onError = {
                        if (urlIndex < sortedUrls.size - 1) {
                            urlIndex++
                        }
                    }
                )
                
                // Si llegamos al final y no hay nada, o si estamos probando la última opción
                if (urlIndex == candidateUrls.size - 1 && currentUrl.isEmpty()) {
                    DefaultProductIcon()
                }
            }
        } else {
            DefaultProductIcon()
        }
    }
}

@Composable
private fun DefaultProductIcon() {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        Icon(
            imageVector = Icons.Default.ShoppingCart,
            contentDescription = null,
            tint = Color.LightGray.copy(alpha = 0.5f),
            modifier = Modifier.size(28.dp)
        )
    }
}
