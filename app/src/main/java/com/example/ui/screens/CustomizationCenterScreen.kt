package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SystemStats
import com.example.model.*
import com.example.ui.components.LiquidGlassSurface
import com.example.ui.components.MainClockView
import com.example.ui.components.TerminalSignatureHeader
import com.example.ui.theme.*

enum class SettingsSection(val title: String, val icon: String) {
    THEME("Theme", "🎨"),
    CLOCK("Clock", "⏱"),
    GLASS("Glass", "💧"),
    TERMINAL("Prompt", ">_"),
    HOME_DOCK("Home", "🏠"),
    DRAWER_ICONS("Icons", "◈"),
    WALLPAPER("Canvas", "🖼"),
    GESTURES("Gestures", "👆"),
    BACKUP("Backup", "💾")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomizationCenterScreen(
    config: LauncherConfig,
    systemStats: SystemStats,
    onConfigChange: ((LauncherConfig) -> LauncherConfig) -> Unit,
    onResetAll: () -> Unit,
    onExportJson: () -> String,
    onImportJson: (String) -> Boolean,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedSection by remember { mutableStateOf(SettingsSection.THEME) }
    var jsonDialogText by remember { mutableStateOf<String?>(null) }
    var isImportMode by remember { mutableStateOf(false) }

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

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF04080C).copy(alpha = 0.96f))
            .testTag("customization_center_screen")
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "EMOLAUNCHER // CONFIG",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = primaryAccent,
                            letterSpacing = 2.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "Real-time personalization engine",
                        style = MaterialTheme.typography.bodySmall.copy(color = EmoMutedGray, fontSize = 11.sp)
                    )
                }
                Text(
                    text = "[DONE]",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = secondaryAccent,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    modifier = Modifier
                        .clickable(onClick = onClose)
                        .padding(6.dp)
                        .testTag("customization_done_btn")
                )
            }

            // Live Preview Card
            LiquidGlassSurface(
                config = config,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .padding(vertical = 6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        TerminalSignatureHeader(
                            config = config,
                            systemStats = systemStats,
                            onClick = {}
                        )
                        MainClockView(
                            config = config.copy(clockFontSize = config.clockFontSize * 0.75f),
                            systemStats = systemStats
                        )
                    }
                }
            }

            // Section Selector Tabs
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(SettingsSection.values()) { section ->
                    val isSelected = section == selectedSection
                    LiquidGlassSurface(
                        config = config,
                        shape = RoundedCornerShape(12.dp),
                        customColor = if (isSelected) primaryAccent.copy(alpha = 0.25f) else null,
                        onClick = { selectedSection = section }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(text = section.icon, fontSize = 12.sp)
                            Text(
                                text = section.title,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isSelected) EmoSoftWhite else EmoMutedGray,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        }
                    }
                }
            }

            // Settings Controls Content
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                when (selectedSection) {
                    SettingsSection.THEME -> {
                        item {
                            SectionHeader(title = "THEME & COLOR ENGINE")
                            SettingSegment(
                                label = "Theme Mode",
                                current = config.themeMode.name,
                                options = ThemeMode.values().map { it.name },
                                onSelect = { onConfigChange { cfg -> cfg.copy(themeMode = ThemeMode.valueOf(it)) } }
                            )
                            SettingToggle(
                                label = "Pure OLED Deep Black (#000000)",
                                checked = config.isOledPureBlack,
                                onCheckedChange = { checked -> onConfigChange { it.copy(isOledPureBlack = checked) } }
                            )
                            SettingColorPalette(
                                label = "Primary Accent Color",
                                currentHex = config.primaryAccentHex,
                                options = listOf("#00F0FF", "#A6FF00", "#FFB703", "#FF007F", "#8338EC", "#00FF66", "#E6E9EA"),
                                onSelect = { onConfigChange { cfg -> cfg.copy(primaryAccentHex = it) } }
                            )
                            SettingColorPalette(
                                label = "Secondary Accent Color",
                                currentHex = config.secondaryAccentHex,
                                options = listOf("#A6FF00", "#00F0FF", "#FB8500", "#FF4D6D", "#00FFCC", "#E6E9EA"),
                                onSelect = { onConfigChange { cfg -> cfg.copy(secondaryAccentHex = it) } }
                            )
                        }
                    }
                    SettingsSection.CLOCK -> {
                        item {
                            SectionHeader(title = "CLOCK & DATE SYSTEM")
                            SettingSegment(
                                label = "Clock Typography Style",
                                current = config.clockStyle.name,
                                options = ClockStyle.values().map { it.name },
                                onSelect = { onConfigChange { cfg -> cfg.copy(clockStyle = ClockStyle.valueOf(it)) } }
                            )
                            SettingToggle(
                                label = "24-Hour Format",
                                checked = config.clockFormat24h,
                                onCheckedChange = { checked -> onConfigChange { it.copy(clockFormat24h = checked) } }
                            )
                            SettingToggle(
                                label = "Display Seconds",
                                checked = config.clockShowSeconds,
                                onCheckedChange = { checked -> onConfigChange { it.copy(clockShowSeconds = checked) } }
                            )
                            SettingToggle(
                                label = "Display Date",
                                checked = config.clockShowDate,
                                onCheckedChange = { checked -> onConfigChange { it.copy(clockShowDate = checked) } }
                            )
                            SettingToggle(
                                label = "Display Weekday",
                                checked = config.clockShowWeekday,
                                onCheckedChange = { checked -> onConfigChange { it.copy(clockShowWeekday = checked) } }
                            )
                            SettingToggle(
                                label = "Orbital Second Dot (Orbital Clock)",
                                checked = config.clockShowOrbitalDot,
                                onCheckedChange = { checked -> onConfigChange { it.copy(clockShowOrbitalDot = checked) } }
                            )
                            SettingSlider(
                                label = "Clock Size Scale (${String.format("%.2f", config.clockFontSize)}x)",
                                value = config.clockFontSize,
                                range = 0.7f..1.5f,
                                onValueChange = { scale -> onConfigChange { it.copy(clockFontSize = scale) } }
                            )
                        }
                    }
                    SettingsSection.GLASS -> {
                        item {
                            SectionHeader(title = "LIQUID GLASS MATERIAL LAYER")
                            SettingSegment(
                                label = "Glass Blend Mode",
                                current = config.glassMode.name,
                                options = GlassMode.values().map { it.name },
                                onSelect = { onConfigChange { cfg -> cfg.copy(glassMode = GlassMode.valueOf(it)) } }
                            )
                            SettingSlider(
                                label = "Glass Opacity (${(config.glassOpacity * 100).toInt()}%)",
                                value = config.glassOpacity,
                                range = 0.1f..0.95f,
                                onValueChange = { op -> onConfigChange { it.copy(glassOpacity = op) } }
                            )
                            SettingSlider(
                                label = "Backdrop Blur Radius (${config.glassBlurRadius.toInt()}dp)",
                                value = config.glassBlurRadius,
                                range = 0f..48f,
                                onValueChange = { r -> onConfigChange { it.copy(glassBlurRadius = r) } }
                            )
                            SettingSlider(
                                label = "Corner Radius (${config.glassCornerRadius.toInt()}dp)",
                                value = config.glassCornerRadius,
                                range = 4f..36f,
                                onValueChange = { cr -> onConfigChange { it.copy(glassCornerRadius = cr) } }
                            )
                            SettingToggle(
                                label = "Specular Edge Highlight",
                                checked = config.glassEdgeHighlight,
                                onCheckedChange = { checked -> onConfigChange { it.copy(glassEdgeHighlight = checked) } }
                            )
                            SettingToggle(
                                label = "Refraction Shimmer Animation",
                                checked = config.glassRefractionShimmer,
                                onCheckedChange = { checked -> onConfigChange { it.copy(glassRefractionShimmer = checked) } }
                            )
                        }
                    }
                    SettingsSection.TERMINAL -> {
                        item {
                            SectionHeader(title = "TERMINAL SIGNATURE & PROMPT")
                            SettingToggle(
                                label = "Show Terminal Prompt on Home Screen",
                                checked = config.showTerminalSignature,
                                onCheckedChange = { checked -> onConfigChange { it.copy(showTerminalSignature = checked) } }
                            )
                            SettingInput(
                                label = "Prompt User Name",
                                value = config.terminalPromptUser,
                                onValueChange = { u -> onConfigChange { it.copy(terminalPromptUser = u) } }
                            )
                            SettingInput(
                                label = "Prompt Host Name",
                                value = config.terminalPromptHost,
                                onValueChange = { h -> onConfigChange { it.copy(terminalPromptHost = h) } }
                            )
                            SettingInput(
                                label = "Working Directory",
                                value = config.terminalPromptDir,
                                onValueChange = { d -> onConfigChange { it.copy(terminalPromptDir = d) } }
                            )
                            SettingInput(
                                label = "Custom Banner Override",
                                value = config.terminalCustomMessage,
                                placeholder = "Optional (e.g. 'SECURE PROTOCOL ACTIVE')",
                                onValueChange = { m -> onConfigChange { it.copy(terminalCustomMessage = m) } }
                            )
                        }
                    }
                    SettingsSection.HOME_DOCK -> {
                        item {
                            SectionHeader(title = "HOME SCREEN & DOCK")
                            SettingSlider(
                                label = "Pinned Quick Apps Count (${config.homeAppCount})",
                                value = config.homeAppCount.toFloat(),
                                range = 0f..5f,
                                onValueChange = { count -> onConfigChange { it.copy(homeAppCount = count.toInt()) } }
                            )
                            SettingToggle(
                                label = "Show Universal Search Pill",
                                checked = config.showSearchBarOnHome,
                                onCheckedChange = { checked -> onConfigChange { it.copy(showSearchBarOnHome = checked) } }
                            )
                            SettingToggle(
                                label = "Show System Stats HUD",
                                checked = config.showSystemHudOnHome,
                                onCheckedChange = { checked -> onConfigChange { it.copy(showSystemHudOnHome = checked) } }
                            )
                            SettingToggle(
                                label = "Show Music Control Widget",
                                checked = config.showMusicWidgetOnHome,
                                onCheckedChange = { checked -> onConfigChange { it.copy(showMusicWidgetOnHome = checked) } }
                            )
                            SettingSegment(
                                label = "Dock Container Style",
                                current = config.dockStyle.name,
                                options = DockStyle.values().map { it.name },
                                onSelect = { onConfigChange { cfg -> cfg.copy(dockStyle = DockStyle.valueOf(it)) } }
                            )
                            SettingSlider(
                                label = "Dock Apps Count (${config.dockAppCount})",
                                value = config.dockAppCount.toFloat(),
                                range = 0f..7f,
                                onValueChange = { c -> onConfigChange { it.copy(dockAppCount = c.toInt()) } }
                            )
                        }
                    }
                    SettingsSection.DRAWER_ICONS -> {
                        item {
                            SectionHeader(title = "APP DRAWER & ICON PACK")
                            SettingSegment(
                                label = "Icon Pack Aesthetic",
                                current = config.iconStyle.name,
                                options = IconStyle.values().map { it.name },
                                onSelect = { onConfigChange { cfg -> cfg.copy(iconStyle = IconStyle.valueOf(it)) } }
                            )
                            SettingSlider(
                                label = "Icon Size (${String.format("%.2f", config.iconSizeMultiplier)}x)",
                                value = config.iconSizeMultiplier,
                                range = 0.7f..1.3f,
                                onValueChange = { s -> onConfigChange { it.copy(iconSizeMultiplier = s) } }
                            )
                            SettingToggle(
                                label = "Display App Labels",
                                checked = config.iconShowLabels,
                                onCheckedChange = { checked -> onConfigChange { it.copy(iconShowLabels = checked) } }
                            )
                            SettingSegment(
                                label = "Drawer Layout",
                                current = config.drawerLayout.name,
                                options = DrawerLayout.values().map { it.name },
                                onSelect = { onConfigChange { cfg -> cfg.copy(drawerLayout = DrawerLayout.valueOf(it)) } }
                            )
                            SettingSlider(
                                label = "Grid Columns (${config.drawerGridColumns})",
                                value = config.drawerGridColumns.toFloat(),
                                range = 3f..6f,
                                onValueChange = { cols -> onConfigChange { it.copy(drawerGridColumns = cols.toInt()) } }
                            )
                        }
                    }
                    SettingsSection.WALLPAPER -> {
                        item {
                            SectionHeader(title = "WALLPAPER CANVAS & PERFORMANCE")
                            SettingSegment(
                                label = "Canvas Wallpaper Texture",
                                current = config.wallpaperType.name,
                                options = WallpaperType.values().map { it.name },
                                onSelect = { onConfigChange { cfg -> cfg.copy(wallpaperType = WallpaperType.valueOf(it)) } }
                            )
                            SettingSegment(
                                label = "Performance Engine Profile",
                                current = config.performanceMode.name,
                                options = PerformanceMode.values().map { it.name },
                                onSelect = { onConfigChange { cfg -> cfg.copy(performanceMode = PerformanceMode.valueOf(it)) } }
                            )
                        }
                    }
                    SettingsSection.GESTURES -> {
                        item {
                            SectionHeader(title = "GESTURE CONTROL SHORTCUTS")
                            SettingSegment(
                                label = "Swipe Up",
                                current = config.gestureSwipeUp.name,
                                options = GestureAction.values().map { it.name },
                                onSelect = { onConfigChange { cfg -> cfg.copy(gestureSwipeUp = GestureAction.valueOf(it)) } }
                            )
                            SettingSegment(
                                label = "Swipe Down",
                                current = config.gestureSwipeDown.name,
                                options = GestureAction.values().map { it.name },
                                onSelect = { onConfigChange { cfg -> cfg.copy(gestureSwipeDown = GestureAction.valueOf(it)) } }
                            )
                            SettingSegment(
                                label = "Double Tap",
                                current = config.gestureDoubleTap.name,
                                options = GestureAction.values().map { it.name },
                                onSelect = { onConfigChange { cfg -> cfg.copy(gestureDoubleTap = GestureAction.valueOf(it)) } }
                            )
                            SettingSegment(
                                label = "Long Press",
                                current = config.gestureLongPress.name,
                                options = GestureAction.values().map { it.name },
                                onSelect = { onConfigChange { cfg -> cfg.copy(gestureLongPress = GestureAction.valueOf(it)) } }
                            )
                        }
                    }
                    SettingsSection.BACKUP -> {
                        item {
                            SectionHeader(title = "BACKUP, RESTORE & OPEN SOURCE")
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        jsonDialogText = onExportJson()
                                        isImportMode = false
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = primaryAccent),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Export JSON", color = Color.Black, fontWeight = FontWeight.Bold)
                                }
                                OutlinedButton(
                                    onClick = {
                                        jsonDialogText = ""
                                        isImportMode = true
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Import JSON", color = EmoSoftWhite)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedButton(
                                onClick = onResetAll,
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF4D6D)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Reset All Settings to Factory Default")
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            LiquidGlassSurface(
                                config = config,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = "EMOLAUNCHER v1.0.0",
                                        style = MaterialTheme.typography.titleMedium.copy(color = primaryAccent)
                                    )
                                    Text(
                                        text = "Production-grade, Open-Source Android launcher layer with Liquid Glass, terminal precision, and Gemini AI search grounding.",
                                        style = MaterialTheme.typography.bodySmall.copy(color = EmoMutedGray, fontSize = 11.sp),
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                    Text(
                                        text = "License: MIT / Apache 2.0 • Zero Telemetry • 100% On-Device & Private",
                                        style = MaterialTheme.typography.labelSmall.copy(color = secondaryAccent, fontSize = 10.sp),
                                        modifier = Modifier.padding(top = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Backup JSON Dialog
        if (jsonDialogText != null) {
            var dialogInput by remember { mutableStateOf(jsonDialogText ?: "") }
            AlertDialog(
                onDismissRequest = { jsonDialogText = null },
                title = {
                    Text(
                        text = if (isImportMode) "Import Configuration" else "Exported Configuration",
                        style = MaterialTheme.typography.titleMedium.copy(color = primaryAccent)
                    )
                },
                text = {
                    OutlinedTextField(
                        value = dialogInput,
                        onValueChange = { dialogInput = it },
                        readOnly = !isImportMode,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = EmoSoftWhite,
                            unfocusedTextColor = EmoSoftWhite
                        )
                    )
                },
                confirmButton = {
                    if (isImportMode) {
                        TextButton(
                            onClick = {
                                onImportJson(dialogInput)
                                jsonDialogText = null
                            }
                        ) {
                            Text("Apply", color = primaryAccent)
                        }
                    } else {
                        TextButton(onClick = { jsonDialogText = null }) {
                            Text("Close", color = primaryAccent)
                        }
                    }
                },
                dismissButton = {
                    TextButton(onClick = { jsonDialogText = null }) {
                        Text("Cancel", color = EmoMutedGray)
                    }
                },
                containerColor = Color(0xFF071015)
            )
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = "// $title",
        style = MaterialTheme.typography.labelSmall.copy(
            color = EmoCyan,
            letterSpacing = 1.5.sp,
            fontWeight = FontWeight.Bold
        ),
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

@Composable
private fun SettingToggle(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(color = EmoSoftWhite, fontSize = 13.sp),
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = EmoCyan,
                checkedTrackColor = EmoCyan.copy(alpha = 0.3f),
                uncheckedThumbColor = EmoMutedGray,
                uncheckedTrackColor = Color(0xFF101B24)
            )
        )
    }
}

@Composable
private fun SettingSlider(
    label: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(color = EmoSoftWhite, fontSize = 12.sp)
        )
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            colors = SliderDefaults.colors(
                thumbColor = EmoCyan,
                activeTrackColor = EmoCyan,
                inactiveTrackColor = Color(0xFF101B24)
            )
        )
    }
}

@Composable
private fun SettingSegment(
    label: String,
    current: String,
    options: List<String>,
    onSelect: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(color = EmoSoftWhite, fontSize = 12.sp),
            modifier = Modifier.padding(bottom = 6.dp)
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(options) { opt ->
                val isSelected = opt == current
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) EmoCyan.copy(alpha = 0.25f) else Color(0xFF0C1720))
                        .clickable { onSelect(opt) }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = opt.replace("_", " "),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (isSelected) EmoCyan else EmoMutedGray,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 10.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingInput(
    label: String,
    value: String,
    placeholder: String = "",
    onValueChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(color = EmoSoftWhite, fontSize = 12.sp),
            modifier = Modifier.padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, fontSize = 12.sp, color = EmoMutedGray) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = EmoCyan,
                unfocusedBorderColor = Color(0xFF162530),
                focusedTextColor = EmoSoftWhite,
                unfocusedTextColor = EmoSoftWhite
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun SettingColorPalette(
    label: String,
    currentHex: String,
    options: List<String>,
    onSelect: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(color = EmoSoftWhite, fontSize = 12.sp),
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            options.forEach { hex ->
                val col = try {
                    Color(android.graphics.Color.parseColor(hex))
                } catch (e: Exception) {
                    Color.White
                }
                val isSelected = hex.equals(currentHex, ignoreCase = true)
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(col)
                        .clickable { onSelect(hex) },
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color.Black)
                        )
                    }
                }
            }
        }
    }
}
