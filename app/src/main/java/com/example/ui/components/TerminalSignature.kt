package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SystemStats
import com.example.model.LauncherConfig
import com.example.ui.theme.EmoCyan
import com.example.ui.theme.EmoLime
import com.example.ui.theme.EmoMutedGray
import com.example.ui.theme.EmoSoftWhite

@Composable
fun TerminalSignatureHeader(
    modifier: Modifier = Modifier,
    config: LauncherConfig = LauncherConfig(),
    systemStats: SystemStats = SystemStats(),
    onClick: () -> Unit
) {
    if (!config.showTerminalSignature) return

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

    val cursorAnim = rememberInfiniteTransition(label = "cursor_blink")
    val cursorAlpha by cursorAnim.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cursor_alpha"
    )

    val user = config.terminalPromptUser
    val host = config.terminalPromptHost
    val dir = config.terminalPromptDir

    val promptText = buildAnnotatedString {
        if (config.terminalCustomMessage.isNotBlank()) {
            withStyle(SpanStyle(color = primaryAccent, fontWeight = FontWeight.Bold)) {
                append("┌─[ ")
            }
            withStyle(SpanStyle(color = EmoSoftWhite)) {
                append(config.terminalCustomMessage)
            }
            withStyle(SpanStyle(color = primaryAccent, fontWeight = FontWeight.Bold)) {
                append(" ]\n└─$ ")
            }
        } else {
            withStyle(SpanStyle(color = primaryAccent)) {
                append("┌─(")
            }
            withStyle(SpanStyle(color = secondaryAccent, fontWeight = FontWeight.SemiBold)) {
                append(user)
            }
            withStyle(SpanStyle(color = primaryAccent)) {
                append("㉿")
            }
            withStyle(SpanStyle(color = secondaryAccent, fontWeight = FontWeight.SemiBold)) {
                append(host)
            }
            withStyle(SpanStyle(color = primaryAccent)) {
                append(")-[")
            }
            withStyle(SpanStyle(color = EmoSoftWhite)) {
                append(dir)
            }
            withStyle(SpanStyle(color = primaryAccent)) {
                append("]\n└─$ ")
            }
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("terminal_signature_header")
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = promptText,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            )
            Box(
                modifier = Modifier
                    .size(width = 6.dp, height = 13.dp)
                    .background(primaryAccent.copy(alpha = cursorAlpha))
            )
        }

        // Live system indicator badge
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier
                .background(Color(0xFF0C161F).copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(if (systemStats.isWifiConnected) secondaryAccent else EmoMutedGray, CircleShape)
            )
            Text(
                text = "${systemStats.batteryPercent}%",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    color = EmoSoftWhite
                )
            )
        }
    }
}
