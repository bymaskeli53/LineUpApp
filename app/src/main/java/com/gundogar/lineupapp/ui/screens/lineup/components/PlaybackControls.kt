package com.gundogar.lineupapp.ui.screens.lineup.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gundogar.lineupapp.R
import com.gundogar.lineupapp.data.model.PlaybackSpeed
import com.gundogar.lineupapp.data.model.PlaybackState
import com.gundogar.lineupapp.ui.theme.LineUpAppTheme
import com.gundogar.lineupapp.ui.theme.SecondaryGold

@Composable
fun PlaybackControls(
    playbackState: PlaybackState,
    frameCount: Int,
    onPlayPause: () -> Unit,
    onStop: () -> Unit,
    onSpeedChange: (PlaybackSpeed) -> Unit,
    modifier: Modifier = Modifier
) {
    var showSpeedMenu by remember { mutableStateOf(false) }

    val playButtonScale by animateFloatAsState(
        targetValue = if (playbackState.isPlaying) 1.1f else 1f,
        label = "playButtonScale"
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
        shadowElevation = 4.dp,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Progress indicator
            if (playbackState.isPlaying) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.tactic_frame_counter, playbackState.currentFrameIndex + 1, frameCount),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )

                    LinearProgressIndicator(
                        progress = { playbackState.progress },
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(4.dp)),
                        color = SecondaryGold,
                        trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Stop Button
                IconButton(
                    onClick = onStop,
                    enabled = playbackState.isPlaying,
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            color = if (playbackState.isPlaying) {
                                Color.Red.copy(alpha = 0.8f)
                            } else {
                                Color.Gray.copy(alpha = 0.3f)
                            },
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = stringResource(R.string.tactic_playback_stop),
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Play/Pause FAB
                val canPlay = frameCount >= 2 || playbackState.isPlaying
                FloatingActionButton(
                    onClick = { if (canPlay) onPlayPause() },
                    containerColor = if (canPlay) SecondaryGold else Color.Gray,
                    contentColor = if (canPlay) Color.Black else Color.White.copy(alpha = 0.5f),
                    modifier = Modifier
                        .size(56.dp)
                        .scale(playButtonScale)
                ) {
                    Icon(
                        imageVector = if (playbackState.isPlaying) {
                            Icons.Default.Pause
                        } else {
                            Icons.Default.PlayArrow
                        },
                        contentDescription = stringResource(if (playbackState.isPlaying) R.string.tactic_playback_pause else R.string.tactic_playback_play),
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Speed Selector
                Box {
                    Surface(
                        modifier = Modifier
                            .clickable { showSpeedMenu = true }
                            .padding(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = playbackState.playbackSpeed.label,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }

                    DropdownMenu(
                        expanded = showSpeedMenu,
                        onDismissRequest = { showSpeedMenu = false }
                    ) {
                        PlaybackSpeed.entries.forEach { speed ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = speed.label,
                                        fontWeight = if (speed == playbackState.playbackSpeed) {
                                            FontWeight.Bold
                                        } else {
                                            FontWeight.Normal
                                        },
                                        color = if (speed == playbackState.playbackSpeed) {
                                            SecondaryGold
                                        } else {
                                            MaterialTheme.colorScheme.onSurface
                                        }
                                    )
                                },
                                onClick = {
                                    onSpeedChange(speed)
                                    showSpeedMenu = false
                                }
                            )
                        }
                    }
                }
            }

            // Frame count info when not playing
            if (!playbackState.isPlaying && frameCount > 1) {
                Text(
                    text = stringResource(R.string.tactic_frames_count, frameCount),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Guidance messages
            when {
                frameCount == 0 -> {
                    Text(
                        text = stringResource(R.string.tactic_hint_add_first_frame),
                        fontSize = 12.sp,
                        color = SecondaryGold,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                frameCount == 1 -> {
                    Text(
                        text = stringResource(R.string.tactic_hint_add_next_frame),
                        fontSize = 12.sp,
                        color = SecondaryGold,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1E1E1E)
@Composable
private fun PlaybackControlsPreview() {
    LineUpAppTheme {
        PlaybackControls(
            playbackState = PlaybackState(
                isPlaying = false,
                currentFrameIndex = 0,
                playbackSpeed = PlaybackSpeed.NORMAL,
                progress = 0f
            ),
            frameCount = 3,
            onPlayPause = {},
            onStop = {},
            onSpeedChange = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1E1E1E)
@Composable
private fun PlaybackControlsPlayingPreview() {
    LineUpAppTheme {
        PlaybackControls(
            playbackState = PlaybackState(
                isPlaying = true,
                currentFrameIndex = 1,
                playbackSpeed = PlaybackSpeed.FAST,
                progress = 0.6f
            ),
            frameCount = 3,
            onPlayPause = {},
            onStop = {},
            onSpeedChange = {}
        )
    }
}
