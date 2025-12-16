package com.gundogar.lineupapp.ui.screens.lineup.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gundogar.lineupapp.data.model.Player
import com.gundogar.lineupapp.data.model.Position
import com.gundogar.lineupapp.data.model.PositionRole
import com.gundogar.lineupapp.data.model.TeamConfig
import com.gundogar.lineupapp.ui.components.jersey.JerseyIcon
import com.gundogar.lineupapp.ui.theme.LineUpAppTheme
import kotlin.math.roundToInt

@Composable
fun DraggablePlayerJersey(
    position: Position,
    player: Player?,
    teamConfig: TeamConfig,
    pitchWidthPx: Float,
    pitchHeightPx: Float,
    onPositionDrag: (positionId: Int, newXPercent: Float, newYPercent: Float) -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    var isDragging by remember { mutableStateOf(false) }
    var dragOffsetX by remember { mutableFloatStateOf(0f) }
    var dragOffsetY by remember { mutableFloatStateOf(0f) }

    val scale by animateFloatAsState(
        targetValue = if (isDragging) 1.15f else 1f,
        animationSpec = spring(stiffness = 400f),
        label = "dragScale"
    )

    val jerseySize = 44.dp
    val jerseySizePx = with(density) { jerseySize.toPx() }

    Column(
        modifier = modifier
            .offset { IntOffset(dragOffsetX.roundToInt(), dragOffsetY.roundToInt()) }
            .scale(scale)
            .then(
                if (isDragging) {
                    Modifier.shadow(8.dp, RoundedCornerShape(8.dp))
                } else {
                    Modifier
                }
            )
            .pointerInput(position.id) {
                detectDragGestures(
                    onDragStart = {
                        isDragging = true
                    },
                    onDragEnd = {
                        isDragging = false

                        // Calculate current position in pixels
                        val currentXPx = position.xPercent * pitchWidthPx + dragOffsetX
                        val currentYPx = (1f - position.yPercent) * pitchHeightPx + dragOffsetY

                        // Convert back to percentages
                        val newXPercent = (currentXPx / pitchWidthPx).coerceIn(0.08f, 0.92f)
                        val newYPercent = (1f - (currentYPx / pitchHeightPx)).coerceIn(0.05f, 0.92f)

                        onPositionDrag(position.id, newXPercent, newYPercent)

                        // Reset offset after position is updated
                        dragOffsetX = 0f
                        dragOffsetY = 0f
                    },
                    onDragCancel = {
                        isDragging = false
                        dragOffsetX = 0f
                        dragOffsetY = 0f
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        dragOffsetX += dragAmount.x
                        dragOffsetY += dragAmount.y
                    }
                )
            }
            .pointerInput(position.id) {
                detectTapGestures(
                    onTap = { onClick() }
                )
            }
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Jersey with rating badge
        Box {
            JerseyIcon(
                primaryColor = teamConfig.primaryColor,
                secondaryColor = teamConfig.secondaryColor,
                style = teamConfig.jerseyStyle,
                number = player?.number,
                modifier = Modifier.size(jerseySize)
            )

            // Rating badge - positioned at top-right of jersey
            player?.rating?.let { rating ->
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 8.dp, y = (-4).dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(getRatingColor(rating))
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = formatRating(rating),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Text(
            text = player?.name?.takeIf { it.isNotBlank() } ?: "Tap to add",
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = if (player?.name?.isNotBlank() == true) FontWeight.Medium else FontWeight.Normal,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .widthIn(max = 70.dp)
                .padding(top = 2.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF2E7D32)
@Composable
private fun DraggablePlayerJerseyPreview() {
    LineUpAppTheme {
        DraggablePlayerJersey(
            position = Position(10, PositionRole.FORWARD, 0.5f, 0.75f),
            player = Player(10, "Messi", 10, 9.3),
            teamConfig = TeamConfig(),
            pitchWidthPx = 300f,
            pitchHeightPx = 500f,
            onPositionDrag = { _, _, _ -> },
            onClick = {}
        )
    }
}
