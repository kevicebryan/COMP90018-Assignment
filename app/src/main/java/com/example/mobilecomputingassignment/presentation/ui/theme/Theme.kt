package com.example.mobilecomputingassignment.presentation.ui.theme

import android.app.Activity
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
private val OrangePrimary = Color(0xFFFF6B35)      // Bright Orange
private val OrangeDark = Color(0xFFE5400A)         // Darker Orange for dark theme
private val YellowSecondary = Color(0xFFFFC107)    // Amber Yellow
private val YellowDark = Color(0xFFFF8F00)         // Darker Yellow for dark theme

// Alternative: Reference from colors.xml
// private val OrangePrimary = colorResource(R.color.watch_orange_primary)

private val DarkColorScheme = darkColorScheme(
    primary = OrangeDark,           // Main brand color in dark theme
    secondary = YellowDark,         // Secondary brand color in dark theme
    tertiary = Color(0xFFFFAB91),   // Light orange for accents
    background = Color(0xFF121212), // Dark background
    surface = Color(0xFF1E1E1E),    // Dark surface color
    onPrimary = Color.White,        // Text on orange buttons
    onSecondary = Color.Black,      // Text on yellow elements
    onBackground = Color.White,     // Text on dark background
    onSurface = Color.White,        // Text on dark surfaces
    error = Color(0xFFFF5722)       // Error color
)

private val LightColorScheme = lightColorScheme(
    primary = OrangePrimary,        // Main brand color (orange)
    secondary = YellowSecondary,    // Secondary brand color (yellow)
    tertiary = Color(0xFFFF8A65),   // Light orange for accents
    background = Color(0xFFFFFBFE), // Light background
    surface = Color(0xFFFFFFFF),    // Light surface color
    onPrimary = Color.White,        // Text on orange buttons
    onSecondary = Color.Black,      // Text on yellow elements
    onBackground = Color(0xFF1C1B1F), // Text on light background
    onSurface = Color(0xFF1C1B1F),   // Text on light surfaces
    error = Color(0xFFFF5722)       // Error color
)

@Composable
fun WatchmatesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+ but disabled to use custom WatchMates colors
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
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