package com.shivansh.ktlab.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Dark Theme Color Palette
val DarkThemeBackground = Color(0xFF0F1117)
val DarkThemeSurface = Color(0xFF0A0C10)
val DarkThemeSurfaceVariant = Color(0xFF161B22)
val DarkThemeBorder = Color(0xFF30363D)

val DarkThemePrimaryGreen = Color(0xFF2EA66B)
val DarkThemeSecondaryAmber = Color(0xFFF0A030)

val DarkThemeTextPrimary = Color(0xFFE6EDF3)
val DarkThemeTextSecondary = Color(0xFF8B949E)
val DarkThemeTextMuted = Color(0xFF484F58)
val DarkThemeErrorRed = Color(0xFFF85149)

// Dark Syntax Highlighting Colors
val DarkCodeKeyword = Color(0xFFFF7B72)   // Red/Pink
val DarkCodeString = Color(0xFFA5D6FF)    // Sky Blue
val DarkCodeNumber = Color(0xFF79C0FF)    // Cyan
val DarkCodeComment = Color(0xFF8B949E)   // Muted Gray
val DarkCodeFunction = Color(0xFFD2A8FF)  // Purple

// Light Theme Color Palette
val LightThemeBackground = Color(0xFFF6F8FA)
val LightThemeSurface = Color(0xFFFFFFFF)
val LightThemeSurfaceVariant = Color(0xFFF0F3F6)
val LightThemeBorder = Color(0xFFD0D7DE)

val LightThemePrimaryGreen = Color(0xFF1A7F4B)
val LightThemeSecondaryAmber = Color(0xFFB76E00)

val LightThemeTextPrimary = Color(0xFF1F2328)
val LightThemeTextSecondary = Color(0xFF57606A)
val LightThemeTextMuted = Color(0xFF8C959F)
val LightThemeErrorRed = Color(0xFFCF222E)

// Light Syntax Highlighting Colors
val LightCodeKeyword = Color(0xFFCF222E)
val LightCodeString = Color(0xFF0A3069)
val LightCodeNumber = Color(0xFF0550AE)
val LightCodeFunction = Color(0xFF8250DF)
val LightCodeComment = Color(0xFF6E7781)

// Backward Compatibility / Defaults -> Bound to MaterialTheme dynamically
val PrimaryGreen: Color @Composable get() = MaterialTheme.colorScheme.primary
val SecondaryAmber: Color @Composable get() = MaterialTheme.colorScheme.secondary
val TextPrimary: Color @Composable get() = MaterialTheme.colorScheme.onSurface
val TextSecondary: Color @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant
val TextMuted: Color @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
val ErrorRed: Color @Composable get() = MaterialTheme.colorScheme.error

// Reused dark names acting as dynamic colors now
val DarkBackground: Color @Composable get() = MaterialTheme.colorScheme.background
val DarkSurface: Color @Composable get() = MaterialTheme.colorScheme.surface
val DarkSurfaceVariant: Color @Composable get() = MaterialTheme.colorScheme.surfaceVariant
val DarkBorder: Color @Composable get() = MaterialTheme.colorScheme.outline
