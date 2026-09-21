package com.saico.mimercado.core.ui.theme

import androidx.compose.ui.graphics.Color

// Base Palette
val AppBackground = Color(0xFFFAFBFC)
val PrimaryCyan = Color(0xFF00D9FF)
val SecondaryTeal = Color(0xFF06B6D4)
val TextDark = Color(0xFF0F172A)
val NeutralGray = Color(0xFF64748B)
val NeutralLight = Color(0xFFE2E8F0)

// Feedback & Status
val SuccessGreen = Color(0xFF10B981)
val ErrorRed = Color(0xFFEF4444)
val WarningAmber = Color(0xFFF59E0B) // For "Personalizado" badge

// Category Colors Map
object CategoryColors {
    val Lácteos = Color(0xFF3B82F6)
    val Panadería = Color(0xFFD97706)
    val Carnes = Color(0xFFEF4444)
    val FrutasVerduras = Color(0xFF10B981)
    val Despensa = Color(0xFF8B5CF6)
    val Limpieza = Color(0xFF06B6D4)
    val Bebidas = Color(0xFFEC4899)
    val Default = Color(0xFF64748B)
}

fun getCategoryColor(category: String): Color {
    val normalized = category.lowercase().trim()
    return when {
        normalized.contains("lácteo") || normalized.contains("dairy") -> CategoryColors.Lácteos
        normalized.contains("panadería") || normalized.contains("bakery") -> CategoryColors.Panadería
        normalized.contains("carne") || normalized.contains("meat") -> CategoryColors.Carnes
        normalized.contains("fruta") || normalized.contains("verdura") || normalized.contains("produce") -> CategoryColors.FrutasVerduras
        normalized.contains("despensa") || normalized.contains("pantry") || normalized.contains("grocery") -> CategoryColors.Despensa
        normalized.contains("limpieza") || normalized.contains("clean") -> CategoryColors.Limpieza
        normalized.contains("bebida") || normalized.contains("beverage") || normalized.contains("drink") -> CategoryColors.Bebidas
        else -> CategoryColors.Default
    }
}
