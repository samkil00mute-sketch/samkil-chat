package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = EmeraldBrand,
    secondary = TealLight,
    tertiary = TealDark,
    background = DarkSlateBg,
    surface = DarkSlateSurface,
    onPrimary = DarkSlateBg,
    onSecondary = DarkSlateBg,
    onBackground = OffWhiteBg,
    onSurface = OffWhiteBg,
    secondaryContainer = OutgoingBubbleDark,
    surfaceVariant = IncomingBubbleDark
)

private val LightColorScheme = lightColorScheme(
    primary = TealPrimary,
    secondary = TealDark,
    tertiary = EmeraldBrand,
    background = OffWhiteBg,
    surface = androidx.compose.ui.graphics.Color.White,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    onBackground = DarkSlateBg,
    onSurface = DarkSlateBg,
    secondaryContainer = OutgoingBubbleLight,
    surfaceVariant = IncomingBubbleLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Disable dynamic colors to enforce the specific branding theme (samkil chat look)
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
