package com.saico.mimercado.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PrimaryCyan,
    onPrimary = Color.White,
    secondary = SecondaryTeal,
    onSecondary = Color.White,
    tertiary = WarningAmber,
    onTertiary = Color.White,
    background = AppBackground,
    onBackground = TextDark,
    surface = Color.White,
    onSurface = TextDark,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = NeutralGray,
    error = ErrorRed,
    onError = Color.White
)

@Composable
fun MiMercadoTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    // Forced light theme for now to maintain consistent UI polish
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
