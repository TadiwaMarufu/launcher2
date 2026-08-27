package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SystemStats
import com.example.model.LauncherConfig
import com.example.ui.theme.EmoCyan
import com.example.ui.theme.EmoLime
import com.example.ui.theme.EmoMutedGray
import com.example.ui.theme.EmoSoftWhite

@Composable
fun SystemHudBar(
    modifier: Modifier = Modifier,
    config: LauncherConfig = LauncherConfig(),
    stats: SystemStats = SystemStats()
) {
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

    LiquidGlassSurface(
        config = config,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            HudStatItem(label = "CPU", value = "${stats.cpuPercentEst}%", accent = primaryAccent)
            HudStatItem(label = "RAM", value = "${(stats.ramUsedMb * 100 / stats.ramTotalMb.coerceAtLeast(1))}%", accent = secondaryAccent)
            HudStatItem(label = "BAT", value = "${stats.batteryPercent}%", accent = if (stats.batteryPercent < 20) Color(0xFFFF4D6D) else primaryAccent)
            HudStatItem(label = "DISK", value = String.format("%.0fGB", stats.storageUsedGb), accent = EmoSoftWhite)
            HudStatItem(label = "UP", value = stats.uptimeString, accent = EmoMutedGray)
        }
    }
}

@Composable
private fun HudStatItem(
    label: String,
    value: String,
    accent: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 9.sp,
                color = EmoMutedGray,
                letterSpacing = 1.sp
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 11.sp,
                color = accent,
                fontWeight = FontWeight.Medium
            )
        )
    }
}
