package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.AppInfo
import com.example.model.DockStyle
import com.example.model.LauncherConfig
import com.example.ui.theme.EmoCyan

@Composable
fun DockBar(
    apps: List<AppInfo>,
    config: LauncherConfig = LauncherConfig(),
    modifier: Modifier = Modifier,
    onAppClick: (AppInfo) -> Unit,
    onAppLongClick: (AppInfo) -> Unit = {}
) {
    if (config.dockStyle == DockStyle.HIDDEN || config.dockAppCount == 0 || apps.isEmpty()) {
        return
    }

    val displayApps = apps.take(config.dockAppCount)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .testTag("dock_bar")
            .padding(horizontal = 24.dp, vertical = 6.dp)
            .windowInsetsPadding(WindowInsets.navigationBars),
        contentAlignment = Alignment.Center
    ) {
        when (config.dockStyle) {
            DockStyle.LIQUID_GLASS -> {
                LiquidGlassSurface(
                    config = config,
                    shape = RoundedCornerShape(26.dp),
                    modifier = Modifier.wrapContentWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        displayApps.forEach { app ->
                            AppIconItem(
                                app = app,
                                config = config.copy(iconShowLabels = false),
                                onClick = { onAppClick(app) },
                                onLongClick = { onAppLongClick(app) }
                            )
                        }
                    }
                }
            }
            DockStyle.FLOATING_PILL -> {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(32.dp))
                        .background(Color(0xFF091219).copy(alpha = 0.85f))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        displayApps.forEach { app ->
                            AppIconItem(
                                app = app,
                                config = config.copy(iconShowLabels = false),
                                onClick = { onAppClick(app) },
                                onLongClick = { onAppLongClick(app) }
                            )
                        }
                    }
                }
            }
            DockStyle.TRANSPARENT -> {
                Row(
                    modifier = Modifier.padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    displayApps.forEach { app ->
                        AppIconItem(
                            app = app,
                            config = config.copy(iconShowLabels = false),
                            onClick = { onAppClick(app) },
                            onLongClick = { onAppLongClick(app) }
                        )
                    }
                }
            }
            DockStyle.HIDDEN -> {}
        }
    }
}
