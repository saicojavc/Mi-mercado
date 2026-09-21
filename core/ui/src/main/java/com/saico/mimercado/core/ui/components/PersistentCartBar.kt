package com.saico.mimercado.core.ui.components

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun PersistentCartBar(
    totalItems: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    
    // Animation states
    val scale = remember { Animatable(1f) }
    val primaryColor = MaterialTheme.colorScheme.primary
    val backgroundColor = remember { Animatable(primaryColor) }
    val flashColor = MaterialTheme.colorScheme.primaryContainer

    // Track previous count to only animate on increase
    var previousTotal by remember { mutableIntStateOf(totalItems) }

    // Trigger animations only on count increase
    LaunchedEffect(totalItems) {
        if (totalItems > previousTotal) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            scope.launch {
                scale.animateTo(
                    targetValue = 1.08f,
                    animationSpec = tween(100)
                )
                scale.animateTo(
                    targetValue = 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            }
            scope.launch {
                backgroundColor.animateTo(flashColor, tween(100))
                backgroundColor.animateTo(primaryColor, tween(400))
            }
        }
        previousTotal = totalItems
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale.value)
            .clickable { onClick() },
        color = backgroundColor.value,
        contentColor = Color.White,
        tonalElevation = 8.dp,
        shadowElevation = 16.dp
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 14.dp)
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = if (totalItems == 1) "1 producto en tu lista" else "$totalItems productos en tu lista",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Text(
                text = "VER LISTA →",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )
        }
    }
}
