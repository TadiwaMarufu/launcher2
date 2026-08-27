package com.example.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppRepository
import com.example.data.LauncherPreferences
import com.example.data.SystemInfoManager
import com.example.data.SystemStats
import com.example.data.db.LauncherDatabase
import com.example.data.db.LauncherNoteEntity
import com.example.gemini.GeminiService
import com.example.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class ActiveOverlay {
    object None : ActiveOverlay()
    object AppDrawer : ActiveOverlay()
    object UniversalSearch : ActiveOverlay()
    object CustomizationCenter : ActiveOverlay()
    object TerminalShell : ActiveOverlay()
}

data class LauncherUiState(
    val config: LauncherConfig = LauncherConfig(),
    val installedApps: List<AppInfo> = emptyList(),
    val filteredApps: List<AppInfo> = emptyList(),
    val selectedCategory: AppCategory = AppCategory.ALL,
    val activeOverlay: ActiveOverlay = ActiveOverlay.None,
    val searchQuery: String = "",
    val searchResults: List<SearchResult> = emptyList(),
    val systemStats: SystemStats = SystemStats(),
    val isSearchingGemini: Boolean = false,
    val geminiResponse: String? = null,
    val selectedAppForActions: AppInfo? = null,
    val toastMessage: String? = null,
    val customNotes: List<LauncherNoteEntity> = emptyList()
)

class LauncherViewModel(application: Application) : AndroidViewModel(application) {

    private val appRepo = AppRepository(application)
    private val sysInfoManager = SystemInfoManager(application)
    private val prefs = LauncherPreferences(application)
    private val geminiService = GeminiService()

    private val db = androidx.room.Room.databaseBuilder(
        application,
        LauncherDatabase::class.java,
        "emo_launcher.db"
    ).fallbackToDestructiveMigration().build()

    private val _uiState = MutableStateFlow(LauncherUiState(config = prefs.loadConfig()))
    val uiState: StateFlow<LauncherUiState> = _uiState.asStateFlow()

    init {
        loadApps()
        refreshSystemStats()
        observeNotes()
    }

    private fun observeNotes() {
        viewModelScope.launch {
            db.launcherDao().getAllNotes().collect { notes ->
                _uiState.update { it.copy(customNotes = notes) }
            }
        }
    }

    fun loadApps() {
        viewModelScope.launch {
            val apps = appRepo.getInstalledApps()
            _uiState.update { state ->
                val hidden = state.config.hiddenPackages
                val filtered = apps.filter { !hidden.contains(it.packageName) }
                state.copy(
                    installedApps = apps,
                    filteredApps = filtered
                )
            }
        }
    }

    fun refreshSystemStats() {
        viewModelScope.launch(Dispatchers.IO) {
            val stats = sysInfoManager.getSystemStats()
            _uiState.update { it.copy(systemStats = stats) }
        }
    }

    fun setOverlay(overlay: ActiveOverlay) {
        _uiState.update {
            it.copy(
                activeOverlay = overlay,
                searchQuery = if (overlay == ActiveOverlay.None) "" else it.searchQuery
            )
        }
        if (overlay == ActiveOverlay.UniversalSearch) {
            onSearchQueryChange(_uiState.value.searchQuery)
        }
    }

    fun closeOverlay() {
        _uiState.update { it.copy(activeOverlay = ActiveOverlay.None, searchQuery = "", searchResults = emptyList()) }
    }

    fun setSelectedCategory(category: AppCategory) {
        _uiState.update { state ->
            val hidden = state.config.hiddenPackages
            val filtered = when (category) {
                AppCategory.ALL -> state.installedApps.filter { !hidden.contains(it.packageName) }
                AppCategory.FAVORITES -> state.installedApps.filter { it.isFavorite && !hidden.contains(it.packageName) }
                AppCategory.HIDDEN -> state.installedApps.filter { hidden.contains(it.packageName) }
                else -> state.installedApps.filter { it.category == category && !hidden.contains(it.packageName) }
            }
            state.copy(selectedCategory = category, filteredApps = filtered)
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { state ->
            val results = mutableListOf<SearchResult>()
            val q = query.trim()

            if (q.isNotBlank()) {
                // 1. Check if Math expression (e.g. calc 24*9 or 12+5)
                val calcResult = evaluateMath(q)
                if (calcResult != null) {
                    results.add(SearchResult.MathCalculation(q, calcResult))
                }

                // 2. Developer / System Commands (e.g. > wifi, > term, > battery, > flash)
                if (q.startsWith(">") || q.startsWith("$")) {
                    val cmdText = q.removePrefix(">").removePrefix("$").trim().lowercase()
                    getAvailableCommands().filter {
                        it.command.lowercase().contains(cmdText) || it.description.lowercase().contains(cmdText)
                    }.forEach {
                        results.add(SearchResult.Command(it))
                    }
                }

                // 3. System Actions
                if ("wifi".contains(q.lowercase())) {
                    results.add(SearchResult.SystemAction("Wi-Fi Settings", "Configure wireless network") { sysInfoManager.openWifiSettings() })
                }
                if ("bluetooth".contains(q.lowercase())) {
                    results.add(SearchResult.SystemAction("Bluetooth Settings", "Pair & manage devices") { sysInfoManager.openBluetoothSettings() })
                }
                if ("battery".contains(q.lowercase())) {
                    results.add(SearchResult.SystemAction("Battery Usage", "Check power status and stats") { sysInfoManager.openBatterySettings() })
                }
                if ("settings".contains(q.lowercase())) {
                    results.add(SearchResult.SystemAction("System Settings", "Open Android OS Settings") { sysInfoManager.openSystemSettings() })
                }

                // 4. Installed Apps Search
                val matchingApps = state.installedApps.filter {
                    it.displayLabel.lowercase().contains(q.lowercase()) ||
                    it.packageName.lowercase().contains(q.lowercase()) ||
                    (it.terminalShortcut != null && it.terminalShortcut.lowercase().startsWith(q.lowercase()))
                }
                matchingApps.forEach {
                    results.add(SearchResult.AppItem(it))
                }
            }

            state.copy(
                searchQuery = query,
                searchResults = results
            )
        }
    }

    fun askGemini(prompt: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSearchingGemini = true, geminiResponse = null) }
            val answer = geminiService.askGemini(prompt)
            _uiState.update { state ->
                val updatedResults = state.searchResults.toMutableList().apply {
                    add(0, SearchResult.GeminiInsight(prompt, answer))
                }
                state.copy(
                    isSearchingGemini = false,
                    geminiResponse = answer,
                    searchResults = updatedResults
                )
            }
        }
    }

    fun launchApp(app: AppInfo) {
        appRepo.launchApp(app.packageName)
        closeOverlay()
    }

    fun openAppDetails(app: AppInfo) {
        appRepo.openAppDetails(app.packageName)
    }

    fun requestUninstall(app: AppInfo) {
        appRepo.requestUninstall(app.packageName)
    }

    fun setSelectedAppForActions(app: AppInfo?) {
        _uiState.update { it.copy(selectedAppForActions = app) }
    }

    fun togglePinHome(app: AppInfo) {
        val currentPinned = _uiState.value.config.pinnedHomePackages.toMutableList()
        if (currentPinned.contains(app.packageName)) {
            currentPinned.remove(app.packageName)
        } else {
            currentPinned.add(app.packageName)
        }
        updateConfig { it.copy(pinnedHomePackages = currentPinned) }
    }

    fun togglePinDock(app: AppInfo) {
        val currentDock = _uiState.value.config.dockPackages.toMutableList()
        if (currentDock.contains(app.packageName)) {
            currentDock.remove(app.packageName)
        } else {
            currentDock.add(app.packageName)
        }
        updateConfig { it.copy(dockPackages = currentDock) }
    }

    fun hideApp(app: AppInfo) {
        val hidden = _uiState.value.config.hiddenPackages.toMutableSet()
        hidden.add(app.packageName)
        updateConfig { it.copy(hiddenPackages = hidden) }
        loadApps()
    }

    fun unhideApp(app: AppInfo) {
        val hidden = _uiState.value.config.hiddenPackages.toMutableSet()
        hidden.remove(app.packageName)
        updateConfig { it.copy(hiddenPackages = hidden) }
        loadApps()
    }

    fun renameApp(app: AppInfo, newName: String) {
        val updated = _uiState.value.installedApps.map {
            if (it.packageName == app.packageName) it.copy(customLabel = newName) else it
        }
        _uiState.update { it.copy(installedApps = updated, filteredApps = updated) }
    }

    fun updateConfig(block: (LauncherConfig) -> LauncherConfig) {
        val newConfig = block(_uiState.value.config)
        prefs.saveConfig(newConfig)
        _uiState.update { it.copy(config = newConfig) }
    }

    fun resetAllSettings() {
        prefs.resetAll()
        val defaultCfg = prefs.loadConfig()
        _uiState.update { it.copy(config = defaultCfg) }
        loadApps()
    }

    fun exportConfig(): String {
        return prefs.exportConfigToJson(_uiState.value.config)
    }

    fun importConfig(json: String): Boolean {
        val imported = prefs.importConfigFromJson(json)
        return if (imported != null) {
            updateConfig { imported }
            true
        } else {
            false
        }
    }

    fun addNote(content: String) {
        if (content.isBlank()) return
        viewModelScope.launch {
            db.launcherDao().insertNote(LauncherNoteEntity(content = content.trim()))
        }
    }

    fun deleteNote(id: Long) {
        viewModelScope.launch {
            db.launcherDao().deleteNote(id)
        }
    }

    fun executeGesture(action: GestureAction) {
        when (action) {
            GestureAction.OPEN_DRAWER -> setOverlay(ActiveOverlay.AppDrawer)
            GestureAction.OPEN_SEARCH -> setOverlay(ActiveOverlay.UniversalSearch)
            GestureAction.OPEN_COMMAND_PALETTE -> {
                setOverlay(ActiveOverlay.UniversalSearch)
                onSearchQueryChange("> ")
            }
            GestureAction.OPEN_CUSTOMIZATION -> setOverlay(ActiveOverlay.CustomizationCenter)
            GestureAction.OPEN_TERMINAL -> setOverlay(ActiveOverlay.TerminalShell)
            GestureAction.TOGGLE_FLASHLIGHT -> sysInfoManager.toggleFlashlight()
            GestureAction.OPEN_SETTINGS -> sysInfoManager.openSystemSettings()
            GestureAction.OPEN_NOTIFICATIONS -> {
                // Try opening notifications panel
                sysInfoManager.openSystemSettings()
            }
            GestureAction.LOCK_SCREEN -> {
                // Safe visual toast or lock intent
                _uiState.update { it.copy(toastMessage = "Screen lock triggered") }
            }
            GestureAction.REFRESH_APPS -> loadApps()
            GestureAction.NONE -> {}
        }
    }

    private fun getAvailableCommands(): List<CommandItem> = listOf(
        CommandItem("cmd_term", "terminal", "Open integrated Unix shell", "Dev", ">_") { setOverlay(ActiveOverlay.TerminalShell) },
        CommandItem("cmd_wifi", "wifi", "Open Wi-Fi settings", "System", "⚙") { sysInfoManager.openWifiSettings() },
        CommandItem("cmd_bt", "bluetooth", "Open Bluetooth settings", "System", "⚙") { sysInfoManager.openBluetoothSettings() },
        CommandItem("cmd_bat", "battery", "View battery details", "System", "⚡") { sysInfoManager.openBatterySettings() },
        CommandItem("cmd_flash", "flash", "Toggle camera flashlight", "Tools", "☼") { sysInfoManager.toggleFlashlight() },
        CommandItem("cmd_settings", "settings", "Open launcher customization", "Launcher", "🛠") { setOverlay(ActiveOverlay.CustomizationCenter) },
        CommandItem("cmd_apps", "apps", "Open application drawer", "Launcher", "◈") { setOverlay(ActiveOverlay.AppDrawer) },
        CommandItem("cmd_reload", "restart", "Reload launcher state", "System", "↻") { loadApps(); refreshSystemStats() },
        CommandItem("cmd_clear", "clean", "Clear cache & refresh memory", "System", "🧹") {
            System.gc()
            refreshSystemStats()
        }
    )

    private fun evaluateMath(query: String): String? {
        val expr = query.removePrefix("calc").trim()
        if (expr.isEmpty()) return null
        return try {
            val sanitized = expr.replace("x", "*").replace("X", "*")
            val regex = """^(-?\d+(?:\.\d+)?)\s*([\+\-\*\/%])\s*(-?\d+(?:\.\d+)?)$""".toRegex()
            val match = regex.find(sanitized)
            if (match != null) {
                val (n1Str, op, n2Str) = match.destructured
                val n1 = n1Str.toDouble()
                val n2 = n2Str.toDouble()
                val res = when (op) {
                    "+" -> n1 + n2
                    "-" -> n1 - n2
                    "*" -> n1 * n2
                    "/" -> if (n2 != 0.0) n1 / n2 else Double.NaN
                    "%" -> n1 % n2
                    else -> null
                }
                if (res != null && !res.isNaN()) {
                    if (res % 1.0 == 0.0) res.toLong().toString() else String.format("%.4f", res)
                } else null
            } else null
        } catch (e: Exception) {
            null
        }
    }
}
