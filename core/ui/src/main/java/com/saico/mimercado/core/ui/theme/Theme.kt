package com.saico.mimercado.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF06B6D4),
    onPrimary = Color.White,
    secondary = Color(0xFF00D9FF),
    onSecondary = Color.Black,
    tertiary = WarningAmber,
    onTertiary = Color.Black,
    background = Color(0xFF0F172A),
    onBackground = TextLight,
    surface = Color(0xFF1E293B),
    onSurface = TextLight,
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = TextMuted,
    outline = Color(0xFF475569),
    outlineVariant = Color(0xFF64748B),
    error = ErrorRed,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF0891B2),   // Cyan más oscuro para accesibilidad sobre fondo blanco
    onPrimary = Color.White,
    secondary = Color(0xFF06B6D4),
    onSecondary = Color.White,
    tertiary = WarningAmber,
    onTertiary = Color.White,
    background = Color(0xFFF8FAFC),
    onBackground = TextDark,
    surface = Color(0xFFFFFFFF),
    onSurface = TextDark,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = NeutralGray,
    outline = Color(0xFFCBD5E1),
    outlineVariant = Color(0xFFE2E8F0),
    error = ErrorRed,
    onError = Color.White
)

@Composable
fun MiMercadoTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
