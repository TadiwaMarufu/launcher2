package com.example.ui.screens

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SystemStats
import com.example.model.AppInfo
import com.example.model.GestureAction
import com.example.model.LauncherConfig
import com.example.ui.components.*
import com.example.ui.theme.EmoCyan
import com.example.ui.theme.EmoMutedGray

@Composable
fun HomeScreen(
    config: LauncherConfig,
    installedApps: List<AppInfo>,
    systemStats: SystemStats,
    onAppClick: (AppInfo) -> Unit,
    onAppLongClick: (AppInfo) -> Unit,
    onSearchTrigger: () -> Unit,
    onTerminalTrigger: () -> Unit,
    onCustomizationTrigger: () -> Unit,
    onGestureAction: (GestureAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val primaryAccent = try {
        Color(android.graphics.Color.parseColor(config.primaryAccentHex))
    } catch (e: Exception) {
        EmoCyan
    }

    val pinnedApps = remember(installedApps, config.pinnedHomePackages, config.homeAppCount) {
        val count = config.homeAppCount
        if (count == 0) emptyList() else {
            val pinned = installedApps.filter { config.pinnedHomePackages.contains(it.packageName) }
            if (pinned.isNotEmpty()) pinned.take(count) else installedApps.take(count)
        }
    }

    val dockApps = remember(installedApps, config.dockPackages, config.dockAppCount) {
        val count = config.dockAppCount
        if (count == 0) emptyList() else {
            val list = installedApps.filter { config.dockPackages.contains(it.packageName) }
            if (list.isNotEmpty()) list.take(count) else installedApps.take(count)
        }
    }

    var dragOffsetY by remember { mutableStateOf(0f) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen_root")
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = { onGestureAction(config.gestureDoubleTap) },
                    onLongPress = { onGestureAction(config.gestureLongPress) }
                )
            }
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onDragStart = { dragOffsetY = 0f },
                    onDragEnd = {
                        if (dragOffsetY < -50f) {
                            onGestureAction(config.gestureSwipeUp)
                        } else if (dragOffsetY > 50f) {
                            onGestureAction(config.gestureSwipeDown)
                        }
                        dragOffsetY = 0f
                    },
                    onVerticalDrag = { _, dragAmount ->
                        dragOffsetY += dragAmount
                    }
                )
            }
    ) {
        // Procedural minimal wallpaper background
        WallpaperBackground(config = config)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Section: Terminal Signature
            TerminalSignatureHeader(
                config = config,
                systemStats = systemStats,
                onClick = {
                    when (config.terminalSignatureAction) {
                        GestureAction.OPEN_TERMINAL -> onTerminalTrigger()
                        GestureAction.OPEN_SEARCH -> onSearchTrigger()
                        GestureAction.OPEN_CUSTOMIZATION -> onCustomizationTrigger()
                        else -> onTerminalTrigger()
                    }
                }
            )

            // Center Column: Clock, System HUD, Shortcuts, Search Pill
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Main Clock
                MainClockView(
                    config = config,
                    systemStats = systemStats,
                    onClick = onCustomizationTrigger
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Pinned Minimal Shortcuts
                if (pinnedApps.isNotEmpty()) {
                    Row(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        pinnedApps.forEach { app ->
                            TextAppShortcut(
                                app = app,
                                config = config,
                                onClick = { onAppClick(app) },
                                onLongClick = { onAppLongClick(app) }
                            )
                        }
                    }
                }

                // System HUD Bar
                if (config.showSystemHudOnHome) {
                    Spacer(modifier = Modifier.height(10.dp))
                    SystemHudBar(
                        config = config,
                        stats = systemStats
                    )
                }

                // Music Widget
                if (config.showMusicWidgetOnHome) {
                    Spacer(modifier = Modifier.height(8.dp))
                    MusicWidget(config = config)
                }

                // Universal Search Pill Trigger
                if (config.showSearchBarOnHome) {
                    Spacer(modifier = Modifier.height(14.dp))
                    val secondaryAccent = try {
                        Color(android.graphics.Color.parseColor(config.secondaryAccentHex))
                    } catch (e: Exception) {
                        Color(0xFFA3E635)
                    }

                    LiquidGlassSurface(
                        config = config,
                        shape = RoundedCornerShape(28.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .padding(horizontal = 24.dp)
                            .testTag("home_search_pill"),
                        onClick = onSearchTrigger
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 18.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "$",
                                    color = EmoMutedGray,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Search or command...",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = EmoMutedGray,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 13.sp
                                    )
                                )
                                Box(
                                    modifier = Modifier
                                        .size(width = 6.dp, height = 14.dp)
                                        .background(secondaryAccent)
                                )
                            }
                            Text(
                                text = "✦ AI",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = primaryAccent,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            )
                        }
                    }
                }
            }

            // Bottom Section: Dock Bar & Gesture Indicator
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                DockBar(
                    apps = dockApps,
                    config = config,
                    onAppClick = onAppClick,
                    onAppLongClick = onAppLongClick
                )

                // Gesture Bar indicator
                Box(
                    modifier = Modifier
                        .padding(bottom = 6.dp)
                        .width(72.dp)
                        .height(3.dp)
                        .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(2.dp))
                )
            }
        }
    }
}
