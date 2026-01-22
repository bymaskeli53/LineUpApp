package com.gundogar.lineupapp.ui.screens.lineup.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gundogar.lineupapp.R
import com.gundogar.lineupapp.data.model.BallPosition
import com.gundogar.lineupapp.data.model.FramePosition
import com.gundogar.lineupapp.data.model.TacticFrame
import com.gundogar.lineupapp.ui.theme.LineUpAppTheme
import com.gundogar.lineupapp.ui.theme.SecondaryGold
import java.util.UUID

@Composable
fun TimelineBar(
    frames: List<TacticFrame>,
    currentFrameIndex: Int,
    isPlaying: Boolean,
    onFrameSelected: (Int) -> Unit,
    onAddFrame: () -> Unit,
    onDuplicateFrame: (Int) -> Unit,
    onDeleteFrame: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    LaunchedEffect(currentFrameIndex) {
        if (currentFrameIndex in frames.indices) {
            listState.animateScrollToItem(currentFrameIndex)
        }
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        shadowElevation = 8.dp,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LazyRow(
                state = listState,
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                itemsIndexed(frames) { index, frame ->
                    FrameThumbnail(
                        frame = frame,
                        frameNumber = index + 1,
                        isSelected = index == currentFrameIndex,
                        isPlaying = isPlaying,
                        onClick = { onFrameSelected(index) },
                        onDuplicate = { onDuplicateFrame(index) },
                        onDelete = if (frames.size > 1) {
                            { onDeleteFrame(index) }
                        } else null
                    )
                }
            }

            // Add Frame Button
            IconButton(
                onClick = onAddFrame,
                enabled = !isPlaying,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(48.dp)
                    .background(
                        color = if (isPlaying) Color.Gray else SecondaryGold,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.tactic_add_frame),
                    tint = Color.Black
                )
            }
        }
    }
}

@Composable
private fun FrameThumbnail(
    frame: TacticFrame,
    frameNumber: Int,
    isSelected: Boolean,
    isPlaying: Boolean,
    onClick: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { if (!isPlaying) onClick() },
                        onLongPress = { if (!isPlaying) showMenu = true }
                    )
                }
        ) {
            // Frame number
            Text(
                text = "$frameNumber",
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) SecondaryGold else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )

            // Mini pitch thumbnail
            Box(
                modifier = Modifier
                    .size(width = 60.dp, height = 70.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF2E7D32))
                    .then(
                        if (isSelected) {
                            Modifier.border(2.dp, SecondaryGold, RoundedCornerShape(8.dp))
                        } else {
                            Modifier.border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        }
                    )
            ) {
                MiniPitchCanvas(
                    playerPositions = frame.playerPositions,
                    ballPosition = frame.ballPosition,
                    hasStrokes = frame.strokes.isNotEmpty(),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Context menu
        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.tactic_duplicate)) },
                onClick = {
                    showMenu = false
                    onDuplicate()
                },
                leadingIcon = {
                    Icon(Icons.Default.ContentCopy, contentDescription = null)
                }
            )
            if (onDelete != null) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.btn_delete), color = Color.Red) },
                    onClick = {
                        showMenu = false
                        onDelete()
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red)
                    }
                )
            }
        }
    }
}

@Composable
private fun MiniPitchCanvas(
    playerPositions: Map<Int, FramePosition>,
    ballPosition: BallPosition?,
    hasStrokes: Boolean,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.padding(2.dp)) {
        val width = size.width
        val height = size.height

        // Draw center line
        drawLine(
            color = Color.White.copy(alpha = 0.5f),
            start = Offset(0f, height / 2),
            end = Offset(width, height / 2),
            strokeWidth = 1f
        )

        // Draw center circle
        drawCircle(
            color = Color.White.copy(alpha = 0.5f),
            radius = width * 0.15f,
            center = Offset(width / 2, height / 2),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1f)
        )

        // Draw player dots
        playerPositions.values.forEach { position ->
            val x = position.xPercent * width
            val y = (1f - position.yPercent) * height
            drawCircle(
                color = Color.White,
                radius = 3.dp.toPx(),
                center = Offset(x, y)
            )
        }

        // Draw ball
        ballPosition?.let { ball ->
            if (ball.isVisible) {
                val ballX = ball.xPercent * width
                val ballY = (1f - ball.yPercent) * height
                drawCircle(
                    color = SecondaryGold,
                    radius = 2.5.dp.toPx(),
                    center = Offset(ballX, ballY)
                )
            }
        }

        // Indicator for drawings
        if (hasStrokes) {
            drawCircle(
                color = Color.Red,
                radius = 3.dp.toPx(),
                center = Offset(width - 6.dp.toPx(), 6.dp.toPx())
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1E1E1E)
@Composable
private fun TimelineBarPreview() {
    LineUpAppTheme {
        val sampleFrames = listOf(
            TacticFrame(
                id = UUID.randomUUID().toString(),
                index = 0,
                playerPositions = mapOf(
                    1 to FramePosition(1, 0.5f, 0.1f),
                    2 to FramePosition(2, 0.2f, 0.3f),
                    3 to FramePosition(3, 0.8f, 0.3f)
                ),
                ballPosition = BallPosition(0.5f, 0.5f),
                strokes = emptyList()
            ),
            TacticFrame(
                id = UUID.randomUUID().toString(),
                index = 1,
                playerPositions = mapOf(
                    1 to FramePosition(1, 0.5f, 0.2f),
                    2 to FramePosition(2, 0.3f, 0.4f),
                    3 to FramePosition(3, 0.7f, 0.4f)
                ),
                ballPosition = BallPosition(0.6f, 0.6f),
                strokes = emptyList()
            )
        )

        TimelineBar(
            frames = sampleFrames,
            currentFrameIndex = 0,
            isPlaying = false,
            onFrameSelected = {},
            onAddFrame = {},
            onDuplicateFrame = {},
            onDeleteFrame = {}
        )
    }
}
