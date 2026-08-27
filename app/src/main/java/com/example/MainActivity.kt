package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.LauncherConfig
import com.example.ui.components.AppActionBottomSheet
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.ActiveOverlay
import com.example.viewmodel.LauncherUiState
import com.example.viewmodel.LauncherViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: LauncherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val uiState by viewModel.uiState.collectAsState()

            MyApplicationTheme(config = uiState.config) {
                EmoLauncherApp(
                    uiState = uiState,
                    viewModel = viewModel
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshSystemStats()
    }
}

@Composable
fun EmoLauncherApp(
    uiState: LauncherUiState,
    viewModel: LauncherViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    // Handle back button: dismiss any active overlay first
    BackHandler(enabled = uiState.activeOverlay != ActiveOverlay.None) {
        viewModel.closeOverlay()
    }

    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Main Home Screen
            HomeScreen(
                config = uiState.config,
                installedApps = uiState.installedApps,
                systemStats = uiState.systemStats,
                onAppClick = { app -> viewModel.launchApp(app) },
                onAppLongClick = { app -> viewModel.setSelectedAppForActions(app) },
                onSearchTrigger = { viewModel.setOverlay(ActiveOverlay.UniversalSearch) },
                onTerminalTrigger = { viewModel.setOverlay(ActiveOverlay.TerminalShell) },
                onCustomizationTrigger = { viewModel.setOverlay(ActiveOverlay.CustomizationCenter) },
                onGestureAction = { action -> viewModel.executeGesture(action) }
            )

            // App Drawer Overlay
            AnimatedVisibility(
                visible = uiState.activeOverlay == ActiveOverlay.AppDrawer,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 })
            ) {
                AppDrawerScreen(
                    apps = uiState.filteredApps,
                    selectedCategory = uiState.selectedCategory,
                    config = uiState.config,
                    onCategorySelect = { cat -> viewModel.setSelectedCategory(cat) },
                    onAppClick = { app -> viewModel.launchApp(app) },
                    onAppLongClick = { app -> viewModel.setSelectedAppForActions(app) },
                    onClose = { viewModel.closeOverlay() }
                )
            }

            // Universal Search & Command Palette Overlay
            AnimatedVisibility(
                visible = uiState.activeOverlay == ActiveOverlay.UniversalSearch,
                enter = fadeIn() + slideInVertically(initialOffsetY = { -it / 3 }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { -it / 3 })
            ) {
                UniversalSearchScreen(
                    query = uiState.searchQuery,
                    results = uiState.searchResults,
                    config = uiState.config,
                    isSearchingGemini = uiState.isSearchingGemini,
                    onQueryChange = { q -> viewModel.onSearchQueryChange(q) },
                    onAppClick = { app -> viewModel.launchApp(app) },
                    onClose = { viewModel.closeOverlay() },
                    onAskGemini = { prompt -> viewModel.askGemini(prompt) }
                )
            }

            // Customization Center Overlay
            AnimatedVisibility(
                visible = uiState.activeOverlay == ActiveOverlay.CustomizationCenter,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 })
            ) {
                CustomizationCenterScreen(
                    config = uiState.config,
                    systemStats = uiState.systemStats,
                    onConfigChange = { block -> viewModel.updateConfig(block) },
                    onResetAll = { viewModel.resetAllSettings() },
                    onExportJson = { viewModel.exportConfig() },
                    onImportJson = { json -> viewModel.importConfig(json) },
                    onClose = { viewModel.closeOverlay() }
                )
            }

            // Terminal Unix Shell Overlay
            AnimatedVisibility(
                visible = uiState.activeOverlay == ActiveOverlay.TerminalShell,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 3 }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 3 })
            ) {
                TerminalScreen(
                    config = uiState.config,
                    apps = uiState.installedApps,
                    systemStats = uiState.systemStats,
                    onLaunchApp = { app -> viewModel.launchApp(app) },
                    onAskGemini = { prompt, onResult ->
                        viewModel.askGemini(prompt)
                        // Results flow through UI state or direct callback
                    },
                    onClose = { viewModel.closeOverlay() }
                )
            }

            // Contextual App Long-Press Action Bottom Sheet
            if (uiState.selectedAppForActions != null) {
                AppActionBottomSheet(
                    app = uiState.selectedAppForActions,
                    config = uiState.config,
                    onDismiss = { viewModel.setSelectedAppForActions(null) },
                    onPinHomeToggle = { app -> viewModel.togglePinHome(app) },
                    onPinDockToggle = { app -> viewModel.togglePinDock(app) },
                    onHideApp = { app -> viewModel.hideApp(app) },
                    onAppInfo = { app -> viewModel.openAppDetails(app) },
                    onUninstall = { app -> viewModel.requestUninstall(app) },
                    onRename = { app, newName -> viewModel.renameApp(app, newName) }
                )
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun EmoLauncherPreview() {
    MyApplicationTheme {
        EmoLauncherApp(uiState = LauncherUiState())
    }
}
