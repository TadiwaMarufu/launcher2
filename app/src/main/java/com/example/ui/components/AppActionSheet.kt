package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppInfo
import com.example.model.LauncherConfig
import com.example.ui.theme.EmoCyan
import com.example.ui.theme.EmoMutedGray
import com.example.ui.theme.EmoSoftWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppActionBottomSheet(
    app: AppInfo?,
    config: LauncherConfig,
    onDismiss: () -> Unit,
    onPinHomeToggle: (AppInfo) -> Unit,
    onPinDockToggle: (AppInfo) -> Unit,
    onHideApp: (AppInfo) -> Unit,
    onAppInfo: (AppInfo) -> Unit,
    onUninstall: (AppInfo) -> Unit,
    onRename: (AppInfo, String) -> Unit
) {
    if (app == null) return

    var isEditingName by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf(app.displayLabel) }

    val primaryAccent = try {
        Color(android.graphics.Color.parseColor(config.primaryAccentHex))
    } catch (e: Exception) {
        EmoCyan
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF071015).copy(alpha = 0.95f),
        contentColor = EmoSoftWhite,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .size(width = 36.dp, height = 4.dp)
                    .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(2.dp))
            )
        },
        modifier = Modifier.testTag("app_action_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp)
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            // App Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                AppIconGlyph(
                    app = app,
                    config = config,
                    primaryAccent = primaryAccent,
                    secondaryAccent = Color(0xFFA6FF00),
                    sizeMultiplier = 1.1f
                )

                Column(modifier = Modifier.weight(1f)) {
                    if (isEditingName) {
                        OutlinedTextField(
                            value = newName,
                            onValueChange = { newName = it },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = primaryAccent,
                                unfocusedBorderColor = EmoMutedGray,
                                focusedTextColor = EmoSoftWhite,
                                unfocusedTextColor = EmoSoftWhite
                            ),
                            trailingIcon = {
                                Text(
                                    text = "SAVE",
                                    color = primaryAccent,
                                    modifier = Modifier
                                        .clickable {
                                            onRename(app, newName)
                                            isEditingName = false
                                        }
                                        .padding(8.dp)
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Text(
                            text = app.displayLabel,
                            style = MaterialTheme.typography.titleLarge.copy(color = EmoSoftWhite)
                        )
                        Text(
                            text = app.packageName,
                            style = MaterialTheme.typography.bodySmall.copy(color = EmoMutedGray, fontSize = 10.sp)
                        )
                    }
                }
            }

            HorizontalDivider(color = Color.White.copy(alpha = 0.08f))

            // Actions list
            ActionRow(
                icon = "📌",
                title = if (config.pinnedHomePackages.contains(app.packageName)) "Remove from Home" else "Pin to Home",
                onClick = {
                    onPinHomeToggle(app)
                    onDismiss()
                }
            )

            ActionRow(
                icon = "⚓",
                title = if (config.dockPackages.contains(app.packageName)) "Remove from Dock" else "Add to Dock",
                onClick = {
                    onPinDockToggle(app)
                    onDismiss()
                }
            )

            ActionRow(
                icon = "✏",
                title = "Rename Shortcut",
                onClick = { isEditingName = true }
            )

            ActionRow(
                icon = "ℹ",
                title = "App Info & Permissions",
                onClick = {
                    onAppInfo(app)
                    onDismiss()
                }
            )

            ActionRow(
                icon = "⚿",
                title = "Hide Application",
                onClick = {
                    onHideApp(app)
                    onDismiss()
                }
            )

            ActionRow(
                icon = "🗑",
                title = "Uninstall",
                color = Color(0xFFFF4D6D),
                onClick = {
                    onUninstall(app)
                    onDismiss()
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ActionRow(
    icon: String,
    title: String,
    color: Color = EmoSoftWhite,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = icon, fontSize = 16.sp)
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = color,
                fontSize = 14.sp
            )
        )
    }
}
