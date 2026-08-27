package com.example.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppCategory
import com.example.model.AppInfo
import com.example.model.IconStyle
import com.example.model.LauncherConfig
import com.example.ui.theme.EmoCyan
import com.example.ui.theme.EmoLime
import com.example.ui.theme.EmoMutedGray
import com.example.ui.theme.EmoSoftWhite

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppIconItem(
    app: AppInfo,
    config: LauncherConfig = LauncherConfig(),
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {}
) {
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

    val sizeMultiplier = config.iconSizeMultiplier

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .testTag("app_icon_${app.packageName}")
            .padding(vertical = 6.dp, horizontal = 4.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
    ) {
        AppIconGlyph(
            app = app,
            config = config,
            primaryAccent = primaryAccent,
            secondaryAccent = secondaryAccent,
            sizeMultiplier = sizeMultiplier
        )

        if (config.iconShowLabels) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = app.displayLabel,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = (11 * sizeMultiplier).sp,
                    color = if (config.iconMonochromeTint) EmoMutedGray else EmoSoftWhite,
                    fontWeight = FontWeight.Normal,
                    letterSpacing = 0.5.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun AppIconGlyph(
    app: AppInfo,
    config: LauncherConfig,
    primaryAccent: Color,
    secondaryAccent: Color,
    sizeMultiplier: Float
) {
    val iconSize = 48.dp * sizeMultiplier

    when (config.iconStyle) {
        IconStyle.NEO_TERMINAL -> {
            val glyph = when (app.category) {
                AppCategory.DEV -> ">_"
                AppCategory.TOOLS -> "⚒"
                AppCategory.MEDIA -> "♫"
                AppCategory.SOCIAL -> "◉"
                AppCategory.SYSTEM -> "⚙"
                AppCategory.GAMES -> "⚄"
                AppCategory.FAVORITES -> "★"
                else -> "◈"
            }

            Box(
                modifier = Modifier
                    .size(iconSize)
                    .clip(RoundedCornerShape(12.dp * sizeMultiplier))
                    .background(Color(0xFF091219))
                    .border(
                        1.dp,
                        Brush.linearGradient(
                            listOf(
                                primaryAccent.copy(alpha = 0.4f),
                                Color.White.copy(alpha = 0.1f)
                            )
                        ),
                        RoundedCornerShape(12.dp * sizeMultiplier)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = app.terminalShortcut?.take(2) ?: glyph,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = (14 * sizeMultiplier).sp,
                        color = if (app.category == AppCategory.DEV) secondaryAccent else primaryAccent
                    )
                )
            }
        }
        IconStyle.ADAPTIVE_GLASS -> {
            LiquidGlassSurface(
                config = config,
                modifier = Modifier.size(iconSize),
                shape = RoundedCornerShape(14.dp * sizeMultiplier)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = app.displayLabel.take(1).uppercase(),
                        style = MaterialTheme.typography.headlineSmall.copy(
                            color = EmoSoftWhite,
                            fontWeight = FontWeight.Light,
                            fontSize = (18 * sizeMultiplier).sp
                        )
                    )
                }
            }
        }
        IconStyle.MINIMAL_MONOCHROME -> {
            Box(
                modifier = Modifier
                    .size(iconSize)
                    .clip(CircleShape)
                    .background(Color(0xFF121E27))
                    .border(1.dp, Color(0xFF263845), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = app.displayLabel.take(2).uppercase(),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = EmoSoftWhite,
                        fontWeight = FontWeight.Medium,
                        fontSize = (12 * sizeMultiplier).sp
                    )
                )
            }
        }
        IconStyle.SYSTEM_STOCK -> {
            Box(
                modifier = Modifier
                    .size(iconSize)
                    .clip(RoundedCornerShape(12.dp * sizeMultiplier))
                    .background(Color(0xFF101B24)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = app.displayLabel.take(1).uppercase(),
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = primaryAccent,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

/**
 * Text-only shortcut for minimal home screen (e.g. `>_ terminal`, `□ files`, `▤ notes`)
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TextAppShortcut(
    app: AppInfo,
    config: LauncherConfig = LauncherConfig(),
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {}
) {
    val primaryAccent = try {
        Color(android.graphics.Color.parseColor(config.primaryAccentHex))
    } catch (e: Exception) {
        EmoCyan
    }

    val glyph = when (app.category) {
        AppCategory.DEV -> ">_"
        AppCategory.TOOLS -> "□"
        AppCategory.MEDIA -> "♫"
        AppCategory.SOCIAL -> "◉"
        else -> "▤"
    }

    LiquidGlassSurface(
        config = config,
        modifier = modifier
            .testTag("text_shortcut_${app.packageName}")
            .padding(vertical = 4.dp)
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = glyph,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = primaryAccent,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            )
            Text(
                text = app.displayLabel.lowercase(),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = EmoSoftWhite,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
            )
        }
    }
}
