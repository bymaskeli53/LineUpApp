package com.gundogar.lineupapp.ui.screens.customization.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gundogar.lineupapp.data.model.JerseyStyle
import com.gundogar.lineupapp.ui.theme.DefaultJerseyPrimary
import com.gundogar.lineupapp.ui.theme.DefaultJerseySecondary
import com.gundogar.lineupapp.ui.theme.LineUpAppTheme
import com.gundogar.lineupapp.ui.theme.SecondaryGold

@Composable
fun JerseyStylePicker(
    selectedStyle: JerseyStyle,
    primaryColor: Color,
    secondaryColor: Color,
    onStyleSelected: (JerseyStyle) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
    ) {
        items(JerseyStyle.entries) { style ->
            JerseyStyleOption(
                style = style,
                primaryColor = primaryColor,
                secondaryColor = secondaryColor,
                isSelected = selectedStyle == style,
                onClick = { onStyleSelected(style) }
            )
        }
    }
}

@Composable
private fun JerseyStyleOption(
    style: JerseyStyle,
    primaryColor: Color,
    secondaryColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) SecondaryGold else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(8.dp)
    ) {
        Canvas(
            modifier = Modifier.size(56.dp)
        ) {
            val width = size.width
            val height = size.height
            val jerseyPath = createMiniJerseyPath(width, height)

            // Draw jersey based on style
            when (style) {
                JerseyStyle.SOLID -> {
                    drawPath(jerseyPath, primaryColor, style = Fill)
                }
                JerseyStyle.VERTICAL_STRIPES -> {
                    drawPath(jerseyPath, primaryColor, style = Fill)
                    drawVerticalStripesMini(width, height, secondaryColor)
                }
                JerseyStyle.HORIZONTAL_STRIPES -> {
                    drawPath(jerseyPath, primaryColor, style = Fill)
                    drawHorizontalStripesMini(width, height, secondaryColor)
                }
                JerseyStyle.HALVES -> {
                    drawHalvesMini(width, height, primaryColor, secondaryColor)
                }
                JerseyStyle.SASH -> {
                    drawPath(jerseyPath, primaryColor, style = Fill)
                    drawSashMini(width, height, secondaryColor)
                }
                JerseyStyle.HOOPS -> {
                    drawPath(jerseyPath, primaryColor, style = Fill)
                    drawHoopsMini(width, height, secondaryColor)
                }
            }

            // Outline
            drawPath(
                jerseyPath,
                Color.Black.copy(alpha = 0.3f),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5f)
            )
        }

        Text(
            text = style.displayName,
            fontSize = 10.sp,
            textAlign = TextAlign.Center,
            color = if (isSelected) SecondaryGold else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

private fun createMiniJerseyPath(width: Float, height: Float): Path {
    return Path().apply {
        moveTo(width * 0.25f, height * 0.1f)
        lineTo(width * 0.0f, height * 0.15f)
        lineTo(width * 0.0f, height * 0.35f)
        lineTo(width * 0.2f, height * 0.35f)
        lineTo(width * 0.2f, height * 0.95f)
        lineTo(width * 0.8f, height * 0.95f)
        lineTo(width * 0.8f, height * 0.35f)
        lineTo(width * 1.0f, height * 0.35f)
        lineTo(width * 1.0f, height * 0.15f)
        lineTo(width * 0.75f, height * 0.1f)
        quadraticTo(width * 0.6f, height * 0.05f, width * 0.5f, height * 0.08f)
        quadraticTo(width * 0.4f, height * 0.05f, width * 0.25f, height * 0.1f)
        close()
    }
}

private fun DrawScope.drawVerticalStripesMini(width: Float, height: Float, color: Color) {
    val stripeWidth = width / 6
    for (i in 0 until 6 step 2) {
        drawRect(
            color = color,
            topLeft = Offset(i * stripeWidth, 0f),
            size = Size(stripeWidth, height)
        )
    }
}

private fun DrawScope.drawHorizontalStripesMini(width: Float, height: Float, color: Color) {
    val stripeHeight = height / 5
    for (i in 0 until 5 step 2) {
        drawRect(
            color = color,
            topLeft = Offset(0f, i * stripeHeight),
            size = Size(width, stripeHeight)
        )
    }
}

private fun DrawScope.drawHalvesMini(width: Float, height: Float, primary: Color, secondary: Color) {
    drawRect(
        color = primary,
        topLeft = Offset(0f, 0f),
        size = Size(width / 2, height)
    )
    drawRect(
        color = secondary,
        topLeft = Offset(width / 2, 0f),
        size = Size(width / 2, height)
    )
}

private fun DrawScope.drawSashMini(width: Float, height: Float, color: Color) {
    val sashPath = Path().apply {
        moveTo(width * 0.6f, 0f)
        lineTo(width, 0f)
        lineTo(width * 0.4f, height)
        lineTo(0f, height)
        close()
    }
    drawPath(sashPath, color)
}

private fun DrawScope.drawHoopsMini(width: Float, height: Float, color: Color) {
    val hoopHeight = height / 4
    for (i in 0 until 4 step 2) {
        drawRect(
            color = color,
            topLeft = Offset(0f, i * hoopHeight),
            size = Size(width, hoopHeight)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun JerseyStylePickerPreview() {
    LineUpAppTheme {
        JerseyStylePicker(
            selectedStyle = JerseyStyle.VERTICAL_STRIPES,
            primaryColor = DefaultJerseyPrimary,
            secondaryColor = DefaultJerseySecondary,
            onStyleSelected = {}
        )
    }
}
