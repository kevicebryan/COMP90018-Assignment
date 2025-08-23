# WatchMates Design Guide

## Complete Theme Structure

### File Organization

```
ui/theme/
├── Color.kt          // Brand color definitions
├── Type.kt           // Typography system
├── Theme.kt          // Main theme composition
└── ThemeUsageGuide.kt // Development reference
```

### Development Workflow

**1. Use Theme Colors (Automatic light/dark adaptation):**

```kotlin
MaterialTheme.colorScheme.primary        // Orange - primary actions
MaterialTheme.colorScheme.secondary      // Yellow - secondary actions
MaterialTheme.colorScheme.surface        // Card backgrounds
MaterialTheme.colorScheme.error          // Error states
```

**2. Use Typography System:**

```kotlin
MaterialTheme.typography.headlineLarge   // Racing Sans One - app title
MaterialTheme.typography.titleLarge      // Racing Sans One - section headers
MaterialTheme.typography.bodyLarge       // Noto Sans - main content
MaterialTheme.typography.labelLarge      // Noto Sans - button text
```

**3. Complete Component Example:**

```kotlin
Card(
    colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surface
    )
) {
    Text(
        "Movie Title",
        style = MaterialTheme.typography.titleLarge,  // Racing Sans One
        color = MaterialTheme.colorScheme.onSurface
    )
    Text(
        "Description...",
        style = MaterialTheme.typography.bodyMedium,  // Noto Sans
        color = MaterialTheme.colorScheme.onSurface
    )
    Button(
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary  // Orange
        )
    ) {
        Text(
            "Watch Now",
            style = MaterialTheme.typography.labelLarge  // Noto Sans
        )
    }
}
```

## Color Resources

## Typography System

All typography styles are defined in: `app/src/main/java/com/example/mobilecomputingassignment/ui/theme/Type.kt`

### Font Families (Google Fonts)

**Racing Sans One (Headers/Titles)**

- Source: Google Fonts API (no files needed!)
- Usage: App titles, section headers, card titles
- Character: Bold, sporty, attention-grabbing
- Available weights: Normal

**Noto Sans (Body/Labels)**

- Source: Google Fonts API (no files needed!)
- Available weights: Light, Normal, Medium, SemiBold, Bold
- Usage: Body text, buttons, form labels
- Character: Clean, readable, professional

### Typography Scale

| Style           | Font            | Size | Usage Example   |
| --------------- | --------------- | ---- | --------------- |
| `displayLarge`  | Racing Sans One | 57sp | Hero titles     |
| `headlineLarge` | Racing Sans One | 32sp | Screen titles   |
| `titleLarge`    | Racing Sans One | 22sp | Section headers |
| `bodyLarge`     | Noto Sans       | 16sp | Main content    |
| `labelLarge`    | Noto Sans       | 14sp | Button text     |

### How to Change Fonts

**To use different Google Fonts:**

1. **Find the font** on [Google Fonts](https://fonts.google.com/)
2. **Update the font name** in `Type.kt`:

```kotlin
private val newHeaderFont = GoogleFont("Your Font Name")
val YourFontFamily = FontFamily(
    Font(googleFont = newHeaderFont, fontProvider = provider, weight = FontWeight.Normal)
)
```

3. **Update typography styles** to use new font family
4. **Clean and rebuild** project

**Benefits of Google Fonts:**

- ✅ No font files to manage
- ✅ Automatic font weight support
- ✅ Always up-to-date fonts
- ✅ Smaller app size
- ✅ Cached across device apps

**Requirements:**

- Internet connection for first load
- Fonts are cached after first download

### Adding New Google Fonts

To add a new Google Font:

```kotlin
// 1. Define the Google Font
private val yourNewFont = GoogleFont("Font Name From Google Fonts")

// 2. Create FontFamily with weights you need
val YourFontFamily = FontFamily(
    Font(googleFont = yourNewFont, fontProvider = provider, weight = FontWeight.Light),
    Font(googleFont = yourNewFont, fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = yourNewFont, fontProvider = provider, weight = FontWeight.Bold)
)

// 3. Use in Typography definitions
titleMedium = TextStyle(
    fontFamily = YourFontFamily,  // Use here
    fontWeight = FontWeight.Bold
)
```

## Theme Colors Location

All app colors are defined in: `app/src/main/java/com/example/mobilecomputingassignment/ui/theme/Theme.kt`

## Current Brand Colors

### Primary Colors (Orange)

- **Light Theme Primary**: `#FF6B35` (Bright Orange)
- **Dark Theme Primary**: `#E5400A` (Darker Orange)
- **Usage**: Main buttons, app bar, primary actions

### Secondary Colors (Yellow)

- **Light Theme Secondary**: `#FFC107` (Amber Yellow)
- **Dark Theme Secondary**: `#FF8F00` (Darker Yellow)
- **Usage**: Secondary buttons, highlights, accent elements

### Tertiary Colors

- **Light Theme Tertiary**: `#FF8A65` (Light Orange)
- **Dark Theme Tertiary**: `#FFAB91` (Light Orange)
- **Usage**: Subtle accents, borders, icons

## How to Change Colors

### 1. Primary Brand Color (Orange)

```kotlin
// In Theme.kt, update these values:
private val OrangePrimary = Color(0xFFFF6B35)      // Your new light theme color
private val OrangeDark = Color(0xFFE5400A)         // Your new dark theme color
```

### 2. Secondary Brand Color (Yellow)

```kotlin
// In Theme.kt, update these values:
private val YellowSecondary = Color(0xFFFFC107)    // Your new light theme color
private val YellowDark = Color(0xFFFF8F00)         // Your new dark theme color
```

### 3. Color Format

Colors use hexadecimal format with alpha channel:

- Format: `Color(0xFFRRGGBB)`
- `FF` = fully opaque
- `RRGGBB` = red, green, blue values in hex

### Examples:

- Pure Red: `Color(0xFFFF0000)`
- Pure Blue: `Color(0xFF0000FF)`
- Pure Green: `Color(0xFF00FF00)`
- Black: `Color(0xFF000000)`
- White: `Color(0xFFFFFFFF)`

## Color Usage in Components

### Buttons

- **Primary Button**: Uses `primary` color (orange)
- **Secondary Button**: Uses `secondary` color (yellow)
- **Text Button**: Uses `onSurface` color

### Text

- **Headlines**: Uses `onBackground` color
- **Body Text**: Uses `onSurface` color
- **Button Text**: Uses `onPrimary` or `onSecondary`

### Backgrounds

- **App Background**: Uses `background` color
- **Card/Surface**: Uses `surface` color

## Testing Changes

After updating colors:

1. **Clean Project** (Build → Clean Project)
2. **Rebuild Project** (Build → Rebuild Project)
3. **Run the app** to see changes
4. **Test both light and dark themes** by changing device settings

## Additional Color Resources

If you need more colors, you can also define them in:
`app/src/main/res/values/colors.xml`

```xml
<resources>
    <color name="watch_orange">#FF6B35</color>
    <color name="watch_yellow">#FFC107</color>
</resources>
```

Then reference in Theme.kt:

```kotlin
private val OrangePrimary = Color(R.color.watch_orange)
```

## Design Tools Integration

### Figma Integration

When exporting colors from Figma:

1. Copy hex value (e.g., `#FF6B35`)
2. Convert to Android format: `Color(0xFFFF6B35)`
3. Update in Theme.kt

### Adobe XD Integration

When exporting from XD:

1. Copy color hex value
2. Add `0xFF` prefix for Android
3. Update in Theme.kt

## Preview Colors

You can preview your theme colors by running the app and checking:

- Buttons (primary/secondary colors)
- Text (on colors)
- Backgrounds
- Cards and surfaces

## Contact

For questions about implementing design changes, contact the development team.
