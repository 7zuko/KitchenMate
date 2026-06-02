package de.thm.smartshopping.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.SideEffect
import androidx.core.view.WindowCompat
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    secondary = AccentGold,

    background = DarkBackground,
    surface = DarkSurface,

    primaryContainer = Color(0xFF2A3524),
    onPrimaryContainer = DarkPrimary,

    secondaryContainer = Color(0xFF4D3C21),
    onSecondaryContainer = SurfaceWhite,

    surfaceVariant = Color(0xFF222822),
    onSurfaceVariant = SurfaceWhite,

    onPrimary = SurfaceWhite,
    onBackground = SurfaceWhite,
    onSurface = SurfaceWhite
)

private val LightColorScheme = lightColorScheme(
    primary = SageGreen,
    secondary = AccentGold,

    background = CreamBackground,
    surface = SurfaceWhite,

    primaryContainer = SageGreenLight,
    onPrimaryContainer = SageGreen,

    secondaryContainer = Color(0xFFF4E2C1),
    onSecondaryContainer = TextPrimary,

    surfaceVariant = SageGreenLight,
    onSurfaceVariant = TextSecondary,

    onPrimary = SurfaceWhite,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
)

@Composable
fun SmartShoppingTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
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

    val view = LocalContext.current as Activity

    SideEffect {
        WindowCompat.setDecorFitsSystemWindows(
            view.window,
            false
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}