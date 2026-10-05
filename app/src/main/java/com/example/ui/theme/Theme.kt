package com.example.ui.theme

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

private val DarkColorScheme = darkColorScheme(
    primary = DiscordBlurpleLight,
    onPrimary = Color.White,
    primaryContainer = DiscordBlurpleDark,
    onPrimaryContainer = Color.White,
    secondary = IvePink,
    onSecondary = Color.White,
    secondaryContainer = IvePurple,
    onSecondaryContainer = IvePinkLight,
    tertiary = IveViolet,
    onTertiary = Color.White,
    background = DiscordDarkBg,
    onBackground = DiscordTextPrimary,
    surface = DiscordDarkSurface,
    onSurface = DiscordTextPrimary,
    surfaceVariant = DiscordDarkCard,
    onSurfaceVariant = DiscordTextSecondary,
    outline = DiscordDarkBorder,
    error = StatusDnd,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = DiscordBlurple,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDCE2FF),
    onPrimaryContainer = DiscordBlurpleDark,
    secondary = IveRose,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFD9E2),
    onSecondaryContainer = Color(0xFF5E1133),
    tertiary = IveViolet,
    onTertiary = Color.White,
    background = DiscordLightBg,
    onBackground = DiscordLightText,
    surface = DiscordLightSurface,
    onSurface = DiscordLightText,
    surfaceVariant = DiscordLightCard,
    onSurfaceVariant = Color(0xFF4E5058),
    outline = Color(0xFFD1D3D8),
    error = StatusDnd,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to Discord dark aesthetic
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
