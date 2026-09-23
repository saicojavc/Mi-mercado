package com.saico.mimercado.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saico.mimercado.core.model.ShoppingListSummary
import com.saico.mimercado.core.model.ShoppingListType

@Composable
fun SelectListDialog(
    productName: String,
    lists: List<ShoppingListSummary>,
    onListsSelected: (List<ShoppingListSummary>) -> Unit,
    onDismiss: () -> Unit
) {
    val selectedIds = remember { mutableStateListOf<String>() }

    LaunchedEffect(lists) {
        if (selectedIds.isEmpty() && lists.isNotEmpty()) {
            selectedIds.add(lists.first().id)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "Seleccionar listas",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (productName.isNotBlank()) {
                    Text(
                        text = "Agregar '$productName' a:",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 300.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(lists, key = { it.id }) { listSummary ->
                    val isChecked = selectedIds.contains(listSummary.id)
                    SelectListRow(
                        summary = listSummary,
                        isSelected = isChecked,
                        onToggle = {
                            if (isChecked) selectedIds.remove(listSummary.id)
                            else selectedIds.add(listSummary.id)
                        }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val chosen = lists.filter { selectedIds.contains(it.id) }
                    if (chosen.isNotEmpty()) {
                        onListsSelected(chosen)
                    }
                },
                enabled = selectedIds.isNotEmpty(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                )
            ) {
                Text("Agregar (${selectedIds.size})", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
fun SelectListRow(
    summary: ShoppingListSummary,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    val startColor = parseHexColor(summary.coverTheme.colorStart, Color(0xFF0F172A))
    val endColor = parseHexColor(summary.coverTheme.colorEnd, Color(0xFF06B6D4))
    val isMain = summary.type == ShoppingListType.MAIN

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle),
        shape = RoundedCornerShape(14.dp),
        border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.secondary) else null,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.secondary,
                    checkmarkColor = MaterialTheme.colorScheme.onSecondary
                )
            )

            Spacer(Modifier.width(8.dp))

            Surface(
                shape = CircleShape,
                modifier = Modifier.size(36.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.linearGradient(listOf(startColor, endColor))),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getDialogCoverIcon(summary.coverTheme.icon),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = summary.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (isMain) {
                        Spacer(Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.secondary
                        ) {
                            Text(
                                "Principal",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Text(
                    text = "${summary.itemCount} productos",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun getDialogCoverIcon(iconName: String): ImageVector {
    return when (iconName.lowercase()) {
        "home", "casa" -> Icons.Default.Home
        "celebration", "fiesta" -> Icons.Default.Celebration
        "car", "viaje" -> Icons.Default.DirectionsCar
        "pets", "mascota" -> Icons.Default.Pets
        "fitness", "gym" -> Icons.Default.FitnessCenter
        "restaurant", "comida" -> Icons.Default.Restaurant
        else -> Icons.Default.ShoppingCart
    }
}

private fun parseHexColor(hex: String, fallback: Color): Color {
    return try {
        val cleaned = hex.removePrefix("#")
        val colorInt = cleaned.toLong(16)
        if (cleaned.length == 6) {
            Color(0xFF000000 or colorInt)
        } else if (cleaned.length == 8) {
            Color(colorInt)
        } else fallback
    } catch (_: Exception) {
        fallback
    }
}
