package com.asta.cherryweather.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val CherryDarkColorScheme = darkColorScheme(
    primary = PrimaryCherry,
    onPrimary = TextWhite,
    primaryContainer = BorderCrimson,
    onPrimaryContainer = TextWhite,
    secondary = GradientStart,
    onSecondary = TextWhite,
    secondaryContainer = CardDarkPlumElevated,
    onSecondaryContainer = TextWhite,
    tertiary = GradientEnd,
    onTertiary = TextWhite,
    background = BackgroundDark,
    onBackground = TextWhite,
    surface = CardDarkPlum,
    onSurface = TextWhite,
    surfaceVariant = CardDarkPlumElevated,
    onSurfaceVariant = TextMuted,
    outline = BorderCrimson,
    outlineVariant = BorderCrimson,
    error = GradientStart,
    onError = TextWhite
)

@Composable
fun CherryWeatherTheme(
    darkTheme: Boolean = true, // Force Cherry Red dark futuristic aesthetic
    content: @Composable () -> Unit
) {
    val colorScheme = CherryDarkColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = BackgroundDark.toArgb()
            window.navigationBarColor = BackgroundDark.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
