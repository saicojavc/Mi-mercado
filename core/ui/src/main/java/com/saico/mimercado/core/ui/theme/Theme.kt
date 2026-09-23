package com.saico.mimercado.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = SecondaryTeal,
    onPrimary = Color.White,
    secondary = PrimaryCyan,
    onSecondary = Color.Black,
    tertiary = WarningAmber,
    onTertiary = Color.Black,
    background = DarkBackground,
    onBackground = TextLight,
    surface = DarkSurface,
    onSurface = TextLight,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextMuted,
    error = ErrorRed,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = SecondaryTeal,
    onPrimary = Color.White,
    secondary = PrimaryCyan,
    onSecondary = Color.White,
    tertiary = WarningAmber,
    onTertiary = Color.White,
    background = AppBackground,
    onBackground = TextDark,
    surface = LightSurface,
    onSurface = TextDark,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = NeutralGray,
    error = ErrorRed,
    onError = Color.White
)

@Composable
fun MiMercadoTheme(
    darkTheme: Boolean = true, // Dark theme by default per spec
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
