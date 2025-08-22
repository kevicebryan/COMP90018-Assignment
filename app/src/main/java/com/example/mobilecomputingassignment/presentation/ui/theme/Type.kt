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
 * - Headers/Titles: Racing Sans One (bold, sporty feel)
 * - Body/Labels: Noto Sans (clean, readable)
 *
 * Using Google Fonts API - no font files needed!
 */

// Google Fonts Provider
private val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

// Google Font Definitions
private val racingSansOneFont = GoogleFont("Racing Sans One")
private val notoSansFont = GoogleFont("Noto Sans")

// Custom Font Families using Google Fonts
val RacingFontFamily = FontFamily(
    Font(googleFont = racingSansOneFont, fontProvider = provider, weight = FontWeight.Normal)
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
        fontFamily = RacingFontFamily,      // Racing font for impact
        fontWeight = FontWeight.Normal,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = RacingFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = RacingFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    ),

    // HEADLINE STYLES - Page titles, section headers
    headlineLarge = TextStyle(
        fontFamily = RacingFontFamily,      // Racing font for headers
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = RacingFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = RacingFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),

    // TITLE STYLES - Smaller headers, card titles
    titleLarge = TextStyle(
        fontFamily = RacingFontFamily,      // Racing font for titles
        fontWeight = FontWeight.Normal,     // Use Bold if available
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = RacingFontFamily,
        fontWeight = FontWeight.Medium,     // Medium weight for contrast
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.1.sp
    ),
    titleSmall = TextStyle(
        fontFamily = RacingFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),

    // BODY STYLES - Main content text
    bodyLarge = TextStyle(
        fontFamily = NotoSansFontFamily,    // Noto Sans for readability
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
        fontFamily = NotoSansFontFamily,    // Noto Sans for UI elements
        fontWeight = FontWeight.Medium,     // Medium weight for emphasis
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
        fontSize = 10.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.sp
    )
)

/**
 * USAGE GUIDELINES:
 *
 * Headers & Titles (Racing Sans One - Google Font):
 * - App name, screen titles: headlineLarge/headlineMedium
 * - Section headers: headlineSmall, titleLarge
 * - Card titles: titleMedium
 * - Small headers: titleSmall
 *
 * Body Content (Noto Sans - Google Font):
 * - Main content: bodyLarge, bodyMedium
 * - Captions, descriptions: bodySmall
 * - Button text: labelLarge, labelMedium
 * - Form labels, tabs: labelSmall
 *
 * Example Usage in Compose:
 *
 * Text(
 *     text = "WatchMates",
 *     style = MaterialTheme.typography.headlineLarge
 * )
 *
 * Text(
 *     text = "Welcome to your movie companion",
 *     style = MaterialTheme.typography.bodyLarge
 * )
 *
 * GOOGLE FONTS INTEGRATION:
 * - Fonts are loaded automatically from Google Fonts API
 * - No font files needed in the project
 * - Automatically handles different font weights
 * - Requires internet connection for first load (then cached)
 */