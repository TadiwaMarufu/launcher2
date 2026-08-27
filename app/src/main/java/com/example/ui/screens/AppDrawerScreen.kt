package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppCategory
import com.example.model.AppInfo
import com.example.model.DrawerLayout
import com.example.model.LauncherConfig
import com.example.ui.components.AppIconItem
import com.example.ui.components.LiquidGlassSurface
import com.example.ui.components.TextAppShortcut
import com.example.ui.theme.EmoCyan
import com.example.ui.theme.EmoLime
import com.example.ui.theme.EmoMutedGray
import com.example.ui.theme.EmoNearBlack
import com.example.ui.theme.EmoSoftWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDrawerScreen(
    apps: List<AppInfo>,
    selectedCategory: AppCategory,
    config: LauncherConfig,
    onCategorySelect: (AppCategory) -> Unit,
    onAppClick: (AppInfo) -> Unit,
    onAppLongClick: (AppInfo) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var drawerFilter by remember { mutableStateOf("") }

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

    val displayedApps = remember(apps, drawerFilter) {
        if (drawerFilter.isBlank()) apps else {
            apps.filter {
                it.displayLabel.contains(drawerFilter, ignoreCase = true) ||
                it.packageName.contains(drawerFilter, ignoreCase = true)
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF04080C).copy(alpha = 0.94f))
            .testTag("app_drawer_screen")
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Header Search inside Drawer
            LiquidGlassSurface(
                config = config,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "◈",
                        color = primaryAccent,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    TextField(
                        value = drawerFilter,
                        onValueChange = { drawerFilter = it },
                        placeholder = {
                            Text(
                                text = "Filter applications...",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = EmoMutedGray,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 13.sp
                                )
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = EmoSoftWhite,
                            unfocusedTextColor = EmoSoftWhite,
                            cursorColor = primaryAccent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("drawer_search_input")
                    )

                    if (drawerFilter.isNotBlank()) {
                        Text(
                            text = "✕",
                            color = EmoMutedGray,
                            modifier = Modifier
                                .clickable { drawerFilter = "" }
                                .padding(6.dp)
                        )
                    }

                    Text(
                        text = "CLOSE",
                        style = MaterialTheme.typography.labelSmall.copy(color = primaryAccent),
                        modifier = Modifier
                            .clickable(onClick = onClose)
                            .padding(4.dp)
                            .testTag("drawer_close_button")
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Categories Row
            if (config.drawerShowCategories) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(AppCategory.values()) { cat ->
                        val isSelected = cat == selectedCategory
                        LiquidGlassSurface(
                            config = config,
                            shape = RoundedCornerShape(12.dp),
                            customColor = if (isSelected) primaryAccent.copy(alpha = 0.25f) else null,
                            onClick = { onCategorySelect(cat) }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = cat.iconGlyph,
                                    color = if (isSelected) primaryAccent else EmoMutedGray,
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = cat.title,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = if (isSelected) EmoSoftWhite else EmoMutedGray,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Apps Grid / List
            if (displayedApps.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No applications found.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = EmoMutedGray,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                }
            } else {
                when (config.drawerLayout) {
                    DrawerLayout.CATEGORIES_GRID, DrawerLayout.COMPACT_GRID -> {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(config.drawerGridColumns),
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag("drawer_apps_grid"),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(bottom = 24.dp)
                        ) {
                            items(displayedApps) { app ->
                                AppIconItem(
                                    app = app,
                                    config = config,
                                    onClick = { onAppClick(app) },
                                    onLongClick = { onAppLongClick(app) }
                                )
                            }
                        }
                    }
                    DrawerLayout.ALPHABETICAL_LIST, DrawerLayout.MINIMAL_TEXT -> {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(1),
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            contentPadding = PaddingValues(bottom = 24.dp)
                        ) {
                            items(displayedApps) { app ->
                                TextAppShortcut(
                                    app = app,
                                    config = config,
                                    onClick = { onAppClick(app) },
                                    onLongClick = { onAppLongClick(app) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
