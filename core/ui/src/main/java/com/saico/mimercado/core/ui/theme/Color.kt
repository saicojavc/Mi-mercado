package com.saico.mimercado.core.ui.theme

import androidx.compose.ui.graphics.Color

val AppBackground = Color(0xFFFAFBFC)
val PrimaryCyan = Color(0xFF00D9FF)
val SecondaryTeal = Color(0xFF06B6D4)
val TextDark = Color(0xFF0F172A)
val NeutralLight = Color(0xFFE2E8F0)
val SuccessGreen = Color(0xFF10B981)
val ErrorRed = Color(0xFFEF4444)
val CustomProductOrange = Color(0xFFF59E0B)

fun getCategoryColor(category: String): Color {
    return when (category.lowercase()) {
        "lácteos" -> Color(0xFF3B82F6) // Blue
        "panadería" -> Color(0xFFD97706) // Amber/Brown
        "carnes" -> Color(0xFFEF4444) // Red
        "frutas y verduras" -> Color(0xFF10B981) // Green
        "despensa" -> Color(0xFF8B5CF6) // Purple
        "limpieza" -> Color(0xFF06B6D4) // Teal
        "bebidas" -> Color(0xFFEC4899) // Pink
        else -> Color(0xFF64748B) // Slate Gray fallback
    }
}

