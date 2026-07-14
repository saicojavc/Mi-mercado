package com.saico.mimercado.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.saico.mimercado.ui.theme.NeutralLight
import com.saico.mimercado.ui.theme.PrimaryCyan
import com.saico.mimercado.ui.theme.TextDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryFilter(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { category ->
            val isSelected = selectedCategory == category
            val scale by animateFloatAsState(
                targetValue = if (isSelected) 0.95f else 1f,
                animationSpec = tween(100),
                label = "scale"
            )
            val alpha by animateFloatAsState(
                targetValue = if (isSelected) 1f else 0.8f,
                animationSpec = tween(100),
                label = "alpha"
            )

            FilterChip(
                selected = isSelected,
                onClick = { onCategorySelected(category) },
                label = { Text(text = category) },
                modifier = Modifier
                    .scale(scale)
                    .alpha(alpha),
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = NeutralLight,
                    labelColor = TextDark,
                    selectedContainerColor = PrimaryCyan,
                    selectedLabelColor = Color.White
                ),
                border = null
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CategoryFilterPreview() {
    CategoryFilter(
        categories = listOf("Todos", "Lácteos", "Panadería"),
        selectedCategory = "Todos",
        onCategorySelected = {}
    )
}
