package com.example.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Represents an installed or simulated application.
 */
data class AppInfo(
    val packageName: String,
    val className: String = "",
    val label: String,
    val customLabel: String? = null,
    val category: AppCategory = AppCategory.TOOLS,
    val isFavorite: Boolean = false,
    val isHidden: Boolean = false,
    val isPinnedToHome: Boolean = false,
    val isPinnedToDock: Boolean = false,
    val installTime: Long = System.currentTimeMillis(),
    val usageCount: Int = 0,
    val customColorHex: String? = null,
    val terminalShortcut: String? = null
) {
    val displayLabel: String
        get() = customLabel?.takeIf { it.isNotBlank() } ?: label
}

enum class AppCategory(val title: String, val iconGlyph: String) {
    ALL("All", "◈"),
    DEV("Dev", ">_"),
    TOOLS("Tools", "⚒"),
    MEDIA("Media", "♫"),
    SOCIAL("Social", "◉"),
    SYSTEM("System", "⚙"),
    GAMES("Games", "⚄"),
    FAVORITES("Starred", "★"),
    HIDDEN("Private", "⚿")
}

enum class ThemeMode {
    SYSTEM,
    MATERIAL_YOU,
    EMO_TERMINAL,
    MONOCHROME,
    CUSTOM,
    AUTO
}

enum class GlassMode {
    CLEAR,
    DARK,
    FROSTED,
    LIQUID,
    SYSTEM
}

enum class ClockStyle {
    ORBITAL_CIRCLE,
    MINIMAL_TEXT,
    TERMINAL_UNIX,
    DIGITAL_BOLD,
    VERTICAL,
    ANALOG_HYBRID
}

enum class PerformanceMode {
    VISUAL,
    BALANCED,
    ULTRA_PERFORMANCE,
    LOW_POWER
}

enum class HapticLevel {
    OFF,
    LIGHT,
    MEDIUM,
    STRONG
}

enum class WallpaperType {
    TERMINAL_GRID,
    DEEP_OLED,
    AMBIENT_PARTICLES,
    CYBER_GRADIENT,
    SYSTEM_WALLPAPER
}

enum class DockStyle {
    LIQUID_GLASS,
    TRANSPARENT,
    FLOATING_PILL,
    HIDDEN
}

enum class DrawerLayout {
    CATEGORIES_GRID,
    COMPACT_GRID,
    ALPHABETICAL_LIST,
    MINIMAL_TEXT
}

enum class IconStyle {
    NEO_TERMINAL,
    ADAPTIVE_GLASS,
    MINIMAL_MONOCHROME,
    SYSTEM_STOCK
}

enum class GestureAction {
    NONE,
    OPEN_DRAWER,
    OPEN_SEARCH,
    OPEN_COMMAND_PALETTE,
    OPEN_CUSTOMIZATION,
    OPEN_TERMINAL,
    TOGGLE_FLASHLIGHT,
    OPEN_SETTINGS,
    OPEN_NOTIFICATIONS,
    LOCK_SCREEN,
    REFRESH_APPS
}
