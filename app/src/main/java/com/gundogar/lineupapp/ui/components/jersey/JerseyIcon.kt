package com.gundogar.lineupapp.ui.components.jersey

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gundogar.lineupapp.data.model.JerseyStyle
import com.gundogar.lineupapp.ui.theme.DefaultJerseyPrimary
import com.gundogar.lineupapp.ui.theme.DefaultJerseySecondary
import com.gundogar.lineupapp.ui.theme.LineUpAppTheme

@Composable
fun JerseyIcon(
    primaryColor: Color,
    secondaryColor: Color,
    style: JerseyStyle,
    modifier: Modifier = Modifier,
    number: Int? = null
) {
    val textMeasurer = rememberTextMeasurer()

    Canvas(modifier = modifier.size(48.dp)) {
        val width = size.width
        val height = size.height

        // Create jersey path
        val jerseyPath = createJerseyPath(width, height)

        // Draw shadow
        drawPath(
            path = jerseyPath,
            color = Color.Black.copy(alpha = 0.3f),
            style = Fill
        )

        // Offset for 3D effect
        val offsetPath = Path().apply {
            addPath(jerseyPath, Offset(-2f, -2f))
        }

        // Draw jersey based on style
        clipPath(offsetPath) {
            when (style) {
                JerseyStyle.SOLID -> {
                    drawPath(offsetPath, primaryColor)
                }
                JerseyStyle.VERTICAL_STRIPES -> {
                    drawPath(offsetPath, primaryColor)
                    drawVerticalStripes(offsetPath, secondaryColor, width)
                }
                JerseyStyle.HORIZONTAL_STRIPES -> {
                    drawPath(offsetPath, primaryColor)
                    drawHorizontalStripes(offsetPath, secondaryColor, height)
                }
                JerseyStyle.HALVES -> {
                    drawHalves(offsetPath, primaryColor, secondaryColor, width)
                }
                JerseyStyle.SASH -> {
                    drawPath(offsetPath, primaryColor)
                    drawSash(offsetPath, secondaryColor, width, height)
                }
                JerseyStyle.HOOPS -> {
                    drawPath(offsetPath, primaryColor)
                    drawHoops(offsetPath, secondaryColor, height)
                }
            }
        }

        // Draw outline
        drawPath(
            path = offsetPath,
            color = Color.Black.copy(alpha = 0.5f),
            style = Stroke(width = 1.5f)
        )

        // Draw collar
        drawCollar(width, height, secondaryColor)

        // Draw number if present
        number?.let {
            val numberText = it.toString()
            val textStyle = TextStyle(
                color = if (style == JerseyStyle.SOLID && primaryColor.luminance() < 0.5f)
                    Color.White else Color.Black,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            val textLayoutResult = textMeasurer.measure(numberText, textStyle)
            drawText(
                textLayoutResult = textLayoutResult,
                topLeft = Offset(
                    (width - textLayoutResult.size.width) / 2 - 1,
                    height * 0.35f
                )
            )
        }
    }
}

private fun createJerseyPath(width: Float, height: Float): Path {
    return Path().apply {
        // Start from left shoulder
        moveTo(width * 0.25f, height * 0.1f)

        // Left sleeve top
        lineTo(width * 0.0f, height * 0.15f)

        // Left sleeve outer
        lineTo(width * 0.0f, height * 0.35f)

        // Left sleeve bottom
        lineTo(width * 0.2f, height * 0.35f)

        // Left body
        lineTo(width * 0.2f, height * 0.95f)

        // Bottom
        lineTo(width * 0.8f, height * 0.95f)

        // Right body
        lineTo(width * 0.8f, height * 0.35f)

        // Right sleeve bottom
        lineTo(width * 1.0f, height * 0.35f)

        // Right sleeve outer
        lineTo(width * 1.0f, height * 0.15f)

        // Right shoulder
        lineTo(width * 0.75f, height * 0.1f)

        // Collar right
        quadraticTo(
            width * 0.6f, height * 0.05f,
            width * 0.5f, height * 0.08f
        )

        // Collar left
        quadraticTo(
            width * 0.4f, height * 0.05f,
            width * 0.25f, height * 0.1f
        )

        close()
    }
}

private fun DrawScope.drawVerticalStripes(path: Path, color: Color, width: Float) {
    val stripeWidth = width / 8
    for (i in 0 until 8 step 2) {
        drawRect(
            color = color,
            topLeft = Offset(i * stripeWidth, 0f),
            size = androidx.compose.ui.geometry.Size(stripeWidth, size.height)
        )
    }
}

private fun DrawScope.drawHorizontalStripes(path: Path, color: Color, height: Float) {
    val stripeHeight = height / 6
    for (i in 0 until 6 step 2) {
        drawRect(
            color = color,
            topLeft = Offset(0f, i * stripeHeight),
            size = androidx.compose.ui.geometry.Size(size.width, stripeHeight)
        )
    }
}

private fun DrawScope.drawHalves(path: Path, primary: Color, secondary: Color, width: Float) {
    drawRect(
        color = primary,
        topLeft = Offset(0f, 0f),
        size = androidx.compose.ui.geometry.Size(width / 2, size.height)
    )
    drawRect(
        color = secondary,
        topLeft = Offset(width / 2, 0f),
        size = androidx.compose.ui.geometry.Size(width / 2, size.height)
    )
}

private fun DrawScope.drawSash(path: Path, color: Color, width: Float, height: Float) {
    val sashPath = Path().apply {
        moveTo(width * 0.7f, 0f)
        lineTo(width * 1.0f, 0f)
        lineTo(width * 0.3f, height)
        lineTo(0f, height)
        close()
    }
    drawPath(sashPath, color)
}

private fun DrawScope.drawHoops(path: Path, color: Color, height: Float) {
    val hoopHeight = height / 5
    for (i in 0 until 5 step 2) {
        drawRect(
            color = color,
            topLeft = Offset(0f, i * hoopHeight),
            size = androidx.compose.ui.geometry.Size(size.width, hoopHeight)
        )
    }
}

private fun DrawScope.drawCollar(width: Float, height: Float, color: Color) {
    val collarPath = Path().apply {
        moveTo(width * 0.35f, height * 0.08f)
        quadraticTo(
            width * 0.5f, height * 0.15f,
            width * 0.65f, height * 0.08f
        )
        quadraticTo(
            width * 0.5f, height * 0.12f,
            width * 0.35f, height * 0.08f
        )
        close()
    }
    drawPath(collarPath, color)
    drawPath(collarPath, Color.Black.copy(alpha = 0.3f), style = Stroke(width = 0.5f))
}

private fun Color.luminance(): Float {
    return (0.299f * red + 0.587f * green + 0.114f * blue)
}

@Preview(showBackground = true)
@Composable
private fun JerseyIconSolidPreview() {
    LineUpAppTheme {
        JerseyIcon(
            primaryColor = DefaultJerseyPrimary,
            secondaryColor = DefaultJerseySecondary,
            style = JerseyStyle.SOLID,
            number = 10
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun JerseyIconStripesPreview() {
    LineUpAppTheme {
        JerseyIcon(
            primaryColor = Color.Red,
            secondaryColor = Color.White,
            style = JerseyStyle.VERTICAL_STRIPES,
            number = 7
        )
    }
}
