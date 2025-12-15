package com.example.lineupapp.ui.screens.formation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lineupapp.data.model.Formation
import com.example.lineupapp.data.model.Position
import com.example.lineupapp.data.model.PositionRole
import com.example.lineupapp.ui.theme.GrassGreen
import com.example.lineupapp.ui.theme.GrassGreenDark
import com.example.lineupapp.ui.theme.LineUpAppTheme

@Composable
fun FormationPreview(
    formation: Formation,
    modifier: Modifier = Modifier,
    pitchColor: Color = GrassGreen,
    pitchColorDark: Color = GrassGreenDark,
    lineColor: Color = Color.White.copy(alpha = 0.5f),
    dotColor: Color = Color.White
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.7f)
    ) {
        val width = size.width
        val height = size.height

        // Draw pitch background
        drawRect(color = pitchColor)

        // Draw subtle grass stripes
        val stripeCount = 8
        val stripeHeight = height / stripeCount
        for (i in 0 until stripeCount) {
            if (i % 2 == 0) {
                drawRect(
                    color = pitchColorDark.copy(alpha = 0.3f),
                    topLeft = Offset(0f, i * stripeHeight),
                    size = Size(width, stripeHeight)
                )
            }
        }

        // Draw simplified pitch lines
        drawPitchLines(lineColor, width, height)

        // Draw position dots
        formation.positions.forEach { position ->
            val x = position.xPercent * width
            val y = (1f - position.yPercent) * height

            // Draw outer glow
            drawCircle(
                color = dotColor.copy(alpha = 0.3f),
                radius = 8.dp.toPx(),
                center = Offset(x, y)
            )

            // Draw dot
            drawCircle(
                color = dotColor,
                radius = 5.dp.toPx(),
                center = Offset(x, y)
            )
        }
    }
}

private fun DrawScope.drawPitchLines(lineColor: Color, width: Float, height: Float) {
    val strokeWidth = 1.5.dp.toPx()
    val stroke = Stroke(width = strokeWidth)

    // Outer boundary
    drawRect(
        color = lineColor,
        topLeft = Offset(4.dp.toPx(), 4.dp.toPx()),
        size = Size(width - 8.dp.toPx(), height - 8.dp.toPx()),
        style = stroke
    )

    // Center line
    drawLine(
        color = lineColor,
        start = Offset(4.dp.toPx(), height / 2),
        end = Offset(width - 4.dp.toPx(), height / 2),
        strokeWidth = strokeWidth
    )

    // Center circle
    val centerCircleRadius = width * 0.15f
    drawCircle(
        color = lineColor,
        radius = centerCircleRadius,
        center = Offset(width / 2, height / 2),
        style = stroke
    )

    // Center dot
    drawCircle(
        color = lineColor,
        radius = 2.dp.toPx(),
        center = Offset(width / 2, height / 2)
    )

    // Penalty areas (top)
    val penaltyAreaWidth = width * 0.44f
    val penaltyAreaHeight = height * 0.12f
    drawRect(
        color = lineColor,
        topLeft = Offset((width - penaltyAreaWidth) / 2, 4.dp.toPx()),
        size = Size(penaltyAreaWidth, penaltyAreaHeight),
        style = stroke
    )

    // Penalty areas (bottom)
    drawRect(
        color = lineColor,
        topLeft = Offset((width - penaltyAreaWidth) / 2, height - penaltyAreaHeight - 4.dp.toPx()),
        size = Size(penaltyAreaWidth, penaltyAreaHeight),
        style = stroke
    )

    // Goal areas (top)
    val goalAreaWidth = width * 0.2f
    val goalAreaHeight = height * 0.05f
    drawRect(
        color = lineColor,
        topLeft = Offset((width - goalAreaWidth) / 2, 4.dp.toPx()),
        size = Size(goalAreaWidth, goalAreaHeight),
        style = stroke
    )

    // Goal areas (bottom)
    drawRect(
        color = lineColor,
        topLeft = Offset((width - goalAreaWidth) / 2, height - goalAreaHeight - 4.dp.toPx()),
        size = Size(goalAreaWidth, goalAreaHeight),
        style = stroke
    )
}

@Preview(showBackground = true)
@Composable
private fun FormationPreviewPreview() {
    LineUpAppTheme {
        FormationPreview(
            formation = Formation(
                id = "442",
                name = "4-4-2",
                displayName = "4-4-2 Classic",
                positions = listOf(
                    Position(1, PositionRole.GOALKEEPER, 0.5f, 0.08f),
                    Position(2, PositionRole.DEFENDER, 0.15f, 0.25f),
                    Position(3, PositionRole.DEFENDER, 0.38f, 0.22f),
                    Position(4, PositionRole.DEFENDER, 0.62f, 0.22f),
                    Position(5, PositionRole.DEFENDER, 0.85f, 0.25f),
                    Position(6, PositionRole.MIDFIELDER, 0.12f, 0.50f),
                    Position(7, PositionRole.MIDFIELDER, 0.37f, 0.47f),
                    Position(8, PositionRole.MIDFIELDER, 0.63f, 0.47f),
                    Position(9, PositionRole.MIDFIELDER, 0.88f, 0.50f),
                    Position(10, PositionRole.FORWARD, 0.35f, 0.75f),
                    Position(11, PositionRole.FORWARD, 0.65f, 0.75f)
                )
            )
        )
    }
}
