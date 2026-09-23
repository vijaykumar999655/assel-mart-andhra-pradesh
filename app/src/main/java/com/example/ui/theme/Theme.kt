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
    primary = CrimsonPrimaryDark,
    onPrimary = Color(0xFF5E0012),
    primaryContainer = Color(0xFF7A0A1D),
    onPrimaryContainer = Color(0xFFFFDADA),
    secondary = SaffronAmberDark,
    onSecondary = Color(0xFF492400),
    secondaryContainer = Color(0xFF6B3700),
    onSecondaryContainer = Color(0xFFFFDCB8),
    tertiary = FarmGreenDark,
    onTertiary = Color(0xFF003915),
    surface = DarkSurface,
    onSurface = Color(0xFFF1EFEA),
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFD4C5BE),
    background = DarkBackground,
    onBackground = Color(0xFFF1EFEA)
)

private val LightColorScheme = lightColorScheme(
    primary = CrimsonPrimary,
    onPrimary = Color.White,
    primaryContainer = CrimsonContainer,
    onPrimaryContainer = OnCrimsonContainer,
    secondary = SaffronAmber,
    onSecondary = Color.White,
    secondaryContainer = SaffronContainer,
    onSecondaryContainer = OnSaffronContainer,
    tertiary = FarmGreen,
    onTertiary = Color.White,
    surface = LightSurface,
    onSurface = Color(0xFF1E1A1A),
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = Color(0xFF51443E),
    background = LightBackground,
    onBackground = Color(0xFF1E1A1A)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use our handcrafted Andhra heritage theme
    content: @Composable () -> Unit,
) {
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
