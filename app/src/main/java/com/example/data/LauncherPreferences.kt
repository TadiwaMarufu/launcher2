package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.*
import org.json.JSONArray
import org.json.JSONObject

class LauncherPreferences(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("emo_launcher_prefs", Context.MODE_PRIVATE)

    fun loadConfig(): LauncherConfig {
        return LauncherConfig(
            themeMode = ThemeMode.valueOf(prefs.getString("theme_mode", ThemeMode.EMO_TERMINAL.name) ?: ThemeMode.EMO_TERMINAL.name),
            primaryAccentHex = prefs.getString("primary_accent", "#00F0FF") ?: "#00F0FF",
            secondaryAccentHex = prefs.getString("secondary_accent", "#A6FF00") ?: "#A6FF00",
            isOledPureBlack = prefs.getBoolean("oled_pure_black", true),

            glassMode = GlassMode.valueOf(prefs.getString("glass_mode", GlassMode.LIQUID.name) ?: GlassMode.LIQUID.name),
            glassBlurRadius = prefs.getFloat("glass_blur", 24f),
            glassOpacity = prefs.getFloat("glass_opacity", 0.55f),
            glassCornerRadius = prefs.getFloat("glass_corner", 20f),
            glassEdgeHighlight = prefs.getBoolean("glass_edge", true),
            glassNoiseEnabled = prefs.getBoolean("glass_noise", true),
            glassRefractionShimmer = prefs.getBoolean("glass_shimmer", true),

            showTerminalSignature = prefs.getBoolean("show_terminal_sig", true),
            terminalPromptUser = prefs.getString("terminal_user", "emo") ?: "emo",
            terminalPromptHost = prefs.getString("terminal_host", "kali") ?: "kali",
            terminalPromptDir = prefs.getString("terminal_dir", "~") ?: "~",
            terminalCustomMessage = prefs.getString("terminal_msg", "") ?: "",
            terminalSignatureAction = GestureAction.valueOf(prefs.getString("terminal_action", GestureAction.OPEN_TERMINAL.name) ?: GestureAction.OPEN_TERMINAL.name),

            clockStyle = ClockStyle.valueOf(prefs.getString("clock_style", ClockStyle.ORBITAL_CIRCLE.name) ?: ClockStyle.ORBITAL_CIRCLE.name),
            clockFormat24h = prefs.getBoolean("clock_24h", true),
            clockShowSeconds = prefs.getBoolean("clock_seconds", true),
            clockShowDate = prefs.getBoolean("clock_date", true),
            clockShowWeekday = prefs.getBoolean("clock_weekday", true),
            clockShowBattery = prefs.getBoolean("clock_battery", true),
            clockShowWeather = prefs.getBoolean("clock_weather", true),
            clockShowOrbitalDot = prefs.getBoolean("clock_orbital_dot", true),
            clockFontSize = prefs.getFloat("clock_font_size", 1.0f),
            clockColorHex = prefs.getString("clock_color", null),

            homeAppCount = prefs.getInt("home_app_count", 3),
            pinnedHomePackages = loadStringList("pinned_home", listOf("com.termux", "com.android.documentsui", "com.google.android.keep")),
            showSearchBarOnHome = prefs.getBoolean("show_search_home", true),
            showSystemHudOnHome = prefs.getBoolean("show_hud_home", true),
            showMusicWidgetOnHome = prefs.getBoolean("show_music_home", true),

            dockStyle = DockStyle.valueOf(prefs.getString("dock_style", DockStyle.LIQUID_GLASS.name) ?: DockStyle.LIQUID_GLASS.name),
            dockAppCount = prefs.getInt("dock_app_count", 4),
            dockPackages = loadStringList("dock_packages", listOf("com.android.chrome", "com.google.android.apps.messaging", "com.google.android.dialer", "com.google.android.apps.photos")),
            dockAutoHide = prefs.getBoolean("dock_auto_hide", false),

            drawerLayout = DrawerLayout.valueOf(prefs.getString("drawer_layout", DrawerLayout.CATEGORIES_GRID.name) ?: DrawerLayout.CATEGORIES_GRID.name),
            drawerShowCategories = prefs.getBoolean("drawer_categories", true),
            drawerShowSearchBar = prefs.getBoolean("drawer_search", true),
            drawerShowRecentApps = prefs.getBoolean("drawer_recent", true),
            drawerGridColumns = prefs.getInt("drawer_columns", 4),

            iconStyle = IconStyle.valueOf(prefs.getString("icon_style", IconStyle.NEO_TERMINAL.name) ?: IconStyle.NEO_TERMINAL.name),
            iconSizeMultiplier = prefs.getFloat("icon_size", 1.0f),
            iconShowLabels = prefs.getBoolean("icon_labels", true),
            iconMonochromeTint = prefs.getBoolean("icon_mono", false),

            wallpaperType = WallpaperType.valueOf(prefs.getString("wallpaper_type", WallpaperType.TERMINAL_GRID.name) ?: WallpaperType.TERMINAL_GRID.name),
            performanceMode = PerformanceMode.valueOf(prefs.getString("perf_mode", PerformanceMode.VISUAL.name) ?: PerformanceMode.VISUAL.name),
            animationSpeedMultiplier = prefs.getFloat("anim_speed", 1.0f),
            enableParallax = prefs.getBoolean("parallax", true),

            gestureSwipeUp = GestureAction.valueOf(prefs.getString("gesture_up", GestureAction.OPEN_DRAWER.name) ?: GestureAction.OPEN_DRAWER.name),
            gestureSwipeDown = GestureAction.valueOf(prefs.getString("gesture_down", GestureAction.OPEN_SEARCH.name) ?: GestureAction.OPEN_SEARCH.name),
            gestureDoubleTap = GestureAction.valueOf(prefs.getString("gesture_double", GestureAction.OPEN_COMMAND_PALETTE.name) ?: GestureAction.OPEN_COMMAND_PALETTE.name),
            gestureLongPress = GestureAction.valueOf(prefs.getString("gesture_long", GestureAction.OPEN_CUSTOMIZATION.name) ?: GestureAction.OPEN_CUSTOMIZATION.name),
            gesturePinch = GestureAction.valueOf(prefs.getString("gesture_pinch", GestureAction.OPEN_SETTINGS.name) ?: GestureAction.OPEN_SETTINGS.name),

            hapticLevel = HapticLevel.valueOf(prefs.getString("haptic_level", HapticLevel.LIGHT.name) ?: HapticLevel.LIGHT.name),
            enableKeySounds = prefs.getBoolean("key_sounds", false),

            hiddenPackages = prefs.getStringSet("hidden_packages", emptySet()) ?: emptySet(),
            hiddenAppsPin = prefs.getString("hidden_pin", null),
            disableUsageStats = prefs.getBoolean("disable_stats", false)
        )
    }

    fun saveConfig(config: LauncherConfig) {
        prefs.edit().apply {
            putString("theme_mode", config.themeMode.name)
            putString("primary_accent", config.primaryAccentHex)
            putString("secondary_accent", config.secondaryAccentHex)
            putBoolean("oled_pure_black", config.isOledPureBlack)

            putString("glass_mode", config.glassMode.name)
            putFloat("glass_blur", config.glassBlurRadius)
            putFloat("glass_opacity", config.glassOpacity)
            putFloat("glass_corner", config.glassCornerRadius)
            putBoolean("glass_edge", config.glassEdgeHighlight)
            putBoolean("glass_noise", config.glassNoiseEnabled)
            putBoolean("glass_shimmer", config.glassRefractionShimmer)

            putBoolean("show_terminal_sig", config.showTerminalSignature)
            putString("terminal_user", config.terminalPromptUser)
            putString("terminal_host", config.terminalPromptHost)
            putString("terminal_dir", config.terminalPromptDir)
            putString("terminal_msg", config.terminalCustomMessage)
            putString("terminal_action", config.terminalSignatureAction.name)

            putString("clock_style", config.clockStyle.name)
            putBoolean("clock_24h", config.clockFormat24h)
            putBoolean("clock_seconds", config.clockShowSeconds)
            putBoolean("clock_date", config.clockShowDate)
            putBoolean("clock_weekday", config.clockShowWeekday)
            putBoolean("clock_battery", config.clockShowBattery)
            putBoolean("clock_weather", config.clockShowWeather)
            putBoolean("clock_orbital_dot", config.clockShowOrbitalDot)
            putFloat("clock_font_size", config.clockFontSize)
            putString("clock_color", config.clockColorHex)

            putInt("home_app_count", config.homeAppCount)
            saveStringList("pinned_home", config.pinnedHomePackages)
            putBoolean("show_search_home", config.showSearchBarOnHome)
            putBoolean("show_hud_home", config.showSystemHudOnHome)
            putBoolean("show_music_home", config.showMusicWidgetOnHome)

            putString("dock_style", config.dockStyle.name)
            putInt("dock_app_count", config.dockAppCount)
            saveStringList("dock_packages", config.dockPackages)
            putBoolean("dock_auto_hide", config.dockAutoHide)

            putString("drawer_layout", config.drawerLayout.name)
            putBoolean("drawer_categories", config.drawerShowCategories)
            putBoolean("drawer_search", config.drawerShowSearchBar)
            putBoolean("drawer_recent", config.drawerShowRecentApps)
            putInt("drawer_columns", config.drawerGridColumns)

            putString("icon_style", config.iconStyle.name)
            putFloat("icon_size", config.iconSizeMultiplier)
            putBoolean("icon_labels", config.iconShowLabels)
            putBoolean("icon_mono", config.iconMonochromeTint)

            putString("wallpaper_type", config.wallpaperType.name)
            putString("perf_mode", config.performanceMode.name)
            putFloat("anim_speed", config.animationSpeedMultiplier)
            putBoolean("parallax", config.enableParallax)

            putString("gesture_up", config.gestureSwipeUp.name)
            putString("gesture_down", config.gestureSwipeDown.name)
            putString("gesture_double", config.gestureDoubleTap.name)
            putString("gesture_long", config.gestureLongPress.name)
            putString("gesture_pinch", config.gesturePinch.name)

            putString("haptic_level", config.hapticLevel.name)
            putBoolean("key_sounds", config.enableKeySounds)

            putStringSet("hidden_packages", config.hiddenPackages)
            putString("hidden_pin", config.hiddenAppsPin)
            putBoolean("disable_stats", config.disableUsageStats)
            apply()
        }
    }

    fun exportConfigToJson(config: LauncherConfig): String {
        val json = JSONObject()
        json.put("version", "1.0")
        json.put("themeMode", config.themeMode.name)
        json.put("primaryAccent", config.primaryAccentHex)
        json.put("secondaryAccent", config.secondaryAccentHex)
        json.put("glassMode", config.glassMode.name)
        json.put("glassBlur", config.glassBlurRadius)
        json.put("glassOpacity", config.glassOpacity)
        json.put("clockStyle", config.clockStyle.name)
        json.put("terminalUser", config.terminalPromptUser)
        json.put("terminalHost", config.terminalPromptHost)
        json.put("wallpaperType", config.wallpaperType.name)
        json.put("homeAppCount", config.homeAppCount)
        json.put("dockAppCount", config.dockAppCount)
        val homeArray = JSONArray()
        config.pinnedHomePackages.forEach { homeArray.put(it) }
        json.put("pinnedHome", homeArray)
        return json.toString(2)
    }

    fun importConfigFromJson(jsonStr: String): LauncherConfig? {
        return try {
            val json = JSONObject(jsonStr)
            val base = loadConfig()
            base.copy(
                themeMode = if (json.has("themeMode")) ThemeMode.valueOf(json.getString("themeMode")) else base.themeMode,
                primaryAccentHex = json.optString("primaryAccent", base.primaryAccentHex),
                secondaryAccentHex = json.optString("secondaryAccent", base.secondaryAccentHex),
                glassMode = if (json.has("glassMode")) GlassMode.valueOf(json.getString("glassMode")) else base.glassMode,
                glassBlurRadius = json.optDouble("glassBlur", base.glassBlurRadius.toDouble()).toFloat(),
                glassOpacity = json.optDouble("glassOpacity", base.glassOpacity.toDouble()).toFloat(),
                clockStyle = if (json.has("clockStyle")) ClockStyle.valueOf(json.getString("clockStyle")) else base.clockStyle,
                terminalPromptUser = json.optString("terminalUser", base.terminalPromptUser),
                terminalPromptHost = json.optString("terminalHost", base.terminalPromptHost),
                wallpaperType = if (json.has("wallpaperType")) WallpaperType.valueOf(json.getString("wallpaperType")) else base.wallpaperType
            )
        } catch (e: Exception) {
            null
        }
    }

    fun resetAll() {
        prefs.edit().clear().apply()
    }

    private fun loadStringList(key: String, default: List<String>): List<String> {
        val raw = prefs.getString(key, null) ?: return default
        return raw.split(",").filter { it.isNotBlank() }
    }

    private fun saveStringList(key: String, list: List<String>) {
        prefs.edit().putString(key, list.joinToString(",")).apply()
    }
}
