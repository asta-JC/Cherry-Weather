package com.asta.cherryweather.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val CherryLightColorScheme = lightColorScheme(
    primary = Color(0xFFAF294A),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFF9DEE5),
    onPrimaryContainer = Color(0xFF652039),
    secondary = Color(0xFF78616B),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFF1E7E7),
    onSecondaryContainer = Color(0xFF422731),
    tertiary = Color(0xFF698269),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFDFEBDD),
    onTertiaryContainer = Color(0xFF243B25),
    background = Color(0xFFFCF8F5),
    onBackground = Color(0xFF422731),
    surface = Color(0xFFFFFCFA),
    onSurface = Color(0xFF422731),
    surfaceVariant = Color(0xFFF1E7E7),
    onSurfaceVariant = Color(0xFF78616B),
    outline = Color(0xFF927780),
    outlineVariant = Color(0xFFE9DDDF),
    surfaceDim = Color(0xFFE9DDDF),
    surfaceBright = Color(0xFFFFFCFA),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFFCF8F5),
    surfaceContainer = Color(0xFFF8F0EF),
    surfaceContainerHigh = Color(0xFFF1E7E7),
    surfaceContainerHighest = Color(0xFFE9DDDF),
    inverseSurface = Color(0xFF392A36),
    inverseOnSurface = Color(0xFFF9E8EE),
    inversePrimary = Color(0xFFFF9DB5),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
)

private val CherryDarkColorScheme = darkColorScheme(
    primary = Color(0xFFFF9DB5),
    onPrimary = Color(0xFF3B1528),
    primaryContainer = Color(0xFF502A3B),
    onPrimaryContainer = Color(0xFFFFD9E3),
    secondary = Color(0xFFCAB6C0),
    onSecondary = Color(0xFF261D27),
    secondaryContainer = Color(0xFF392A36),
    onSecondaryContainer = Color(0xFFF9E8EE),
    tertiary = Color(0xFFB7D0AC),
    onTertiary = Color(0xFF18351F),
    tertiaryContainer = Color(0xFF304831),
    onTertiaryContainer = Color(0xFFDFEBDD),
    background = Color(0xFF1C151D),
    onBackground = Color(0xFFF9E8EE),
    surface = Color(0xFF261D27),
    onSurface = Color(0xFFF9E8EE),
    surfaceVariant = Color(0xFF392A36),
    onSurfaceVariant = Color(0xFFCAB6C0),
    outline = Color(0xFF9B828F),
    outlineVariant = Color(0xFF4B3845),
    surfaceDim = Color(0xFF1C151D),
    surfaceBright = Color(0xFF392A36),
    surfaceContainerLowest = Color(0xFF181119),
    surfaceContainerLow = Color(0xFF211922),
    surfaceContainer = Color(0xFF261D27),
    surfaceContainerHigh = Color(0xFF30232F),
    surfaceContainerHighest = Color(0xFF392A36),
    inverseSurface = Color(0xFFF9E8EE),
    inverseOnSurface = Color(0xFF422731),
    inversePrimary = Color(0xFFAF294A),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
)

@Composable
fun CherryWeatherTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) CherryDarkColorScheme else CherryLightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }
    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
