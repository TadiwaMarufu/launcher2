package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppInfo
import com.example.model.CommandItem
import com.example.model.LauncherConfig
import com.example.model.SearchResult
import com.example.ui.components.AppIconGlyph
import com.example.ui.components.LiquidGlassSurface
import com.example.ui.theme.EmoCyan
import com.example.ui.theme.EmoLime
import com.example.ui.theme.EmoMutedGray
import com.example.ui.theme.EmoNearBlack
import com.example.ui.theme.EmoSoftWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UniversalSearchScreen(
    query: String,
    results: List<SearchResult>,
    config: LauncherConfig,
    isSearchingGemini: Boolean,
    onQueryChange: (String) -> Unit,
    onAppClick: (AppInfo) -> Unit,
    onClose: () -> Unit,
    onAskGemini: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
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

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF04080C).copy(alpha = 0.94f))
            .testTag("universal_search_screen")
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Header Search Bar
            LiquidGlassSurface(
                config = config,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = ">_",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = primaryAccent,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    TextField(
                        value = query,
                        onValueChange = onQueryChange,
                        placeholder = {
                            Text(
                                text = "Search apps, '> cmd', calc, or ask AI...",
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
                            .focusRequester(focusRequester)
                            .testTag("universal_search_input")
                    )

                    if (query.isNotBlank()) {
                        Text(
                            text = "✕",
                            color = EmoMutedGray,
                            modifier = Modifier
                                .clickable { onQueryChange("") }
                                .padding(8.dp)
                                .testTag("search_clear_button")
                        )
                    }

                    Text(
                        text = "ESC",
                        style = MaterialTheme.typography.labelSmall.copy(color = primaryAccent),
                        modifier = Modifier
                            .clickable(onClick = onClose)
                            .padding(4.dp)
                            .testTag("search_close_button")
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // AI Search prompt suggestion chip
            if (query.isNotBlank() && !isSearchingGemini) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onAskGemini(query) }
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "✦", color = secondaryAccent)
                    Text(
                        text = "Ask Gemini: \"$query\"",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = secondaryAccent,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp
                        )
                    )
                }
            }

            if (isSearchingGemini) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = secondaryAccent
                    )
                    Text(
                        text = "Querying Gemini AI with Search Grounding...",
                        style = MaterialTheme.typography.bodySmall.copy(color = secondaryAccent)
                    )
                }
            }

            // Results List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(results) { item ->
                    when (item) {
                        is SearchResult.MathCalculation -> {
                            MathResultCard(item, config, primaryAccent)
                        }
                        is SearchResult.Command -> {
                            CommandResultCard(item.item, config, primaryAccent, secondaryAccent)
                        }
                        is SearchResult.SystemAction -> {
                            SystemActionResultCard(item, config, primaryAccent)
                        }
                        is SearchResult.AppItem -> {
                            AppSearchResultCard(
                                app = item.app,
                                config = config,
                                primaryAccent = primaryAccent,
                                onClick = { onAppClick(item.app) }
                            )
                        }
                        is SearchResult.GeminiInsight -> {
                            GeminiInsightCard(item, config, secondaryAccent)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MathResultCard(
    math: SearchResult.MathCalculation,
    config: LauncherConfig,
    accent: Color
) {
    LiquidGlassSurface(
        config = config,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "calc: ${math.expression}",
                    style = MaterialTheme.typography.bodySmall.copy(color = EmoMutedGray)
                )
                Text(
                    text = "= ${math.result}",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = accent,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            Text(text = "🔢", fontSize = 20.sp)
        }
    }
}

@Composable
private fun CommandResultCard(
    cmd: CommandItem,
    config: LauncherConfig,
    primaryAccent: Color,
    secondaryAccent: Color
) {
    LiquidGlassSurface(
        config = config,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { cmd.directAction?.invoke() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(Color(0xFF09141D), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = cmd.shortcut ?: ">_",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = secondaryAccent,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "> ${cmd.command}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = primaryAccent,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Text(
                    text = cmd.description,
                    style = MaterialTheme.typography.bodySmall.copy(color = EmoMutedGray)
                )
            }
            Text(
                text = cmd.category.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(color = EmoMutedGray, fontSize = 9.sp)
            )
        }
    }
}

@Composable
private fun SystemActionResultCard(
    action: SearchResult.SystemAction,
    config: LauncherConfig,
    accent: Color
) {
    LiquidGlassSurface(
        config = config,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { action.action() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "⚙", fontSize = 20.sp)
            Column {
                Text(
                    text = action.title,
                    style = MaterialTheme.typography.titleMedium.copy(color = EmoSoftWhite)
                )
                Text(
                    text = action.subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(color = EmoMutedGray)
                )
            }
        }
    }
}

@Composable
private fun AppSearchResultCard(
    app: AppInfo,
    config: LauncherConfig,
    primaryAccent: Color,
    onClick: () -> Unit
) {
    LiquidGlassSurface(
        config = config,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            AppIconGlyph(
                app = app,
                config = config,
                primaryAccent = primaryAccent,
                secondaryAccent = Color(0xFFA6FF00),
                sizeMultiplier = 0.9f
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = app.displayLabel,
                    style = MaterialTheme.typography.titleMedium.copy(color = EmoSoftWhite)
                )
                Text(
                    text = app.packageName,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = EmoMutedGray,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                )
            }

            if (app.terminalShortcut != null) {
                Text(
                    text = "$ ${app.terminalShortcut}",
                    style = MaterialTheme.typography.labelSmall.copy(color = primaryAccent)
                )
            }
        }
    }
}

@Composable
private fun GeminiInsightCard(
    item: SearchResult.GeminiInsight,
    config: LauncherConfig,
    accent: Color
) {
    LiquidGlassSurface(
        config = config,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "✦ Gemini Grounded AI", color = accent, style = MaterialTheme.typography.labelLarge)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = item.answer,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = EmoSoftWhite,
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                )
            )
        }
    }
}
