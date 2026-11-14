package com.shivansh.ktlab.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowInsetsControllerCompat

data class SyntaxColors(
    val keyword: Color,
    val string: Color,
    val number: Color,
    val function: Color,
    val comment: Color
)

val DarkSyntaxColors = SyntaxColors(
    keyword = DarkCodeKeyword,
    string = DarkCodeString,
    number = DarkCodeNumber,
    function = DarkCodeFunction,
    comment = DarkCodeComment
)

val LightSyntaxColors = SyntaxColors(
    keyword = LightCodeKeyword,
    string = LightCodeString,
    number = LightCodeNumber,
    function = LightCodeFunction,
    comment = LightCodeComment
)

val LocalSyntaxColors = staticCompositionLocalOf { DarkSyntaxColors }

private val DarkColorScheme = darkColorScheme(
    primary = DarkThemePrimaryGreen,
    secondary = DarkThemeSecondaryAmber,
    background = DarkThemeBackground,
    surface = DarkThemeSurface,
    surfaceVariant = DarkThemeSurfaceVariant,
    onBackground = DarkThemeTextPrimary,
    onSurface = DarkThemeTextPrimary,
    onPrimary = DarkThemeBackground,
    onSecondary = DarkThemeBackground,
    outline = DarkThemeBorder,
    error = DarkThemeErrorRed
)

private val LightColorScheme = lightColorScheme(
    primary = LightThemePrimaryGreen,
    secondary = LightThemeSecondaryAmber,
    background = LightThemeBackground,
    surface = LightThemeSurface,
    surfaceVariant = LightThemeSurfaceVariant,
    onBackground = LightThemeTextPrimary,
    onSurface = LightThemeTextPrimary,
    onPrimary = LightThemeSurface,
    onSecondary = LightThemeSurface,
    outline = LightThemeBorder,
    error = LightThemeErrorRed
)

@Composable
fun KtLabTheme(
    themeMode: String = "DARK",
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        "LIGHT" -> false
        "SYSTEM" -> isSystemInDarkTheme()
        else -> true // "DARK"
    }

    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val syntaxColors = if (darkTheme) DarkSyntaxColors else LightSyntaxColors

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.surface.toArgb()
                window.navigationBarColor = colorScheme.surface.toArgb()
                val insetsController = WindowInsetsControllerCompat(window, view)
                insetsController.isAppearanceLightStatusBars = !darkTheme
                insetsController.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(
        LocalSyntaxColors provides syntaxColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
