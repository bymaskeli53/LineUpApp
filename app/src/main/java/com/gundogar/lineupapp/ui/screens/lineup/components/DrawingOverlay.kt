package com.gundogar.lineupapp.ui.screens.lineup.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import com.gundogar.lineupapp.data.model.DrawingStroke
import com.gundogar.lineupapp.data.model.DrawingTool
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun DrawingOverlay(
    strokes: List<DrawingStroke>,
    isDrawingMode: Boolean,
    currentTool: DrawingTool,
    currentColor: Color,
    currentStrokeWidth: Float,
    onStrokeComplete: (DrawingStroke) -> Unit,
    onEraseStroke: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Track current drawing in progress
    var currentPoints by remember { mutableStateOf<List<Offset>>(emptyList()) }
    var startPoint by remember { mutableStateOf<Offset?>(null) }

    Canvas(
        modifier = modifier
            .pointerInput(isDrawingMode, currentTool, currentColor, currentStrokeWidth) {
                if (!isDrawingMode) return@pointerInput

                if (currentTool == DrawingTool.ERASER) {
                    detectTapGestures { tapOffset ->
                        // Convert to percentage
                        val percentOffset = Offset(
                            tapOffset.x / size.width,
                            tapOffset.y / size.height
                        )
                        // Find stroke to erase
                        val strokeToErase = findStrokeAtPoint(strokes, percentOffset, size.width.toFloat(), size.height.toFloat())
                        strokeToErase?.let { onEraseStroke(it.id) }
                    }
                } else {
                    detectDragGestures(
                        onDragStart = { offset ->
                            val percentOffset = Offset(
                                offset.x / size.width,
                                offset.y / size.height
                            )
                            startPoint = percentOffset
                            currentPoints = listOf(percentOffset)
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            val percentOffset = Offset(
                                change.position.x / size.width,
                                change.position.y / size.height
                            )

                            when (currentTool) {
                                DrawingTool.PEN -> {
                                    currentPoints = currentPoints + percentOffset
                                }
                                DrawingTool.ARROW, DrawingTool.LINE, DrawingTool.DASHED_LINE -> {
                                    // For shapes, we only need start and end
                                    startPoint?.let { start ->
                                        currentPoints = listOf(start, percentOffset)
                                    }
                                }
                                DrawingTool.ERASER -> {
                                    // Find and erase strokes along the drag path
                                    val strokeToErase = findStrokeAtPoint(strokes, percentOffset, size.width.toFloat(), size.height.toFloat())
                                    strokeToErase?.let { onEraseStroke(it.id) }
                                }
                            }
                        },
                        onDragEnd = {
                            if (currentPoints.isNotEmpty() && currentTool != DrawingTool.ERASER) {
                                val stroke = DrawingStroke(
                                    tool = currentTool,
                                    points = currentPoints,
                                    color = currentColor,
                                    strokeWidth = currentStrokeWidth
                                )
                                onStrokeComplete(stroke)
                            }
                            currentPoints = emptyList()
                            startPoint = null
                        },
                        onDragCancel = {
                            currentPoints = emptyList()
                            startPoint = null
                        }
                    )
                }
            }
    ) {
        // Draw all existing strokes
        strokes.forEach { stroke ->
            drawStroke(stroke, size.width, size.height)
        }

        // Draw current stroke in progress
        if (currentPoints.isNotEmpty() && isDrawingMode) {
            val tempStroke = DrawingStroke(
                tool = currentTool,
                points = currentPoints,
                color = currentColor,
                strokeWidth = currentStrokeWidth
            )
            drawStroke(tempStroke, size.width, size.height)
        }
    }
}

private fun DrawScope.drawStroke(stroke: DrawingStroke, canvasWidth: Float, canvasHeight: Float) {
    if (stroke.points.isEmpty()) return

    // Convert percentage points to actual coordinates
    val actualPoints = stroke.points.map { point ->
        Offset(point.x * canvasWidth, point.y * canvasHeight)
    }

    when (stroke.tool) {
        DrawingTool.PEN -> drawPenStroke(actualPoints, stroke.color, stroke.strokeWidth)
        DrawingTool.ARROW -> drawArrowStroke(actualPoints, stroke.color, stroke.strokeWidth)
        DrawingTool.LINE -> drawLineStroke(actualPoints, stroke.color, stroke.strokeWidth)
        DrawingTool.DASHED_LINE -> drawDashedLineStroke(actualPoints, stroke.color, stroke.strokeWidth)
        DrawingTool.ERASER -> { /* Eraser doesn't draw */ }
    }
}

private fun DrawScope.drawPenStroke(points: List<Offset>, color: Color, strokeWidth: Float) {
    if (points.size < 2) return

    val path = Path().apply {
        moveTo(points.first().x, points.first().y)
        for (i in 1 until points.size) {
            lineTo(points[i].x, points[i].y)
        }
    }

    drawPath(
        path = path,
        color = color,
        style = Stroke(
            width = strokeWidth,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    )
}

private fun DrawScope.drawLineStroke(points: List<Offset>, color: Color, strokeWidth: Float) {
    if (points.size < 2) return

    val start = points.first()
    val end = points.last()

    drawLine(
        color = color,
        start = start,
        end = end,
        strokeWidth = strokeWidth,
        cap = StrokeCap.Round
    )
}

private fun DrawScope.drawArrowStroke(points: List<Offset>, color: Color, strokeWidth: Float) {
    if (points.size < 2) return

    val start = points.first()
    val end = points.last()

    // Draw main line
    drawLine(
        color = color,
        start = start,
        end = end,
        strokeWidth = strokeWidth,
        cap = StrokeCap.Round
    )

    // Calculate arrow head
    val angle = atan2(end.y - start.y, end.x - start.x)
    val arrowLength = strokeWidth * 4
    val arrowAngle = Math.toRadians(25.0).toFloat()

    val arrow1 = Offset(
        end.x - arrowLength * cos(angle - arrowAngle),
        end.y - arrowLength * sin(angle - arrowAngle)
    )
    val arrow2 = Offset(
        end.x - arrowLength * cos(angle + arrowAngle),
        end.y - arrowLength * sin(angle + arrowAngle)
    )

    // Draw arrow head
    drawLine(
        color = color,
        start = end,
        end = arrow1,
        strokeWidth = strokeWidth,
        cap = StrokeCap.Round
    )
    drawLine(
        color = color,
        start = end,
        end = arrow2,
        strokeWidth = strokeWidth,
        cap = StrokeCap.Round
    )
}

private fun DrawScope.drawDashedLineStroke(points: List<Offset>, color: Color, strokeWidth: Float) {
    if (points.size < 2) return

    val start = points.first()
    val end = points.last()

    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 10f), 0f)

    drawLine(
        color = color,
        start = start,
        end = end,
        strokeWidth = strokeWidth,
        cap = StrokeCap.Round,
        pathEffect = pathEffect
    )
}

private fun findStrokeAtPoint(
    strokes: List<DrawingStroke>,
    point: Offset,
    canvasWidth: Float,
    canvasHeight: Float
): DrawingStroke? {
    val hitRadius = 0.02f // 2% of canvas size for hit detection

    return strokes.lastOrNull { stroke ->
        stroke.points.any { strokePoint ->
            val distance = sqrt(
                (point.x - strokePoint.x).pow(2) + (point.y - strokePoint.y).pow(2)
            )
            distance < hitRadius
        }
    }
}
