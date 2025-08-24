package com.example.mobilecomputingassignment.presentation.ui.theme

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

// WatchMates Custom Colors - can also reference from colors.xml if needed
private val OrangePrimary = Color(0xFFFF6B35) // Bright Orange
private val OrangeDark = Color(0xFFE5400A) // Darker Orange for dark theme
private val YellowSecondary = Color(0xFFFFC107) // Amber Yellow
private val YellowDark = Color(0xFFFF8F00) // Darker Yellow for dark theme

// Alternative: Reference from colors.xml
// private val OrangePrimary = colorResource(R.color.watch_orange_primary)

private val DarkColorScheme =
        darkColorScheme(
                primary = OrangeDark, // Main brand color in dark theme
                onPrimary = Color.White, // Text on orange buttons
                primaryContainer = Color(0xFF4A2C17), // Dark orange container
                onPrimaryContainer = Color(0xFFFFDCC5), // Text on dark orange container
                secondary = YellowDark, // Secondary brand color in dark theme
                onSecondary = Color.Black, // Text on yellow elements
                secondaryContainer = Color(0xFF4A3800), // Dark yellow container
                onSecondaryContainer = Color(0xFFFFF4C4), // Text on dark yellow container
                tertiary = Color(0xFFFFAB91), // Light orange for accents
                onTertiary = Color.Black, // Text on tertiary
                tertiaryContainer = Color(0xFF5D2F00), // Dark tertiary container
                onTertiaryContainer = Color(0xFFFFDCC5), // Text on tertiary container
                background = Color(0xFF121212), // Dark background
                onBackground = Color.White, // Text on dark background
                surface = Color(0xFF1E1E1E), // Dark surface color
                onSurface = Color.White, // Text on dark surfaces
                surfaceVariant = Color(0xFF2A2A2A), // Variant surface color
                onSurfaceVariant = Color(0xFFE0E0E0), // Text on variant surface
                outline = Color(0xFF757575), // Outline color
                error = Color(0xFFFF5722) // Error color
        )

private val LightColorScheme =
        lightColorScheme(
                primary = OrangePrimary, // Main brand color (orange)
                onPrimary = Color.White, // Text on orange buttons
                primaryContainer = Color(0xFFFFDCC5), // Light orange container
                onPrimaryContainer = Color(0xFF4A2C17), // Text on light orange container
                secondary = YellowSecondary, // Secondary brand color (yellow)
                onSecondary = Color.Black, // Text on yellow elements
                secondaryContainer = Color(0xFFFFF4C4), // Light yellow container
                onSecondaryContainer = Color(0xFF4A3800), // Text on light yellow container
                tertiary = Color(0xFFFF8A65), // Light orange for accents
                onTertiary = Color.White, // Text on tertiary
                tertiaryContainer = Color(0xFFFFE0CC), // Light tertiary container
                onTertiaryContainer = Color(0xFF5D2F00), // Text on tertiary container
                background = Color(0xFFFFFBFE), // Light background
                onBackground = Color(0xFF1C1B1F), // Text on light background
                surface = Color(0xFFFFFFFF), // Light surface color
                onSurface = Color(0xFF1C1B1F), // Text on light surfaces
                surfaceVariant = Color(0xFFF5F5F5), // Variant surface color
                onSurfaceVariant = Color(0xFF49454F), // Text on variant surface
                outline = Color(0xFF79747E), // Outline color
                error = Color(0xFFFF5722) // Error color
        )

@Composable
fun WatchmatesTheme(
        darkTheme: Boolean = isSystemInDarkTheme(),
        // Dynamic color is available on Android 12+ but disabled to use custom WatchMates colors
        dynamicColor: Boolean = false,
        content: @Composable () -> Unit
) {
    val colorScheme =
            when {
                dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                    val context = LocalContext.current
                    if (darkTheme) dynamicDarkColorScheme(context)
                    else dynamicLightColorScheme(context)
                }
                darkTheme -> DarkColorScheme
                else -> LightColorScheme
            }

    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
