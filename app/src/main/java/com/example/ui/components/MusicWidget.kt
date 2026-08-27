package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LauncherConfig
import com.example.ui.theme.EmoCyan
import com.example.ui.theme.EmoLime
import com.example.ui.theme.EmoMutedGray
import com.example.ui.theme.EmoSoftWhite
import androidx.compose.foundation.border

data class MediaTrackState(
    val title: String = "Midnight Protocol",
    val artist: String = "Master Boot Record",
    val isPlaying: Boolean = true,
    val progress: Float = 0.42f
)

@Composable
fun MusicWidget(
    modifier: Modifier = Modifier,
    config: LauncherConfig = LauncherConfig(),
    trackState: MediaTrackState = MediaTrackState(),
    onPlayPauseToggle: () -> Unit = {},
    onNext: () -> Unit = {},
    onPrevious: () -> Unit = {}
) {
    if (!config.showMusicWidgetOnHome) return

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
        shape = RoundedCornerShape(24.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("music_widget")
            .padding(horizontal = 20.dp, vertical = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Glass icon container
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(primaryAccent.copy(alpha = 0.15f))
                            .border(1.dp, primaryAccent.copy(alpha = 0.30f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "♪",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = if (trackState.isPlaying) primaryAccent else EmoMutedGray,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Column {
                        Text(
                            text = "CURRENT SESSION",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = EmoMutedGray,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp,
                                fontSize = 9.sp
                            )
                        )
                        Text(
                            text = trackState.title,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = EmoSoftWhite,
                                fontWeight = FontWeight.Medium,
                                fontSize = 13.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = trackState.artist,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = EmoMutedGray,
                                fontSize = 11.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Controls
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "⏮",
                        style = MaterialTheme.typography.titleSmall.copy(color = EmoSoftWhite.copy(alpha = 0.7f)),
                        modifier = Modifier
                            .testTag("music_prev_btn")
                            .clickable(onClick = onPrevious)
                            .padding(4.dp)
                    )

                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.08f))
                            .border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape)
                            .clickable(onClick = onPlayPauseToggle)
                            .testTag("music_play_pause_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (trackState.isPlaying) "⏸" else "▶",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = primaryAccent,
                                fontSize = 12.sp
                            )
                        )
                    }

                    Text(
                        text = "⏭",
                        style = MaterialTheme.typography.titleSmall.copy(color = EmoSoftWhite.copy(alpha = 0.7f)),
                        modifier = Modifier
                            .testTag("music_next_btn")
                            .clickable(onClick = onNext)
                            .padding(4.dp)
                    )
                }
            }

            // Progress bar line
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.5.dp)
                    .clip(RoundedCornerShape(1.dp))
                    .background(Color.White.copy(alpha = 0.08f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(trackState.progress)
                        .fillMaxHeight()
                        .background(primaryAccent)
                )
            }
        }
    }
}
