package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.example.data.SystemStats
import com.example.model.AppInfo
import com.example.model.LauncherConfig
import com.example.ui.components.LiquidGlassSurface
import com.example.ui.theme.EmoCyan
import com.example.ui.theme.EmoLime
import com.example.ui.theme.EmoMutedGray
import com.example.ui.theme.EmoSoftWhite
import kotlinx.coroutines.launch

data class TerminalLog(
    val prompt: String,
    val output: String,
    val isError: Boolean = false,
    val isAi: Boolean = false
)

@Composable
fun TerminalScreen(
    config: LauncherConfig,
    apps: List<AppInfo>,
    systemStats: SystemStats,
    onLaunchApp: (AppInfo) -> Unit,
    onAskGemini: (String, (String) -> Unit) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var inputCommand by remember { mutableStateOf("") }
    val logs = remember {
        mutableStateListOf(
            TerminalLog(
                prompt = "system",
                output = "EmoLauncher Unix Shell v1.0.0 (x86_64/aarch64)\nType 'help' or 'neofetch' to begin. Try 'ai <prompt>' for Gemini AI."
            )
        )
    }

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
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

    fun executeCommand(raw: String) {
        val trimmed = raw.trim()
        if (trimmed.isEmpty()) return

        val parts = trimmed.split(" ")
        val cmd = parts[0].lowercase()
        val args = parts.drop(1).joinToString(" ")

        when (cmd) {
            "help" -> {
                logs.add(
                    TerminalLog(
                        prompt = raw,
                        output = """
Available Commands:
  help               - Show this manual
  apps, ls           - List all launchable applications
  launch <name|pkg>  - Launch target application
  neofetch, sysinfo  - Display ASCII system status
  wifi, bt, bat      - Open respective system settings
  ai <question>      - Query Gemini AI assistant
  calc <expression>  - Compute mathematical expression
  clear              - Clear terminal history
  exit, quit         - Close terminal shell
                        """.trimIndent()
                    )
                )
            }
            "clear" -> {
                logs.clear()
            }
            "exit", "quit" -> {
                onClose()
                return
            }
            "ls", "apps" -> {
                val appListStr = apps.take(20).mapIndexed { idx, app ->
                    "  [${idx + 1}] ${app.displayLabel.padEnd(16)} -> ${app.packageName}"
                }.joinToString("\n")
                logs.add(TerminalLog(prompt = raw, output = "Installed Apps (${apps.size}):\n$appListStr"))
            }
            "launch" -> {
                if (args.isBlank()) {
                    logs.add(TerminalLog(prompt = raw, output = "Error: Usage 'launch <app_name>'", isError = true))
                } else {
                    val target = apps.firstOrNull {
                        it.displayLabel.contains(args, ignoreCase = true) ||
                        it.packageName.contains(args, ignoreCase = true)
                    }
                    if (target != null) {
                        logs.add(TerminalLog(prompt = raw, output = "Launching ${target.displayLabel}..."))
                        onLaunchApp(target)
                    } else {
                        logs.add(TerminalLog(prompt = raw, output = "Error: App '$args' not found.", isError = true))
                    }
                }
            }
            "neofetch", "sysinfo" -> {
                val banner = """
   /\_/\     emo@kali-android
  ( o.o )    ----------------
   > ^ <     OS: EmoLauncher v1.0.0
             Host: Android OS
             Uptime: ${systemStats.uptimeString}
             Battery: ${systemStats.batteryPercent}% (${if (systemStats.isCharging) "Charging" else "Discharging"})
             Memory: ${systemStats.ramUsedMb}MB / ${systemStats.ramTotalMb}MB
             Storage: ${String.format("%.1f", systemStats.storageUsedGb)}GB / ${String.format("%.1f", systemStats.storageTotalGb)}GB
             Shell: emo-sh 1.0
                """.trimIndent()
                logs.add(TerminalLog(prompt = raw, output = banner))
            }
            "ai" -> {
                if (args.isBlank()) {
                    logs.add(TerminalLog(prompt = raw, output = "Error: Usage 'ai <your question>'", isError = true))
                } else {
                    logs.add(TerminalLog(prompt = raw, output = "✦ Generating response from Gemini AI...", isAi = true))
                    onAskGemini(args) { answer ->
                        logs.add(TerminalLog(prompt = "gemini-3.5-flash", output = answer, isAi = true))
                        scope.launch { listState.animateScrollToItem(logs.size - 1) }
                    }
                }
            }
            "calc" -> {
                logs.add(TerminalLog(prompt = raw, output = "Result: ${evalMathSimple(args)}"))
            }
            else -> {
                logs.add(TerminalLog(prompt = raw, output = "emo-sh: command not found: $cmd. Type 'help' for manual.", isError = true))
            }
        }

        inputCommand = ""
        scope.launch {
            if (logs.isNotEmpty()) listState.animateScrollToItem(logs.size - 1)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF03070A))
            .testTag("terminal_screen")
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Terminal Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "●", color = Color(0xFFFF4D6D), fontSize = 12.sp)
                    Text(text = "●", color = Color(0xFFFFB703), fontSize = 12.sp)
                    Text(text = "●", color = Color(0xFFA6FF00), fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "emo@kali:~ (sh)",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = EmoSoftWhite,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp
                        )
                    )
                }
                Text(
                    text = "[ESC/CLOSE]",
                    style = MaterialTheme.typography.labelSmall.copy(color = primaryAccent),
                    modifier = Modifier
                        .clickable(onClick = onClose)
                        .padding(4.dp)
                        .testTag("terminal_close_btn")
                )
            }

            HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

            // Output logs
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(logs) { log ->
                    Column {
                        if (log.prompt != "system") {
                            Text(
                                text = "┌─(emo㉿kali)-[~]\n└─$ ${log.prompt}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = primaryAccent,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp
                                )
                            )
                        }
                        Text(
                            text = log.output,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = if (log.isError) Color(0xFFFF4D6D) else if (log.isAi) secondaryAccent else EmoSoftWhite,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            ),
                            modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                        )
                    }
                }
            }

            // Command Input line
            LiquidGlassSurface(
                config = config,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "$",
                        color = secondaryAccent,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    TextField(
                        value = inputCommand,
                        onValueChange = { inputCommand = it },
                        placeholder = {
                            Text(
                                text = "type command (e.g. 'help', 'neofetch')...",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = EmoMutedGray,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp
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
                            .testTag("terminal_input_field")
                    )
                    Text(
                        text = "RUN",
                        style = MaterialTheme.typography.labelSmall.copy(color = primaryAccent, fontWeight = FontWeight.Bold),
                        modifier = Modifier
                            .clickable { executeCommand(inputCommand) }
                            .padding(8.dp)
                            .testTag("terminal_run_btn")
                    )
                }
            }
        }
    }
}

private fun evalMathSimple(expr: String): String {
    return try {
        val sanitized = expr.replace("x", "*").replace("X", "*").trim()
        val regex = """^(-?\d+(?:\.\d+)?)\s*([\+\-\*\/%])\s*(-?\d+(?:\.\d+)?)$""".toRegex()
        val match = regex.find(sanitized)
        if (match != null) {
            val (n1Str, op, n2Str) = match.destructured
            val n1 = n1Str.toDouble()
            val n2 = n2Str.toDouble()
            when (op) {
                "+" -> (n1 + n2).toString()
                "-" -> (n1 - n2).toString()
                "*" -> (n1 * n2).toString()
                "/" -> if (n2 != 0.0) (n1 / n2).toString() else "Divide by zero error"
                "%" -> (n1 % n2).toString()
                else -> "Syntax error"
            }
        } else "Invalid format (use: calc 15 + 4)"
    } catch (e: Exception) {
        "Error: ${e.message}"
    }
}
