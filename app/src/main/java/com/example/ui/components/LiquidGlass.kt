package com.example.ui.components

import android.os.Build
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.model.GlassMode
import com.example.model.LauncherConfig
import com.example.ui.theme.EmoCyan
import com.example.ui.theme.EmoDarkBlueBlack

/**
 * Liquid Glass physical material container.
 */
@Composable
fun LiquidGlassSurface(
    modifier: Modifier = Modifier,
    config: LauncherConfig = LauncherConfig(),
    shape: Shape = RoundedCornerShape(config.glassCornerRadius.dp),
    borderWidth: Dp = 1.dp,
    customColor: Color? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val isLiquid = config.glassMode == GlassMode.LIQUID
    val infiniteTransition = rememberInfiniteTransition(label = "glass_shimmer")
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    val primaryAccent = try {
        Color(android.graphics.Color.parseColor(config.primaryAccentHex))
    } catch (e: Exception) {
        EmoCyan
    }

    val baseAlpha = config.glassOpacity.coerceIn(0.1f, 0.95f)

    val backgroundColor = when (config.glassMode) {
        GlassMode.CLEAR -> Color(0xFF071015).copy(alpha = (baseAlpha * 0.4f).coerceIn(0.04f, 0.95f))
        GlassMode.DARK -> Color(0xFF03070A).copy(alpha = (baseAlpha * 1.1f).coerceIn(0.1f, 1f))
        GlassMode.FROSTED -> Color.White.copy(alpha = 0.055f)
        GlassMode.LIQUID -> Color.White.copy(alpha = 0.07f)
        GlassMode.SYSTEM -> MaterialTheme.colorScheme.surface.copy(alpha = baseAlpha)
    }

    val finalBg = customColor?.copy(alpha = baseAlpha) ?: backgroundColor

    val borderBrush = if (config.glassEdgeHighlight) {
        if (isLiquid && config.glassRefractionShimmer) {
            val offsetRatio = shimmerOffset
            Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.20f),
                    primaryAccent.copy(alpha = 0.35f),
                    Color.White.copy(alpha = 0.12f),
                    Color.White.copy(alpha = 0.04f)
                ),
                start = Offset(0f, 0f),
                end = Offset(400f * (1f + offsetRatio), 400f * (1f + offsetRatio))
            )
        } else {
            Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.18f),
                    Color.White.copy(alpha = 0.08f),
                    Color.White.copy(alpha = 0.03f)
                ),
                start = Offset(0f, 0f),
                end = Offset(800f, 800f)
            )
        }
    } else {
        Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.10f),
                Color.White.copy(alpha = 0.03f)
            )
        )
    }

    val clickModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick
        )
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .clip(shape)
            .then(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && config.glassBlurRadius > 0f) {
                    Modifier.blur(config.glassBlurRadius.dp)
                } else {
                    Modifier
                }
            )
            .background(finalBg, shape)
            .border(borderWidth, borderBrush, shape)
            .drawBehind {
                // Subtle top specular edge highlight line (from HTML design: w-full h-[1px] bg-gradient-to-r from-transparent via-white/20 to-transparent)
                drawLine(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.22f),
                            Color.Transparent
                        )
                    ),
                    start = Offset(0f, 0.5f),
                    end = Offset(size.width, 0.5f),
                    strokeWidth = 1.dp.toPx()
                )

                if (config.glassRefractionShimmer && isLiquid) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(primaryAccent.copy(alpha = 0.06f), Color.Transparent),
                            center = Offset(size.width * 0.2f, size.height * 0.1f),
                            radius = size.width * 0.8f
                        )
                    )
                }
            }
            .then(clickModifier),
        content = content
    )
}
