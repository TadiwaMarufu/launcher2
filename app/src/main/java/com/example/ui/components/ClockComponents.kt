package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SystemStats
import com.example.model.ClockStyle
import com.example.model.LauncherConfig
import com.example.ui.theme.EmoCyan
import com.example.ui.theme.EmoLime
import com.example.ui.theme.EmoMutedGray
import com.example.ui.theme.EmoSoftWhite
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun MainClockView(
    modifier: Modifier = Modifier,
    config: LauncherConfig = LauncherConfig(),
    systemStats: SystemStats = SystemStats(),
    onClick: (() -> Unit)? = null
) {
    var currentTime by remember { mutableStateOf(Date()) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = Date()
            delay(500)
        }
    }

    val primaryAccent = try {
        if (config.clockColorHex != null) {
            Color(android.graphics.Color.parseColor(config.clockColorHex))
        } else {
            Color(android.graphics.Color.parseColor(config.primaryAccentHex))
        }
    } catch (e: Exception) {
        EmoCyan
    }

    val secondaryAccent = try {
        Color(android.graphics.Color.parseColor(config.secondaryAccentHex))
    } catch (e: Exception) {
        EmoLime
    }

    val timeFormatPattern = if (config.clockFormat24h) {
        if (config.clockShowSeconds) "HH:mm:ss" else "HH:mm"
    } else {
        if (config.clockShowSeconds) "hh:mm:ss a" else "hh:mm a"
    }

    val timeString = remember(currentTime, timeFormatPattern) {
        SimpleDateFormat(timeFormatPattern, Locale.getDefault()).format(currentTime)
    }

    val dayOfWeek = remember(currentTime) {
        val cal = Calendar.getInstance().apply { time = currentTime }
        when (cal.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> "M O N"
            Calendar.TUESDAY -> "T U E"
            Calendar.WEDNESDAY -> "W E D"
            Calendar.THURSDAY -> "T H U"
            Calendar.FRIDAY -> "F R I"
            Calendar.SATURDAY -> "S A T"
            Calendar.SUNDAY -> "S U N"
            else -> "T H U"
        }
    }

    val fullDateString = remember(currentTime) {
        SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).format(currentTime).uppercase()
    }

    val clickModifier = if (onClick != null) {
        Modifier.clickable(onClick = onClick)
    } else Modifier

    Box(
        modifier = modifier
            .fillMaxWidth()
            .then(clickModifier),
        contentAlignment = Alignment.Center
    ) {
        when (config.clockStyle) {
            ClockStyle.ORBITAL_CIRCLE -> {
                OrbitalCircleClock(
                    timeString = timeString,
                    dayOfWeek = dayOfWeek,
                    fullDateString = fullDateString,
                    config = config,
                    primaryAccent = primaryAccent,
                    secondaryAccent = secondaryAccent,
                    systemStats = systemStats
                )
            }
            ClockStyle.MINIMAL_TEXT -> {
                MinimalTextClock(
                    timeString = timeString,
                    dayOfWeek = dayOfWeek,
                    fullDateString = fullDateString,
                    config = config,
                    primaryAccent = primaryAccent
                )
            }
            ClockStyle.TERMINAL_UNIX -> {
                TerminalUnixClock(
                    currentTime = currentTime,
                    config = config,
                    primaryAccent = primaryAccent,
                    systemStats = systemStats
                )
            }
            ClockStyle.DIGITAL_BOLD -> {
                DigitalBoldClock(
                    timeString = timeString,
                    fullDateString = fullDateString,
                    config = config,
                    primaryAccent = primaryAccent,
                    systemStats = systemStats
                )
            }
            ClockStyle.VERTICAL -> {
                VerticalStackedClock(
                    currentTime = currentTime,
                    fullDateString = fullDateString,
                    config = config,
                    primaryAccent = primaryAccent
                )
            }
            ClockStyle.ANALOG_HYBRID -> {
                AnalogHybridClock(
                    currentTime = currentTime,
                    timeString = timeString,
                    config = config,
                    primaryAccent = primaryAccent
                )
            }
        }
    }
}

@Composable
fun OrbitalCircleClock(
    timeString: String,
    dayOfWeek: String,
    fullDateString: String,
    config: LauncherConfig,
    primaryAccent: Color,
    secondaryAccent: Color,
    systemStats: SystemStats
) {
    val cal = Calendar.getInstance()
    val seconds = cal.get(Calendar.SECOND)
    val millis = cal.get(Calendar.MILLISECOND)
    val progress = (seconds + millis / 1000f) / 60f

    val infiniteTransition = rememberInfiniteTransition(label = "orbital_pulse")
    val ambientPulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = Modifier
            .size(280.dp * config.clockFontSize)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 1.5.dp.toPx()
            val radius = (size.minDimension - strokeWidth * 4) / 2f
            val center = Offset(size.width / 2f, size.height / 2f)

            // Outer faint track
            drawCircle(
                color = Color.White.copy(alpha = 0.05f),
                radius = radius,
                center = center,
                style = Stroke(width = strokeWidth)
            )

            // Dynamic progress arc
            drawArc(
                brush = Brush.sweepGradient(
                    listOf(
                        secondaryAccent.copy(alpha = 0.05f),
                        secondaryAccent.copy(alpha = 0.85f * ambientPulse)
                    )
                ),
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth * 1.5f)
            )

            // Orbital traveling dot
            if (config.clockShowOrbitalDot) {
                val angleRad = Math.toRadians((360.0 * progress) - 90.0)
                val dotX = center.x + radius * cos(angleRad).toFloat()
                val dotY = center.y + radius * sin(angleRad).toFloat()

                // Glow halo
                drawCircle(
                    color = secondaryAccent.copy(alpha = 0.40f * ambientPulse),
                    radius = 7.dp.toPx(),
                    center = Offset(dotX, dotY)
                )
                // Solid dot
                drawCircle(
                    color = secondaryAccent,
                    radius = 3.5.dp.toPx(),
                    center = Offset(dotX, dotY)
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (config.clockShowWeekday) {
                Text(
                    text = dayOfWeek,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = EmoMutedGray,
                        letterSpacing = 4.sp,
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp
                    ),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            Text(
                text = timeString,
                style = MaterialTheme.typography.displayLarge.copy(
                    color = EmoSoftWhite,
                    fontWeight = FontWeight.ExtraLight,
                    letterSpacing = (-2).sp,
                    fontSize = (68 * config.clockFontSize).sp,
                    lineHeight = (68 * config.clockFontSize).sp
                )
            )

            if (config.clockShowDate || config.clockShowWeather) {
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (config.clockShowDate) {
                        Text(
                            text = fullDateString,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = primaryAccent.copy(alpha = 0.9f),
                                letterSpacing = 2.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp
                            )
                        )
                    }

                    if (config.clockShowDate && (config.clockShowWeather || config.clockShowBattery)) {
                        Box(
                            modifier = Modifier
                                .size(3.dp)
                                .background(Color.White.copy(alpha = 0.25f), CircleShape)
                        )
                    }

                    if (config.clockShowWeather) {
                        Text(
                            text = "24°C ☼",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = EmoMutedGray,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                    }

                    if (config.clockShowBattery) {
                        Text(
                            text = "${systemStats.batteryPercent}%",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (systemStats.batteryPercent < 20) Color(0xFFFF4D6D) else secondaryAccent.copy(alpha = 0.8f),
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MinimalTextClock(
    timeString: String,
    dayOfWeek: String,
    fullDateString: String,
    config: LauncherConfig,
    primaryAccent: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(vertical = 16.dp)
    ) {
        Text(
            text = timeString,
            style = MaterialTheme.typography.displayLarge.copy(
                fontSize = (48 * config.clockFontSize).sp,
                fontWeight = FontWeight.ExtraLight,
                letterSpacing = 3.sp,
                color = EmoSoftWhite
            )
        )
        if (config.clockShowDate) {
            Text(
                text = "$dayOfWeek • $fullDateString",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = primaryAccent.copy(alpha = 0.85f),
                    letterSpacing = 2.sp,
                    fontSize = 12.sp
                ),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun TerminalUnixClock(
    currentTime: Date,
    config: LauncherConfig,
    primaryAccent: Color,
    systemStats: SystemStats
) {
    val utcFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).apply {
        timeZone = TimeZone.getDefault()
    }
    val unixTimestamp = currentTime.time / 1000

    LiquidGlassSurface(
        config = config,
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "$ sys.time --utc",
                style = MaterialTheme.typography.labelSmall.copy(color = primaryAccent, letterSpacing = 1.sp)
            )
            Text(
                text = "[${utcFormat.format(currentTime)}]",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = EmoSoftWhite,
                    letterSpacing = 1.sp,
                    fontSize = 17.sp
                ),
                modifier = Modifier.padding(vertical = 4.dp)
            )
            Text(
                text = "epoch: $unixTimestamp | up: ${systemStats.uptimeString}",
                style = MaterialTheme.typography.bodySmall.copy(color = EmoMutedGray, fontSize = 11.sp)
            )
        }
    }
}

@Composable
fun DigitalBoldClock(
    timeString: String,
    fullDateString: String,
    config: LauncherConfig,
    primaryAccent: Color,
    systemStats: SystemStats
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = timeString,
            style = MaterialTheme.typography.displayLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = (54 * config.clockFontSize).sp,
                letterSpacing = (-1).sp,
                color = EmoSoftWhite
            )
        )
        if (config.clockShowDate) {
            Text(
                text = fullDateString,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = primaryAccent,
                    letterSpacing = 2.sp
                ),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun VerticalStackedClock(
    currentTime: Date,
    fullDateString: String,
    config: LauncherConfig,
    primaryAccent: Color
) {
    val hours = SimpleDateFormat(if (config.clockFormat24h) "HH" else "hh", Locale.getDefault()).format(currentTime)
    val mins = SimpleDateFormat("mm", Locale.getDefault()).format(currentTime)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = hours,
            style = MaterialTheme.typography.displayLarge.copy(
                fontSize = (64 * config.clockFontSize).sp,
                fontWeight = FontWeight.ExtraLight,
                lineHeight = 60.sp,
                color = primaryAccent
            )
        )
        Text(
            text = mins,
            style = MaterialTheme.typography.displayLarge.copy(
                fontSize = (64 * config.clockFontSize).sp,
                fontWeight = FontWeight.Light,
                lineHeight = 60.sp,
                color = EmoSoftWhite
            )
        )
        if (config.clockShowDate) {
            Text(
                text = fullDateString,
                style = MaterialTheme.typography.bodySmall.copy(color = EmoMutedGray, letterSpacing = 2.sp),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun AnalogHybridClock(
    currentTime: Date,
    timeString: String,
    config: LauncherConfig,
    primaryAccent: Color
) {
    val cal = Calendar.getInstance().apply { time = currentTime }
    val hours = cal.get(Calendar.HOUR)
    val minutes = cal.get(Calendar.MINUTE)
    val seconds = cal.get(Calendar.SECOND)

    Box(
        modifier = Modifier
            .size(200.dp * config.clockFontSize)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.minDimension / 2f - 8.dp.toPx()

            // 12 ticks
            for (i in 0 until 12) {
                val angle = Math.toRadians((i * 30 - 90).toDouble())
                val isQuarter = i % 3 == 0
                val tickLen = if (isQuarter) 10.dp.toPx() else 5.dp.toPx()
                val start = Offset(
                    center.x + (radius - tickLen) * cos(angle).toFloat(),
                    center.y + (radius - tickLen) * sin(angle).toFloat()
                )
                val end = Offset(
                    center.x + radius * cos(angle).toFloat(),
                    center.y + radius * sin(angle).toFloat()
                )
                drawLine(
                    color = if (isQuarter) primaryAccent else Color.White.copy(alpha = 0.2f),
                    start = start,
                    end = end,
                    strokeWidth = if (isQuarter) 2.dp.toPx() else 1.dp.toPx()
                )
            }

            // Hour Hand
            val hourAngle = Math.toRadians(((hours + minutes / 60f) * 30 - 90).toDouble())
            val hourEnd = Offset(
                center.x + (radius * 0.5f) * cos(hourAngle).toFloat(),
                center.y + (radius * 0.5f) * sin(hourAngle).toFloat()
            )
            drawLine(color = EmoSoftWhite, start = center, end = hourEnd, strokeWidth = 3.dp.toPx())

            // Minute Hand
            val minAngle = Math.toRadians(((minutes + seconds / 60f) * 6 - 90).toDouble())
            val minEnd = Offset(
                center.x + (radius * 0.75f) * cos(minAngle).toFloat(),
                center.y + (radius * 0.75f) * sin(minAngle).toFloat()
            )
            drawLine(color = primaryAccent, start = center, end = minEnd, strokeWidth = 2.dp.toPx())

            // Center pin
            drawCircle(color = primaryAccent, radius = 3.dp.toPx(), center = center)
        }

        Text(
            text = timeString,
            style = MaterialTheme.typography.bodySmall.copy(color = EmoMutedGray, fontSize = 10.sp),
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp)
        )
    }
}
