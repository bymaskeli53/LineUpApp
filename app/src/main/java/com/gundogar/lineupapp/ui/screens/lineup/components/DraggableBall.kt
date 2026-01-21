package com.gundogar.lineupapp.ui.screens.lineup.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.gundogar.lineupapp.ui.theme.LineUpAppTheme
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun DraggableBall(
    xPercent: Float,
    yPercent: Float,
    pitchWidthPx: Float,
    pitchHeightPx: Float,
    isDraggable: Boolean = true,
    onPositionChange: (newXPercent: Float, newYPercent: Float) -> Unit,
    modifier: Modifier = Modifier
) {
    // Reset drag state when position changes externally
    var isDragging by remember(xPercent, yPercent) { mutableStateOf(false) }
    var dragOffsetX by remember(xPercent, yPercent) { mutableFloatStateOf(0f) }
    var dragOffsetY by remember(xPercent, yPercent) { mutableFloatStateOf(0f) }

    val currentXPercent by rememberUpdatedState(xPercent)
    val currentYPercent by rememberUpdatedState(yPercent)
    val currentPitchWidthPx by rememberUpdatedState(pitchWidthPx)
    val currentPitchHeightPx by rememberUpdatedState(pitchHeightPx)

    val scale by animateFloatAsState(
        targetValue = if (isDragging) 1.3f else 1f,
        animationSpec = spring(stiffness = 400f),
        label = "ballDragScale"
    )

    val ballSize = 24.dp

    Box(
        modifier = modifier
            .size(ballSize)
            .offset { IntOffset(dragOffsetX.roundToInt(), dragOffsetY.roundToInt()) }
            .scale(scale)
            .then(
                if (isDragging) {
                    Modifier.shadow(12.dp, androidx.compose.foundation.shape.CircleShape)
                } else {
                    Modifier.shadow(4.dp, androidx.compose.foundation.shape.CircleShape)
                }
            )
            .then(
                if (isDraggable) {
                    Modifier.pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = {
                                isDragging = true
                            },
                            onDragEnd = {
                                isDragging = false

                                val newXPercent = (currentXPercent + dragOffsetX / currentPitchWidthPx)
                                    .coerceIn(0.02f, 0.98f)
                                val newYPercent = (currentYPercent - dragOffsetY / currentPitchHeightPx)
                                    .coerceIn(0.02f, 0.98f)

                                onPositionChange(newXPercent, newYPercent)

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
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        FootballCanvas(modifier = Modifier.size(ballSize))
    }
}

@Composable
private fun FootballCanvas(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val radius = size.minDimension / 2

        // Ball gradient background (white to light gray)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White,
                    Color(0xFFF5F5F5),
                    Color(0xFFE0E0E0)
                ),
                center = Offset(centerX - radius * 0.2f, centerY - radius * 0.2f),
                radius = radius * 1.2f
            ),
            radius = radius,
            center = Offset(centerX, centerY)
        )

        // Draw pentagon pattern
        drawFootballPattern(centerX, centerY, radius)

        // Subtle border
        drawCircle(
            color = Color(0xFFBDBDBD),
            radius = radius,
            center = Offset(centerX, centerY),
            style = Stroke(width = 1.dp.toPx())
        )
    }
}

private fun DrawScope.drawFootballPattern(centerX: Float, centerY: Float, radius: Float) {
    val pentagonColor = Color(0xFF424242)

    // Center pentagon
    drawPentagon(
        center = Offset(centerX, centerY),
        size = radius * 0.35f,
        color = pentagonColor
    )

    // Surrounding pentagons (5 around the center)
    val outerRadius = radius * 0.6f
    for (i in 0 until 5) {
        val angle = Math.toRadians((i * 72.0 - 90.0))
        val px = centerX + outerRadius * cos(angle).toFloat()
        val py = centerY + outerRadius * sin(angle).toFloat()
        drawPentagon(
            center = Offset(px, py),
            size = radius * 0.25f,
            color = pentagonColor
        )
    }
}

private fun DrawScope.drawPentagon(center: Offset, size: Float, color: Color) {
    val path = Path()
    for (i in 0 until 5) {
        val angle = Math.toRadians((i * 72.0 - 90.0))
        val x = center.x + size * cos(angle).toFloat()
        val y = center.y + size * sin(angle).toFloat()
        if (i == 0) {
            path.moveTo(x, y)
        } else {
            path.lineTo(x, y)
        }
    }
    path.close()
    drawPath(path, color)
}

@Preview(showBackground = true, backgroundColor = 0xFF2E7D32)
@Composable
private fun DraggableBallPreview() {
    LineUpAppTheme {
        Box(
            modifier = Modifier.size(100.dp),
            contentAlignment = Alignment.Center
        ) {
            DraggableBall(
                xPercent = 0.5f,
                yPercent = 0.5f,
                pitchWidthPx = 300f,
                pitchHeightPx = 500f,
                onPositionChange = { _, _ -> }
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF2E7D32)
@Composable
private fun FootballPreview() {
    LineUpAppTheme {
        Box(
            modifier = Modifier.size(60.dp),
            contentAlignment = Alignment.Center
        ) {
            FootballCanvas(modifier = Modifier.size(48.dp))
        }
    }
}
