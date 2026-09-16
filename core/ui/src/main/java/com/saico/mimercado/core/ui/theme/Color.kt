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
    // Normalizamos para asegurar que coincida con los colores definidos
    val normalized = category.lowercase().trim()
    return when {
        normalized.contains("lácteo") || normalized.contains("dairy") -> Color(0xFF3B82F6) // Blue
        normalized.contains("panadería") || normalized.contains("bakery") -> Color(0xFFD97706) // Amber/Brown
        normalized.contains("carne") || normalized.contains("meat") -> Color(0xFFEF4444) // Red
        normalized.contains("fruta") || normalized.contains("verdura") || normalized.contains("produce") -> Color(0xFF10B981) // Green
        normalized.contains("despensa") || normalized.contains("pantry") || normalized.contains("grocery") -> Color(0xFF8B5CF6) // Purple
        normalized.contains("limpieza") || normalized.contains("clean") -> Color(0xFF06B6D4) // Teal
        normalized.contains("bebida") || normalized.contains("beverage") || normalized.contains("drink") -> Color(0xFFEC4899) // Pink
        else -> Color(0xFF64748B) // Slate Gray fallback
    }
}

