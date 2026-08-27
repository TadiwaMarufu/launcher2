package com.example.model

/**
 * Complete configurable state for EmoLauncher.
 */
data class LauncherConfig(
    // Visual & Theme
    val themeMode: ThemeMode = ThemeMode.EMO_TERMINAL,
    val primaryAccentHex: String = "#00D1FF", // Electric cyan
    val secondaryAccentHex: String = "#A3E635", // Neon lime
    val isOledPureBlack: Boolean = true,
    
    // Liquid & Frosted Glass Material System
    val glassMode: GlassMode = GlassMode.FROSTED,
    val glassBlurRadius: Float = 24f, // dp blur
    val glassOpacity: Float = 0.55f, // 0.0 - 1.0
    val glassCornerRadius: Float = 28f,
    val glassEdgeHighlight: Boolean = true,
    val glassNoiseEnabled: Boolean = true,
    val glassRefractionShimmer: Boolean = true,
    
    // Terminal Signature & Header
    val showTerminalSignature: Boolean = true,
    val terminalPromptUser: String = "emo",
    val terminalPromptHost: String = "kali",
    val terminalPromptDir: String = "~",
    val terminalCustomMessage: String = "",
    val terminalSignatureAction: GestureAction = GestureAction.OPEN_TERMINAL,
    
    // Clock & Date System
    val clockStyle: ClockStyle = ClockStyle.ORBITAL_CIRCLE,
    val clockFormat24h: Boolean = true,
    val clockShowSeconds: Boolean = true,
    val clockShowDate: Boolean = true,
    val clockShowWeekday: Boolean = true,
    val clockShowBattery: Boolean = true,
    val clockShowWeather: Boolean = true,
    val clockShowOrbitalDot: Boolean = true,
    val clockFontSize: Float = 1.0f,
    val clockColorHex: String? = null,
    
    // Home Screen & Shortcuts
    val homeAppCount: Int = 3, // 0 to 5 or custom
    val pinnedHomePackages: List<String> = listOf("com.termux", "com.android.documentsui", "com.google.android.keep"),
    val showSearchBarOnHome: Boolean = true,
    val showSystemHudOnHome: Boolean = true,
    val showMusicWidgetOnHome: Boolean = true,
    
    // Dock Configuration
    val dockStyle: DockStyle = DockStyle.LIQUID_GLASS,
    val dockAppCount: Int = 4, // 0 to 7
    val dockPackages: List<String> = listOf(
        "com.android.chrome",
        "com.google.android.apps.messaging",
        "com.google.android.dialer",
        "com.google.android.apps.photos"
    ),
    val dockAutoHide: Boolean = false,
    
    // App Drawer
    val drawerLayout: DrawerLayout = DrawerLayout.CATEGORIES_GRID,
    val drawerShowCategories: Boolean = true,
    val drawerShowSearchBar: Boolean = true,
    val drawerShowRecentApps: Boolean = true,
    val drawerGridColumns: Int = 4,
    
    // Icon Configuration
    val iconStyle: IconStyle = IconStyle.NEO_TERMINAL,
    val iconSizeMultiplier: Float = 1.0f,
    val iconShowLabels: Boolean = true,
    val iconMonochromeTint: Boolean = false,
    
    // Wallpaper & Animation
    val wallpaperType: WallpaperType = WallpaperType.TERMINAL_GRID,
    val performanceMode: PerformanceMode = PerformanceMode.VISUAL,
    val animationSpeedMultiplier: Float = 1.0f,
    val enableParallax: Boolean = true,
    
    // Gestures
    val gestureSwipeUp: GestureAction = GestureAction.OPEN_DRAWER,
    val gestureSwipeDown: GestureAction = GestureAction.OPEN_SEARCH,
    val gestureDoubleTap: GestureAction = GestureAction.OPEN_COMMAND_PALETTE,
    val gestureLongPress: GestureAction = GestureAction.OPEN_CUSTOMIZATION,
    val gesturePinch: GestureAction = GestureAction.OPEN_SETTINGS,
    
    // Haptics & Sounds
    val hapticLevel: HapticLevel = HapticLevel.LIGHT,
    val enableKeySounds: Boolean = false,
    
    // Privacy
    val hiddenPackages: Set<String> = emptySet(),
    val hiddenAppsPin: String? = null,
    val disableUsageStats: Boolean = false
)
