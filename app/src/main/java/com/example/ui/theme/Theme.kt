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
import com.example.model.LauncherConfig
import com.example.model.ThemeMode

@Composable
fun MyApplicationTheme(
    config: LauncherConfig = LauncherConfig(),
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val primaryAccent = try {
        Color(android.graphics.Color.parseColor(config.primaryAccentHex))
    } catch (e: Exception) {
        EmoCyan
    }

    val secondaryAccent = try {
        Color(android.graphics.Color.parseColor(config.secondaryAccentHex))
    } catch (e: Exception) {
        EmoLime
    }

    val colorScheme = when (config.themeMode) {
        ThemeMode.MATERIAL_YOU -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            } else {
                darkColorScheme(
                    primary = primaryAccent,
                    secondary = secondaryAccent,
                    background = if (config.isOledPureBlack) Color.Black else EmoNearBlack,
                    surface = EmoDarkBlueBlack,
                    onPrimary = Color.Black,
                    onBackground = EmoSoftWhite,
                    onSurface = EmoSoftWhite
                )
            }
        }
        ThemeMode.MONOCHROME -> {
            darkColorScheme(
                primary = EmoSoftWhite,
                secondary = EmoMutedGray,
                tertiary = EmoMutedGray,
                background = if (config.isOledPureBlack) Color.Black else EmoNearBlack,
                surface = EmoDarkBlueBlack,
                onPrimary = Color.Black,
                onBackground = EmoSoftWhite,
                onSurface = EmoSoftWhite
            )
        }
        ThemeMode.SYSTEM -> {
            if (darkTheme) {
                darkColorScheme(
                    primary = primaryAccent,
                    secondary = secondaryAccent,
                    background = if (config.isOledPureBlack) Color.Black else EmoNearBlack,
                    surface = EmoDarkBlueBlack,
                    onPrimary = Color.Black,
                    onBackground = EmoSoftWhite,
                    onSurface = EmoSoftWhite
                )
            } else {
                lightColorScheme(
                    primary = primaryAccent,
                    secondary = secondaryAccent,
                    background = Color(0xFFF1F5F9),
                    surface = Color.White,
                    onPrimary = Color.White,
                    onBackground = Color(0xFF0F172A),
                    onSurface = Color(0xFF0F172A)
                )
            }
        }
        else -> { // EMO_TERMINAL & CUSTOM
            darkColorScheme(
                primary = primaryAccent,
                secondary = secondaryAccent,
                tertiary = EmoAmber,
                background = if (config.isOledPureBlack) Color.Black else EmoNearBlack,
                surface = EmoDarkBlueBlack,
                onPrimary = Color.Black,
                onSecondary = Color.Black,
                onBackground = EmoSoftWhite,
                onSurface = EmoSoftWhite,
                surfaceVariant = EmoSurfaceDark,
                outline = EmoSubtleBorder
            )
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
