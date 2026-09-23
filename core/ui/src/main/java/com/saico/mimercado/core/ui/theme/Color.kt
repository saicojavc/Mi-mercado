package com.saico.mimercado.core.ui.theme

import androidx.compose.ui.graphics.Color

// Base Dark Palette (Slate / Bring style)
val DarkBackground = Color(0xFF0F172A)      // Slate 900
val DarkSurface = Color(0xFF1E293B)         // Slate 800
val DarkSurfaceVariant = Color(0xFF334155)  // Slate 700
val DarkBorder = Color(0xFF475569)          // Slate 600

// Base Light Palette
val AppBackground = Color(0xFFFAFBFC)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFF1F5F9)

// Accent Palette
val PrimaryCyan = Color(0xFF00D9FF)
val SecondaryTeal = Color(0xFF06B6D4)
val AccentTeal = Color(0xFF14B8A6)

// Text Colors
val TextLight = Color(0xFFF8FAFC)
val TextMuted = Color(0xFF94A3B8)
val TextDark = Color(0xFF0F172A)
val NeutralGray = Color(0xFF64748B)
val NeutralLight = Color(0xFFE2E8F0)

// Feedback & Status
val SuccessGreen = Color(0xFF10B981)
val ErrorRed = Color(0xFFEF4444)
val WarningAmber = Color(0xFFF59E0B)

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
