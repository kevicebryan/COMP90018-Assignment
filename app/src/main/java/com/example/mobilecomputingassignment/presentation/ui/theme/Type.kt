package com.example.mobilecomputingassignment.presentation.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.example.mobilecomputingassignment.R

/**
 * WatchMates Typography System
 *
 * Font Hierarchy:
 * - Headers/Titles: Space Mono (modern, monospace feel)
 * - Body/Labels: Noto Sans (clean, readable)
 *
 * Using Google Fonts API with proper error handling
 */

// Google Fonts Provider
val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

// Google Font Definitions
val spaceMonoFont = GoogleFont("Space Mono")
val notoSansFont = GoogleFont("Noto Sans")

// Custom Font Families using Google Fonts with fallbacks
val SpaceMonoFamily = FontFamily(
    Font(
        googleFont = spaceMonoFont,
        fontProvider = provider,
        weight = FontWeight.Normal
    ),
    Font(
        googleFont = spaceMonoFont,
        fontProvider = provider,
        weight = FontWeight.Bold
    )
)

val NotoSansFontFamily = FontFamily(
    Font(googleFont = notoSansFont, fontProvider = provider, weight = FontWeight.Light),
    Font(googleFont = notoSansFont, fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = notoSansFont, fontProvider = provider, weight = FontWeight.Medium),
    Font(googleFont = notoSansFont, fontProvider = provider, weight = FontWeight.SemiBold),
    Font(googleFont = notoSansFont, fontProvider = provider, weight = FontWeight.Bold)
)

// WatchMates Typography System
val Typography = Typography(
    // DISPLAY STYLES - Largest text, used for hero sections
    displayLarge = TextStyle(
        fontFamily = SpaceMonoFamily, // Space Mono for modern tech feel
        fontWeight = FontWeight.Bold,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = SpaceMonoFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = SpaceMonoFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    ),

    // HEADLINE STYLES - Page titles, section headers
    headlineLarge = TextStyle(
        fontFamily = SpaceMonoFamily, // Space Mono for headers
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = SpaceMonoFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = SpaceMonoFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),

    // TITLE STYLES - Smaller headers, card titles
    titleLarge = TextStyle(
        fontFamily = SpaceMonoFamily, // Space Mono for titles
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = NotoSansFontFamily, // Noto Sans for better readability
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.1.sp
    ),
    titleSmall = TextStyle(
        fontFamily = NotoSansFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),

    // BODY STYLES - Main content text
    bodyLarge = TextStyle(
        fontFamily = NotoSansFontFamily, // Noto Sans for readability
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = NotoSansFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = NotoSansFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),

    // LABEL STYLES - Buttons, tabs, form labels
    labelLarge = TextStyle(
        fontFamily = NotoSansFontFamily, // Noto Sans for UI elements
        fontWeight = FontWeight.Medium, // Medium weight for emphasis
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = NotoSansFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = NotoSansFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)

/**
 * USAGE GUIDELINES:
 *
 * Headers & Titles (Space Mono):
 * - App name, screen titles: headlineLarge/headlineMedium
 * - Section headers: headlineSmall, titleLarge
 * - Card titles: titleMedium (now uses Noto Sans for better readability)
 * - Small headers: titleSmall
 *
 * Body Content (Noto Sans):
 * - Main content: bodyLarge, bodyMedium
 * - Captions, descriptions: bodySmall
 * - Button text: labelLarge, labelMedium
 * - Form labels, tabs: labelSmall
 *
 * Example Usage in Compose:
 * ```
 * Text(
 *     text = "WatchMates",
 *     style = MaterialTheme.typography.headlineLarge
 * )
 *
 * Text(
 *     text = "Welcome to your movie companion",
 *     style = MaterialTheme.typography.bodyLarge
 * )
 * ```
 *
 * FONT LOADING:
 * - Fonts are loaded from Google Fonts API
 * - Requires internet connection for first load (then cached)
 * - Falls back to system fonts if loading fails
 */