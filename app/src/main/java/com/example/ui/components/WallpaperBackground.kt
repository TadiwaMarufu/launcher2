package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.model.LauncherConfig
import com.example.model.WallpaperType
import com.example.ui.theme.EmoCyan
import com.example.ui.theme.EmoDarkBlueBlack
import com.example.ui.theme.EmoLime
import com.example.ui.theme.EmoNearBlack

@Composable
fun WallpaperBackground(
    modifier: Modifier = Modifier,
    config: LauncherConfig = LauncherConfig()
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

    val infiniteTransition = rememberInfiniteTransition(label = "wallpaper_ambient")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val baseBackground = if (config.isOledPureBlack) Color.Black else EmoNearBlack

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(baseBackground)
    ) {
        when (config.wallpaperType) {
            WallpaperType.TERMINAL_GRID -> {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val gridSpacing = 48f
                    val dotRadius = 1.2f
                    val dotColor = Color.White.copy(alpha = 0.045f)
                    val accentColor = primaryAccent.copy(alpha = 0.08f)

                    // Draw subtle grid points
                    var x = 0f
                    while (x < size.width) {
                        var y = 0f
                        while (y < size.height) {
                            val isAccent = ((x / gridSpacing).toInt() + (y / gridSpacing).toInt()) % 11 == 0
                            drawCircle(
                                color = if (isAccent) accentColor else dotColor,
                                radius = if (isAccent) dotRadius * 1.5f else dotRadius,
                                center = Offset(x, y)
                            )
                            y += gridSpacing
                        }
                        x += gridSpacing
                    }

                    // Subtle radial ambient glow at center
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                primaryAccent.copy(alpha = 0.03f * pulse),
                                Color.Transparent
                            ),
                            center = Offset(size.width * 0.5f, size.height * 0.42f),
                            radius = size.width * 0.75f
                        )
                    )
                }
            }
            WallpaperType.AMBIENT_PARTICLES -> {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val particleCount = 28
                    val rand = java.util.Random(42)
                    for (i in 0 until particleCount) {
                        val px = rand.nextFloat() * size.width
                        val py = rand.nextFloat() * size.height
                        val radius = 1f + rand.nextFloat() * 2.2f
                        val alpha = 0.03f + rand.nextFloat() * 0.07f
                        val col = if (i % 3 == 0) primaryAccent else if (i % 5 == 0) secondaryAccent else Color.White
                        drawCircle(
                            color = col.copy(alpha = alpha),
                            radius = radius,
                            center = Offset(px, py)
                        )
                    }
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                primaryAccent.copy(alpha = 0.04f * pulse),
                                Color.Transparent
                            ),
                            center = Offset(size.width * 0.5f, size.height * 0.35f),
                            radius = size.width * 0.6f
                        )
                    )
                }
            }
            WallpaperType.CYBER_GRADIENT -> {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                primaryAccent.copy(alpha = 0.07f * pulse),
                                secondaryAccent.copy(alpha = 0.03f),
                                Color.Transparent
                            ),
                            center = Offset(size.width * 0.3f, size.height * 0.25f),
                            radius = size.width * 0.9f
                        )
                    )
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                secondaryAccent.copy(alpha = 0.05f),
                                Color.Transparent
                            ),
                            center = Offset(size.width * 0.8f, size.height * 0.75f),
                            radius = size.width * 0.7f
                        )
                    )
                }
            }
            WallpaperType.DEEP_OLED -> {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                primaryAccent.copy(alpha = 0.02f),
                                Color.Transparent
                            ),
                            center = Offset(size.width * 0.5f, size.height * 0.4f),
                            radius = size.width * 0.5f
                        )
                    )
                }
            }
            WallpaperType.SYSTEM_WALLPAPER -> {
                // Background remains minimal dark overlay
            }
        }
    }
}
