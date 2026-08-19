package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = NewsBlueLight,
    onPrimary = Color.White,
    primaryContainer = NewsBlueDark,
    onPrimaryContainer = Color(0xFFDBEAFE),
    secondary = NewsCrimsonLight,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF450A0A),
    onSecondaryContainer = Color(0xFFFFE4E6),
    tertiary = NewsAmberLight,
    onTertiary = Color(0xFF78350F),
    tertiaryContainer = Color(0xFF451A03),
    onTertiaryContainer = Color(0xFFFEF3C7),
    background = HighDensityDarkCanvas,
    onBackground = HighDensityDarkTextPrimary,
    surface = HighDensityDarkSurface,
    onSurface = HighDensityDarkTextPrimary,
    surfaceVariant = HighDensityDarkSurfaceVariant,
    onSurfaceVariant = HighDensityDarkTextSecondary,
    outline = HighDensityDarkBorder
)

private val LightColorScheme = lightColorScheme(
    primary = NewsBluePrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEFF6FF), // blue-50
    onPrimaryContainer = NewsBlueDark,
    secondary = NewsCrimson,
    onSecondary = Color.White,
    secondaryContainer = NewsCrimsonContainer, // red-50
    onSecondaryContainer = NewsCrimsonDark,
    tertiary = NewsAmber,
    onTertiary = Color.White,
    tertiaryContainer = NewsAmberContainer,
    onTertiaryContainer = Color(0xFF78350F),
    background = HighDensityCanvas, // #F8F9FF
    onBackground = HighDensityTextPrimary,
    surface = HighDensitySurface,
    onSurface = HighDensityTextPrimary,
    surfaceVariant = HighDensitySurfaceVariant,
    onSurfaceVariant = HighDensityTextMuted,
    outline = HighDensityBorder
)

@Composable
fun GlobalNewsTheme(
    themeMode: String = "SYSTEM", // "SYSTEM", "LIGHT", "DARK"
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        "LIGHT" -> false
        "DARK" -> true
        else -> isSystemInDarkTheme()
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
