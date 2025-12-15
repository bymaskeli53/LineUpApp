package com.example.lineupapp.ui.screens.lineup.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lineupapp.ui.theme.GrassGreen
import com.example.lineupapp.ui.theme.GrassGreenDark
import com.example.lineupapp.ui.theme.GrassGreenLight
import com.example.lineupapp.ui.theme.LineUpAppTheme

@Composable
fun FootballPitch(
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.65f)
    ) {
        val width = size.width
        val height = size.height
        val padding = 12.dp.toPx()
        val lineWidth = 2.5.dp.toPx()
        val lineColor = Color.White.copy(alpha = 0.95f)

        // Draw pitch background with gradient
        drawPitchBackground(width, height)

        // Draw grass stripes for realistic effect
        drawGrassStripes(width, height)

        // Draw all pitch markings
        drawPitchMarkings(width, height, padding, lineWidth, lineColor)

        // Draw goals
        drawGoals(width, height, padding, lineColor)
    }
}

private fun DrawScope.drawPitchBackground(width: Float, height: Float) {
    // Main gradient background
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                GrassGreenDark,
                GrassGreen,
                GrassGreenLight,
                GrassGreen,
                GrassGreenDark
            )
        ),
        size = Size(width, height)
    )
}

private fun DrawScope.drawGrassStripes(width: Float, height: Float) {
    val stripeCount = 12
    val stripeHeight = height / stripeCount

    for (i in 0 until stripeCount) {
        val alpha = if (i % 2 == 0) 0.08f else 0f
        drawRect(
            color = Color.Black.copy(alpha = alpha),
            topLeft = Offset(0f, i * stripeHeight),
            size = Size(width, stripeHeight)
        )
    }
}

private fun DrawScope.drawPitchMarkings(
    width: Float,
    height: Float,
    padding: Float,
    lineWidth: Float,
    lineColor: Color
) {
    val stroke = Stroke(width = lineWidth)
    val pitchWidth = width - (padding * 2)
    val pitchHeight = height - (padding * 2)

    // Outer boundary with rounded corners
    drawRoundRect(
        color = lineColor,
        topLeft = Offset(padding, padding),
        size = Size(pitchWidth, pitchHeight),
        cornerRadius = CornerRadius(4.dp.toPx()),
        style = stroke
    )

    // Center line
    drawLine(
        color = lineColor,
        start = Offset(padding, height / 2),
        end = Offset(width - padding, height / 2),
        strokeWidth = lineWidth
    )

    // Center circle
    val centerCircleRadius = pitchWidth * 0.18f
    drawCircle(
        color = lineColor,
        radius = centerCircleRadius,
        center = Offset(width / 2, height / 2),
        style = stroke
    )

    // Center dot
    drawCircle(
        color = lineColor,
        radius = 4.dp.toPx(),
        center = Offset(width / 2, height / 2),
        style = Fill
    )

    // Penalty areas
    val penaltyAreaWidth = pitchWidth * 0.55f
    val penaltyAreaHeight = pitchHeight * 0.16f

    // Top penalty area
    drawRoundRect(
        color = lineColor,
        topLeft = Offset((width - penaltyAreaWidth) / 2, padding),
        size = Size(penaltyAreaWidth, penaltyAreaHeight),
        cornerRadius = CornerRadius(2.dp.toPx()),
        style = stroke
    )

    // Bottom penalty area
    drawRoundRect(
        color = lineColor,
        topLeft = Offset((width - penaltyAreaWidth) / 2, height - padding - penaltyAreaHeight),
        size = Size(penaltyAreaWidth, penaltyAreaHeight),
        cornerRadius = CornerRadius(2.dp.toPx()),
        style = stroke
    )

    // Goal areas (6-yard box)
    val goalAreaWidth = pitchWidth * 0.25f
    val goalAreaHeight = pitchHeight * 0.06f

    // Top goal area
    drawRoundRect(
        color = lineColor,
        topLeft = Offset((width - goalAreaWidth) / 2, padding),
        size = Size(goalAreaWidth, goalAreaHeight),
        cornerRadius = CornerRadius(2.dp.toPx()),
        style = stroke
    )

    // Bottom goal area
    drawRoundRect(
        color = lineColor,
        topLeft = Offset((width - goalAreaWidth) / 2, height - padding - goalAreaHeight),
        size = Size(goalAreaWidth, goalAreaHeight),
        cornerRadius = CornerRadius(2.dp.toPx()),
        style = stroke
    )

    // Penalty spots
    val penaltySpotDistance = pitchHeight * 0.11f

    // Top penalty spot
    drawCircle(
        color = lineColor,
        radius = 3.dp.toPx(),
        center = Offset(width / 2, padding + penaltySpotDistance),
        style = Fill
    )

    // Bottom penalty spot
    drawCircle(
        color = lineColor,
        radius = 3.dp.toPx(),
        center = Offset(width / 2, height - padding - penaltySpotDistance),
        style = Fill
    )

    // Penalty arcs
    val penaltyArcRadius = pitchWidth * 0.12f

    // Top penalty arc
    drawArc(
        color = lineColor,
        startAngle = 35f,
        sweepAngle = 110f,
        useCenter = false,
        topLeft = Offset(
            width / 2 - penaltyArcRadius,
            padding + penaltySpotDistance - penaltyArcRadius
        ),
        size = Size(penaltyArcRadius * 2, penaltyArcRadius * 2),
        style = stroke
    )

    // Bottom penalty arc
    drawArc(
        color = lineColor,
        startAngle = 215f,
        sweepAngle = 110f,
        useCenter = false,
        topLeft = Offset(
            width / 2 - penaltyArcRadius,
            height - padding - penaltySpotDistance - penaltyArcRadius
        ),
        size = Size(penaltyArcRadius * 2, penaltyArcRadius * 2),
        style = stroke
    )

    // Corner arcs
    val cornerRadius = pitchWidth * 0.04f

    // Top-left corner
    drawArc(
        color = lineColor,
        startAngle = 0f,
        sweepAngle = 90f,
        useCenter = false,
        topLeft = Offset(padding - cornerRadius, padding - cornerRadius),
        size = Size(cornerRadius * 2, cornerRadius * 2),
        style = stroke
    )

    // Top-right corner
    drawArc(
        color = lineColor,
        startAngle = 90f,
        sweepAngle = 90f,
        useCenter = false,
        topLeft = Offset(width - padding - cornerRadius, padding - cornerRadius),
        size = Size(cornerRadius * 2, cornerRadius * 2),
        style = stroke
    )

    // Bottom-left corner
    drawArc(
        color = lineColor,
        startAngle = 270f,
        sweepAngle = 90f,
        useCenter = false,
        topLeft = Offset(padding - cornerRadius, height - padding - cornerRadius),
        size = Size(cornerRadius * 2, cornerRadius * 2),
        style = stroke
    )

    // Bottom-right corner
    drawArc(
        color = lineColor,
        startAngle = 180f,
        sweepAngle = 90f,
        useCenter = false,
        topLeft = Offset(width - padding - cornerRadius, height - padding - cornerRadius),
        size = Size(cornerRadius * 2, cornerRadius * 2),
        style = stroke
    )
}

private fun DrawScope.drawGoals(
    width: Float,
    height: Float,
    padding: Float,
    lineColor: Color
) {
    val goalWidth = width * 0.18f
    val goalDepth = 8.dp.toPx()
    val goalPostWidth = 3.dp.toPx()

    // Top goal
    val topGoalLeft = (width - goalWidth) / 2

    // Goal frame
    drawRoundRect(
        color = Color.White,
        topLeft = Offset(topGoalLeft, padding - goalDepth),
        size = Size(goalWidth, goalDepth + 2),
        cornerRadius = CornerRadius(2.dp.toPx())
    )

    // Goal net effect (top)
    drawGoalNet(topGoalLeft, padding - goalDepth, goalWidth, goalDepth)

    // Goal posts shadow
    drawRect(
        color = Color.Black.copy(alpha = 0.2f),
        topLeft = Offset(topGoalLeft + 2, padding - goalDepth + 2),
        size = Size(goalWidth - 4, goalDepth)
    )

    // Bottom goal
    val bottomGoalTop = height - padding

    // Goal frame
    drawRoundRect(
        color = Color.White,
        topLeft = Offset(topGoalLeft, bottomGoalTop - 2),
        size = Size(goalWidth, goalDepth + 2),
        cornerRadius = CornerRadius(2.dp.toPx())
    )

    // Goal net effect (bottom)
    drawGoalNet(topGoalLeft, bottomGoalTop, goalWidth, goalDepth)

    // Goal posts shadow
    drawRect(
        color = Color.Black.copy(alpha = 0.2f),
        topLeft = Offset(topGoalLeft + 2, bottomGoalTop),
        size = Size(goalWidth - 4, goalDepth - 2)
    )
}

private fun DrawScope.drawGoalNet(x: Float, y: Float, width: Float, depth: Float) {
    val netColor = Color.Gray.copy(alpha = 0.4f)
    val netSpacing = 4.dp.toPx()

    // Vertical lines
    var currentX = x + netSpacing
    while (currentX < x + width - netSpacing) {
        drawLine(
            color = netColor,
            start = Offset(currentX, y),
            end = Offset(currentX, y + depth),
            strokeWidth = 0.5f
        )
        currentX += netSpacing
    }

    // Horizontal lines
    var currentY = y + netSpacing
    while (currentY < y + depth) {
        drawLine(
            color = netColor,
            start = Offset(x + 2, currentY),
            end = Offset(x + width - 2, currentY),
            strokeWidth = 0.5f
        )
        currentY += netSpacing
    }
}

@Preview(showBackground = true)
@Composable
private fun FootballPitchPreview() {
    LineUpAppTheme {
        FootballPitch()
    }
}
